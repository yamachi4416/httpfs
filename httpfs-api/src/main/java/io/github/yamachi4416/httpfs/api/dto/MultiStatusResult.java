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
import reactor.core.publisher.Mono;

public class MultiStatusResult<T> {
  private final HttpStatus status;
  private final T item;
  private final String detail;

  public MultiStatusResult(HttpStatus status, T item, String detail) {
    this.status = status;
    this.item = item;
    if (detail == null && status.isError()) {
      this.detail = status.getReasonPhrase();
    } else {
      this.detail = detail;
    }
  }

  public MultiStatusResult(HttpStatus status, T item) {
    this(status, item, null);
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

  public String getDetail() {
    return detail;
  }

  public static MultiStatusResult<FsItem> ofOverWritable(OverWritableResult ret) {
    return new MultiStatusResult<FsItem>(
        ret.isOverwrite() ? HttpStatus.NO_CONTENT : HttpStatus.CREATED,
        ret.getItem());
  }

  @FunctionalInterface
  public static interface WithIOException {
    MultiStatusResult<?> get() throws IOException;
  }

  @FunctionalInterface
  public static interface WithIOExceptionMono {
    Mono<MultiStatusResult<?>> get() throws IOException;
  }

  public static MultiStatusResult<?> withIOException(WithIOException fn) {
    return withIOException(() -> {
      return Mono.just(fn.get());
    }).block();
  }

  public static Mono<MultiStatusResult<?>> withIOException(WithIOExceptionMono fn) {
    try {
      return fn.get();
    } catch (FileNotFoundException | NoSuchFileException e) {
      return Mono.just(new MultiStatusResult<>(HttpStatus.NOT_FOUND, null));
    } catch (FileAlreadyExistsException e) {
      return Mono.just(new MultiStatusResult<>(HttpStatus.FORBIDDEN, null));
    } catch (DirectoryNotEmptyException e) {
      return Mono.just(new MultiStatusResult<>(HttpStatus.FORBIDDEN, null, "Directory Not Empty"));
    } catch (FileSystemException e) {
      return Mono.just(new MultiStatusResult<>(HttpStatus.FORBIDDEN, null));
    } catch (IOException e) {
      return Mono.just(new MultiStatusResult<>(HttpStatus.INTERNAL_SERVER_ERROR, null));
    }
  }
}
