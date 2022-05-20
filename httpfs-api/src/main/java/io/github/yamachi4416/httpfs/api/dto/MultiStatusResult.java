package io.github.yamachi4416.httpfs.api.dto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.NoSuchFileException;

import org.springframework.http.HttpStatus;

import io.github.yamachi4416.httpfs.fs.FsItem;
import io.github.yamachi4416.httpfs.fs.dto.OverWritableResult;

public class MultiStatusResult<T> {
  private final HttpStatus status;
  private final T item;

  public MultiStatusResult(HttpStatus status, T item) {
    this.status = status;
    this.item = item;
  }

  public String getStatus() {
    return status.toString();
  }

  public int getStatusCode() {
    return status.value();
  }

  public T getItem() {
    return item;
  }

  public static MultiStatusResult<FsItem> ofOverWritable(OverWritableResult ret) {
    return new MultiStatusResult<FsItem>(
        ret.isOverwrite() ? HttpStatus.NO_CONTENT : HttpStatus.CREATED,
        ret.getItem());
  }

  public static MultiStatusResult<?> ofIOException(IOException e) {
    if (e instanceof FileNotFoundException) {
      return new MultiStatusResult<>(HttpStatus.NOT_FOUND, null);
    } else if (e instanceof NoSuchFileException) {
      return new MultiStatusResult<>(HttpStatus.NOT_FOUND, null);
    } else if (e instanceof FileSystemException) {
      return new MultiStatusResult<>(HttpStatus.FORBIDDEN, null);
    } else if (e instanceof FileAlreadyExistsException) {
      return new MultiStatusResult<>(HttpStatus.FORBIDDEN, null);
    } else if (e instanceof DirectoryNotEmptyException) {
      return new MultiStatusResult<>(HttpStatus.FORBIDDEN, null);
    }
    return new MultiStatusResult<>(HttpStatus.INTERNAL_SERVER_ERROR, null);
  }

}
