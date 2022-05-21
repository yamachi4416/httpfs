import { HttpException } from '../HttpException';
import { FsItem } from './FsItem';

export class MultiStatus {
  status: string;
  statusCode: number;
  detail: string;
  item?: FsItem;

  get isError() {
    return this.statusCode >= 400;
  }

  toException(): HttpException | undefined {
    if (this.isError) {
      return new HttpException(this.statusCode, this.status);
    }
  }

  static fromJson(json: MultiStatus, path: string[]) {
    const ret = new MultiStatus();
    ret.status = json.status;
    ret.statusCode = json.statusCode;
    ret.detail = json.detail;
    if (json.item) {
      ret.item = FsItem.fromJson(json.item, path);
    }
    return ret;
  }

  static fromError(err: Error) {
    const ret = new MultiStatus();
    ret.status = err.message;
    if (err instanceof HttpException) {
      ret.statusCode = err.status;
      ret.detail = err.detail || err.message;
    } else {
      ret.statusCode = 500;
      ret.detail = err.message;
    }
    return ret;
  }

  static fromFileWithError(file: File, err: Error, path: string[]) {
    const ret = this.fromError(err);
    ret.item = FsItem.fromFile(file, path);
    return ret;
  }
}
