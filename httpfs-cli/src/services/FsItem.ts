import { AppConfig } from '../config';

const { ApiEndpoint } = AppConfig;

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
