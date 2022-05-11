import { AppConfig } from '../config';

const nop = () => {};

const { ApiEndpoint, MaximumUploadSize } = AppConfig;

export class FsItem {
  readonly path: string;
  readonly paths: string[];
  readonly endpoint: string;
  readonly directory: boolean = false;
  readonly name: string;
  readonly mimeType: string;
  readonly lastModified?: Date;
  readonly creationTime?: Date;
  readonly size: number = 0;
  readonly writable: boolean = false;
  readonly parent: string;

  selected: boolean = false;

  constructor(props: FsItem) {
    Object.assign(this, props);
    this.endpoint = `${ApiEndpoint}${this.path}`;
  }

  static fromJson(json: any, path: string[]): FsItem {
    const { lastModified, creationTime, ...props } = json;
    const item = props as FsItem;
    const paths = [...path].filter(p => p);
    return new FsItem({
      ...item,
      lastModified: lastModified && new Date(lastModified),
      creationTime: creationTime && new Date(creationTime),
      path: `/${[...paths, item.name].join('/')}`,
      paths: [...paths, item.name],
      parent: `/${paths.join('/')}`,
    } as FsItem);
  }

  static fromFile(file: File, path: string[]): FsItem {
    const obj = { name: file.name, mimeType: file.type, size: file.size };
    return this.fromJson(obj, path);
  }
}

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
  callback: (items: FsItem[]) => void = nop
): Promise<FsItem[]> {
  const groups = [[]];

  let size = 0;
  let chunks = groups[0];
  for (const file of Array.from(files)) {
    if (size + file.size >= MaximumUploadSize) {
      size = 0;
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
      return fetchApi<object[]>(path, { method: 'put', body: form })
        .then(items => items.map(item => FsItem.fromJson(item, path)))
        .then(items => {
          callback(items);
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
