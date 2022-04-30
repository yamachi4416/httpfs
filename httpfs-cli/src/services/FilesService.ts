const nop = function (...args: any) {};

export const fetchDirectoryItems = async (path: string[]): Promise<object[]> => {
  return await fetch(`/api/files/${path.join("/")}`, {
    method: "get",
    cache: "no-cache",
    headers: {
      "Content-Type": "application/json",
    },
  }).then((res) => res.json());
};

export const uploadFiles = async (path: string[], files: FileList, callback = nop) => {
  const uploads = Array.from(files)
    .map((file) => {
      const form = new FormData();
      form.append("files", file);
      return form;
    })
    .map(async (form) =>
      fetch(`/api/files/${path.join("/")}?action=upload`, {
        method: "put",
        body: form,
      }).then((res) => {
        const item = res.json();
        callback(item);
        return item;
      })
    );

  return Promise.all(uploads);
};

export const createDirectory = async (path: string[], dirname: string) => {
  const form = new FormData();
  form.append("dirname", dirname);
  return await fetch(`/api/files/${path.join("/")}?action=mkdir`, {
    method: "put",
    body: form,
  }).then((res) => res.json());
};

export const downloadFile = (path: string[], name: string) => {
  window.open(`/api/files/${path.join("/")}/${name}`, "_blank");
};

export default {
  fetchDirectoryItems,
  uploadFiles,
  createDirectory,
  downloadFile
};
