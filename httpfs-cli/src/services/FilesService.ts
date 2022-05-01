const nop = function (...args: any) {};

export interface FsItem {
  directory: boolean;
  name: string;
  mimeType: string;
  lastModified: string;
  creationTime: string;
  size: number;
  writable: boolean;
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
  const endPoint = `/api/files/${path.join("/")}${query}`;
  const response = await fetch(endPoint, options);
  if (response.status >= 400) {
    throw new HttpException(response.status, response.statusText);
  }
  return await response.json();
};

export const fetchDirectoryItems = async (
  path: string[]
): Promise<FsItem[]> => {
  return await fetchApi(path, {
    method: "get",
    cache: "no-cache",
    headers: {
      "Content-Type": "application/json",
    },
  });
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
      fetchApi<FsItem>(path, { method: "put", body: form }, "upload").then(
        (item) => {
          callback(item);
          return item;
        }
      )
    );

  return Promise.all(uploads);
};

export const createDirectory = async (
  path: string[],
  dirname: string
): Promise<FsItem> => {
  const form = new FormData();
  form.append("dirname", dirname);
  return await fetchApi<FsItem>(path, { method: "put", body: form }, "mkdir");
};

export const downloadFile = (path: string[], name: string): void => {
  window.open(`/api/files/${path.join("/")}/${name}`, "_blank");
};

export default {
  fetchDirectoryItems,
  uploadFiles,
  createDirectory,
  downloadFile,
};
