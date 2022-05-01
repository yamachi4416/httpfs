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
  constructor(item: object, path: string[]) {
    Object.assign(this, item);
    this.path = `/${[...path, this.name].join("/")}`
    this.endpoint = `${endpoint}${this.path}`
  }
}

export class HttpException extends Error {
  status: number;
  constructor(status: number, message: string = null) {
    super(message);
    this.status = status;
  }
}

const fetchApi = async <T>(
  path: string[],
  options: RequestInit = {},
  action: string = null
): Promise<T> => {
  const query = action ? `?action=${action}` : "";
  const endPoint = `${endpoint}/${path.join("/")}${query}`;
  const response = await fetch(endPoint, options);
  if (response.status >= 400) {
    throw new HttpException(response.status, response.statusText);
  }
  return await response.json();
};

export const fetchDirectoryItems = async (
  path: string[]
): Promise<FsItem[]> => {
  return await fetchApi<object[]>(path, {
    method: "get",
    cache: "no-cache",
    headers: {
      "Content-Type": "application/json",
    },
  }).then((items) => items.map((item) => new FsItem(item, path)));
};

export const uploadFiles = async (
  path: string[],
  files: FileList,
  callback: (item: FsItem) => void = nop
): Promise<FsItem[]> => {
  const uploads = Array.from(files)
    .map((file) => {
      const form = new FormData();
      form.append("files", file);
      return form;
    })
    .map(async (form) =>
      fetchApi<object>(path, { method: "put", body: form }, "upload")
        .then((item) => new FsItem(item, path))
        .then((item) => {
          callback(item);
          return item;
        })
    );

  return Promise.all(uploads);
};

export const createDirectory = async (
  path: string[],
  dirname: string
): Promise<FsItem> => {
  const form = new FormData();
  form.append("dirname", dirname);
  return await fetchApi<object>(
    path,
    { method: "put", body: form },
    "mkdir"
  ).then((item) => new FsItem(item, path));
};

export default {
  fetchDirectoryItems,
  uploadFiles,
  createDirectory,
};
