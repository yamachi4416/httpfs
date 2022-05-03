const nop = function (...args: any) {};
const endpoint = "/api/files";

export class FsItem {
  path: string;
  endpoint: string;
  directory: boolean;
  name: string;
  mimeType: string;
  lastModified: string;
  creationTime: string;
  size: number;
  writable: boolean;
  selected: boolean;
  constructor(item: object, path: string[]) {
    Object.assign(this, item);
    this.path = `/${[...path, this.name].join("/")}`;
    this.endpoint = `${endpoint}${this.path}`;
    this.selected = false;
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
  options: RequestInit = {},
  action: string = null
): Promise<T> {
  const query = action ? `?action=${action}` : "";
  const endPoint = `${endpoint}/${path.join("/")}${query}`;
  const response = await fetch(endPoint, options);
  if (response.status >= 400) {
    throw new HttpException(response.status, response.statusText);
  }
  return await response.json();
}

export async function fetchDirectoryItems(path: string[]): Promise<FsItem[]> {
  return await fetchApi<object[]>(path, {
    method: "get",
    cache: "no-cache",
    headers: {
      "Content-Type": "application/json",
    },
  }).then((items) => items.map((item) => new FsItem(item, path)));
}

export async function uploadFiles(
  path: string[],
  files: FileList,
  callback: (items: FsItem[]) => void = nop
): Promise<FsItem[]> {
  const maxSize = 1048576; //TODO
  const groups = [[]];

  let size = 0;
  let chunks = groups[0];
  for (let file of Array.from(files)) {
    if (size + file.size >= maxSize) {
      size = 0;
      chunks = [file];
      groups.push(chunks);
    } else {
      size += file.size;
      chunks.push(file);
    }
  }

  const uploads = groups
    .filter((chunks) => chunks.length > 0)
    .map(async (chunks) => {
      const form = new FormData();
      chunks.forEach((file) => form.append("files", file));
      return fetchApi<object[]>(path, { method: "put", body: form }, "upload")
        .then((items) => items.map((item) => new FsItem(item, path)))
        .then((items) => {
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
  form.append("dirname", dirname);
  return await fetchApi<object>(
    path,
    { method: "put", body: form },
    "mkdir"
  ).then((item) => new FsItem(item, path));
}

export async function deleteItems(
  path: string[],
  items: FsItem[]
): Promise<string[]> {
  const form = new FormData();
  for (let item of items) {
    form.append("name", item.name);
  }
  return await fetchApi<string[]>(path, {
    method: "delete",
    body: form,
  });
}

export default {
  fetchDirectoryItems,
  uploadFiles,
  createDirectory,
  deleteItems,
};
