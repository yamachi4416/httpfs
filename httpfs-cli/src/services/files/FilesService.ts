import { ApiEndpoint, MaximumUploadSize } from '../../config';
import { FsItem } from './FsItem';
import { HttpException } from '../HttpException';
import { MultiStatus } from './MultiStatus';

async function fetchApi<T>(
  path: string[],
  options: RequestInit = {}
): Promise<T> {
  const endPoint = `${ApiEndpoint}/${path.join('/')}`;
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

export async function uploadFiles({
  path = [],
  files = new FileList(),
  callback = () => {},
}: {
  path: string[];
  files: FileList;
  callback: (item: FsItem, err?: Error) => void;
}): Promise<MultiStatus[]> {
  const groups = [[]];

  let size = 0;
  let chunks = groups[0];
  for (const file of Array.from(files)) {
    if (size + file.size >= MaximumUploadSize) {
      size = file.size;
      chunks = [file];
      groups.push(chunks);
    } else {
      size += file.size;
      chunks.push(file);
    }
  }

  const uploads = groups
    .filter(chunks => chunks.length > 0)
    .map(async chunks => {
      const form = new FormData();
      chunks.forEach(file => form.append('files', file));

      return fetchApi<MultiStatus[]>(path, {
        method: 'put',
        body: form,
      })
        .then(results => {
          return results.map((result, idx) => {
            const msts = MultiStatus.fromJson(result, path);
            if (msts.isError && msts.item == null) {
              msts.item = FsItem.fromFile(chunks[idx], path);
            }
            callback(msts.item, msts.toException());
            return msts;
          });
        })
        .catch(err => {
          return chunks.map(file => {
            const msts = MultiStatus.fromFileWithError(file, err, path);
            callback(msts.item, msts.toException());
            return msts;
          });
        });
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
