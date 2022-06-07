package io.github.yamachi4416.httpfs.fs.internal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.github.yamachi4416.httpfs.fs.FsItem;

public class DefaultFsItem implements FsItem {
  @JsonIgnore
  private Path path;
  private boolean directory;
  private boolean writable;
  private String name;
  private String mimeType;
  private Long size;
  private Instant lastModified;
  private Instant creationTime;

  private DefaultFsItem() {
  }

  @Override
  public boolean isWritable() {
    return writable;
  }

  @Override
  public Path getPath() {
    return path;
  }

  @Override
  public boolean isDirectory() {
    return directory;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getMimeType() {
    return mimeType;
  }

  @Override
  public Long getSize() {
    return size;
  }

  @Override
  public Instant getCreationTime() {
    return creationTime;
  }

  @Override
  public Instant getLastModified() {
    return lastModified;
  }

  @Override
  public String toString() {
    return path.toString();
  }

  public static FsItem of(Path path) {
    var item = new DefaultFsItem();
    var file = path.toFile();

    item.path = path;
    item.directory = file.isDirectory();
    item.writable = file.canWrite();
    item.name = file.getName();

    if (item.directory) {
      item.size = 0L;
    } else {
      item.size = file.length();
      item.mimeType = MimeTypeHelper.getMimeType(path);
    }

    try {
      var attr = Files.readAttributes(path, BasicFileAttributes.class);
      item.lastModified = attr.lastModifiedTime().toInstant();
      item.creationTime = attr.creationTime().toInstant();
    } catch (IOException e) {
    }

    return item;
  }
}
