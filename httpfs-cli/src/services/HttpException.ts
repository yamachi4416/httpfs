export class HttpException extends Error {
  status: number;

  constructor(status: number, message: string = null) {
    super(message);
    this.status = status;
  }
}
