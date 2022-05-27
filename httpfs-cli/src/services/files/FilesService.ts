import { configAsync, getConfig } from './ConfigService';
import { FsItem } from './FsItem';
import { HttpException } from '../HttpException';
import { MultiStatus } from './MultiStatus';

async function fetchApi<T>(
  path: string[],
  options: RequestInit = {}
): Promise<T> {
  const { filesApiEndpoint } = await configAsync();
  const endPoint = `${filesApiEndpoint}/${path
    .map(p => encodeURIComponent(p))
    .join('/')}`;
  const response = await fetch(endPoint, options);
  if (response.status >= 400) {
    throw new HttpException(
      response.status,
      response.statusText,
      await response.text()
    );
  }
  return await response.json();
}

export async function fetchDirectoryItems(path: string[]): Promise<FsItem[]> {
  return await fetchApi<object[]>(path, {
    method: 'get',
    cache: 'no-cache',
    headers: {
      'Content-Type': 'application/json',
    },
  }).then(items => items.map(item => FsItem.fromJson(item, path)));
}

function toFilesChunks(files: File[], maxSize: number) {
  const chunks: File[][] = [[]];

  let size = 0;
  for (const file of Array.from(files)) {
    if (size + file.size >= maxSize) {
      size = file.size;
      chunks.unshift([file]);
    } else {
      size += file.size;
      chunks[0].push(file);
    }
  }

  return chunks.filter(chunk => chunk.length > 0).reverse();
}

function toFileUplaodForm(files: File[], maxSize: number) {
  const uploads: File[] = [];
  const ignores: File[] = [];
  const form = new FormData();

  for (const file of files) {
    if (file.size >= maxSize) {
      ignores.push(file);
    } else {
      uploads.push(file);
      form.append('files', file);
    }
  }

  return { form, uploads, ignores };
}

export async function uploadFiles({
  path = [],
  files = new FileList(),
  callback = () => {},
}: {
  path: string[];
  files: FileList;
  callback: (item: FsItem, err?: Error) => void;
}): Promise<MultiStatus[]> {
  const { maxFileSize } = getConfig();
  const chunks = toFilesChunks(Array.from(files), maxFileSize);

  const onErrorHandler = (files: File[], err: Error) => {
    return files.map(file => {
      const msts = MultiStatus.fromFileWithError(file, err, path);
      callback(msts.item, msts.toException());
      return msts;
    });
  };

  const uploads = chunks
    .map(files => toFileUplaodForm(files, maxFileSize))
    .map(async ({ form, uploads, ignores }) => {
      if (ignores.length) {
        return onErrorHandler(ignores, HttpException.payloadTooLarge());
      }

      return fetchApi<MultiStatus[]>(path, {
        method: 'put',
        body: form,
      })
        .then(results => {
          return results.map((result, idx) => {
            const msts = MultiStatus.fromJson(result, path);
            if (msts.isError && msts.item == null) {
              msts.item = FsItem.fromFile(uploads[idx], path);
            }
            callback(msts.item, msts.toException());
            return msts;
          });
        })
        .catch(err => onErrorHandler(uploads, err));
    });

  return (await Promise.all(uploads)).reduce(
    (result, items) => [...result, ...items],
    []
  );
}

export async function createDirectory({
  path,
  dirname,
}: {
  path: string[];
  dirname: string;
}): Promise<FsItem> {
  return await fetchApi<object>(path, {
    method: 'post',
    headers: {
      'Content-Type': 'application/json',
      'X-Method': 'MKCOL',
    },
    body: JSON.stringify({
      dirname,
    }),
  }).then(item => FsItem.fromJson(item, path));
}

export async function moveItems({
  path = [],
  destination,
  items = [],
}: {
  path: string[];
  destination: string;
  items: FsItem[];
}): Promise<MultiStatus[]> {
  return await fetchApi<MultiStatus[]>(path, {
    method: 'post',
    headers: {
      'Content-Type': 'application/json',
      'X-Method': 'MOVE',
    },
    body: JSON.stringify({
      destination: destination || '/',
      names: items.map(item => item.name),
      overwrite: true,
    }),
  }).then(results => {
    return results.map((result, idx) => {
      const msts = MultiStatus.fromJson(result, path);
      if (msts.isError && msts.item == null) {
        msts.item = items[idx];
      }
      return msts;
    });
  });
}

export async function deleteItems(
  path: string[],
  items: FsItem[]
): Promise<MultiStatus[]> {
  return await fetchApi<MultiStatus[]>(path, {
    method: 'delete',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      names: items.map(item => item.name),
    }),
  }).then(results => {
    return results.map((result, idx) => {
      const msts = MultiStatus.fromJson(result, path);
      if (msts.isError && msts.item == null) {
        msts.item = items[idx];
      }
      return msts;
    });
  });
}
