export class HttpException extends Error {
  status: number;
  detail: any;

  constructor(status: number, message: string = null, detail: any = null) {
    super(message);
    this.status = status;
    this.detail = detail ?? message;
  }

  static payloadTooLarge() {
    return new HttpException(413, 'Payload Too Large');
  }
}
