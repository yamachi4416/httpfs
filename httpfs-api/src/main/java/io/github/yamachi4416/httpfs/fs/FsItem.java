package io.github.yamachi4416.httpfs.fs;

import java.nio.file.Path;
import java.time.Instant;

public interface FsItem {
  Path getPath();

  boolean isWritable();

  boolean isDirectory();

  String getName();

  String getMimeType();

  Long getSize();

  Instant getCreationTime();

  Instant getLastModified();
}
