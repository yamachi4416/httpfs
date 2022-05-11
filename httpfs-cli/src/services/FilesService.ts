import { ApiEndpoint, MaximumUploadSize } from '../config';
import { FsItem } from './FsItem';

export class HttpException extends Error {
  status: number;

  constructor(status: number, message: string = null) {
    super(message);
    this.status = status;
  }
}

async function fetchApi<T>(
  path: string[],
  options: RequestInit = {}
): Promise<T> {
  const endPoint = `${ApiEndpoint}/${path.join('/')}`;
  const response = await fetch(endPoint, options);
  if (response.status >= 400) {
    throw new HttpException(response.status, response.statusText);
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

export async function uploadFiles(
  path: string[],
  files: FileList,
  callback: (items: FsItem[], err?: Error) => void = () => {}
): Promise<FsItem[]> {
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

      return fetchApi<object[]>(path, {
        method: 'put',
        body: form,
      })
        .then(files => {
          const items = files.map(item => FsItem.fromJson(item, path));
          callback(items);
          return items;
        })
        .catch(err => {
          const items = chunks.map(file => FsItem.fromFile(file, path));
          callback(items, err);
          return items;
        });
    });

  return (await Promise.all(uploads)).reduce(
    (result, items) => [...result, ...items],
    []
  );
}

export async function createDirectory(
  path: string[],
  dirname: string
): Promise<FsItem> {
  const form = new FormData();
  form.append('dirname', dirname);
  return await fetchApi<object>(path, { method: 'post', body: form }).then(
    item => FsItem.fromJson(item, path)
  );
}

export async function deleteItems(
  path: string[],
  items: FsItem[]
): Promise<string[]> {
  return await fetchApi<string[]>(path, {
    method: 'delete',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(items.map(item => item.name)),
  });
}

export default {
  fetchDirectoryItems,
  uploadFiles,
  createDirectory,
  deleteItems,
};
