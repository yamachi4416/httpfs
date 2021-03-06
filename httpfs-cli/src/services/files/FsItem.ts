import { getConfig } from './ConfigService';

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
    const { filesApiEndpoint } = getConfig();
    this.endpoint = `${filesApiEndpoint}${this.path}`;
  }

  download() {
    const a = document.createElement('a');
    a.setAttribute('href', this.endpoint);
    a.setAttribute('download', this.name);
    a.click();
  }

  static fromJson(json: any, path: string[]): FsItem {
    const { lastModified, creationTime, ...props } = json;
    const item = props as FsItem;
    const paths = [...path].filter(p => p);
    return new FsItem({
      ...item,
      lastModified: lastModified && new Date(lastModified),
      creationTime: creationTime && new Date(creationTime),
      path: `/${[...paths, item.name]
        .map(p => encodeURIComponent(p))
        .join('/')}`,
      paths: [...paths, item.name],
      parent: `/${paths.map(p => encodeURIComponent(p)).join('/')}`,
    } as FsItem);
  }

  static fromFile(file: File, path: string[]): FsItem {
    const obj = { name: file.name, mimeType: file.type, size: file.size };
    return this.fromJson(obj, path);
  }
}
