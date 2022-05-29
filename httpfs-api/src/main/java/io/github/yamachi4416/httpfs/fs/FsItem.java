package io.github.yamachi4416.httpfs.fs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class FsItem {
  @JsonIgnore
  private Path path;
  private boolean directory;
  private boolean writable;
  private String name;
  private String mimeType;
  private Long size;
  private Instant lastModified;
  private Instant creationTime;

  public FsItem(Path path) {
    var file = path.toFile();

    this.path = path;
    this.directory = file.isDirectory();
    this.writable = file.canWrite();
    this.name = file.getName();

    if (this.directory) {
      this.size = 0L;
    } else {
      this.size = file.length();
      this.mimeType = MimeTypeHelper.getMimeType(path);
    }

    try {
      var attr = Files.readAttributes(path, BasicFileAttributes.class);
      this.lastModified = attr.lastModifiedTime().toInstant();
      this.creationTime = attr.creationTime().toInstant();
    } catch (IOException e) {
    }
  }

  public boolean isWritable() {
    return writable;
  }

  public void setWritable(boolean writable) {
    this.writable = writable;
  }

  public Path getPath() {
    return path;
  }

  public boolean isDirectory() {
    return directory;
  }

  public String getName() {
    return name;
  }

  public String getMimeType() {
    return mimeType;
  }

  public Long getSize() {
    return size;
  }

  public Instant getCreationTime() {
    return creationTime;
  }

  public Instant getLastModified() {
    return lastModified;
  }
}
