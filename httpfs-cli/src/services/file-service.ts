const nop = function (...args: any) {};

export default {
  async list(path: string[]): Promise<object[]> {
    return await fetch(`/api/files/${path.join("/")}`, {
      method: "get",
      cache: "no-cache",
      headers: {
        "Content-Type": "application/json",
      },
    }).then((res) => res.json());
  },

  async upload(path: string[], files: FileList, callback = nop) {
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
  },

  async createDirectory(path: string[], dirname: string) {
    const form = new FormData();
    form.append("dirname", dirname);
    return await fetch(`/api/files/${path.join("/")}?action=mkdir`, {
      method: "put",
      body: form,
    }).then((res) => res.json());
  },

  download(path: string[], name: string) {
    window.open(`/api/files/${path.join("/")}/${name}`, "_blank");
  },
};
