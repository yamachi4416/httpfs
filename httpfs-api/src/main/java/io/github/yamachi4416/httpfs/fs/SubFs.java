package io.github.yamachi4416.httpfs.fs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.stream.Stream;

import io.github.yamachi4416.httpfs.fs.internal.DefaultSubFs;
import reactor.core.publisher.Mono;

public interface SubFs {

  Path getPath();

  boolean isWritable();

  boolean isChild(Path path);

  FsItem resolve(String... paths) throws FileNotFoundException;

  SubFs sub(Path path) throws FileNotFoundException;

  SubFs wSub(Path path) throws FileNotFoundException, AccessDeniedException;

  Stream<FsItem> list() throws IOException;

  OverWritableResult createFile(String fileName, GetInputStream getInputStream) throws IOException;

  Mono<OverWritableResult> createFile(String fileName, TransferTo transferTo) throws AccessDeniedException;

  FsItem createDirectory(String dirname) throws IOException;

  FsItem delete(String name) throws IOException;

  OverWritableResult move(SubFs dest, String name, boolean overWrite) throws IOException;

  FsItem rename(String target, String newName) throws IOException;

  public static SubFs defaultSubFs(Path docRoot) {
    return DefaultSubFs.of(docRoot);
  }

  @FunctionalInterface
  static interface GetInputStream {
    InputStream get() throws IOException;
  }

  @FunctionalInterface
  static interface TransferTo {
    Mono<?> transferTo(Path path);
  }
}
