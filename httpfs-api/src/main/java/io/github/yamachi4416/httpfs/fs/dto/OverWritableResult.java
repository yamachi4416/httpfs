package io.github.yamachi4416.httpfs.fs.dto;

import java.nio.file.Files;
import java.nio.file.Path;

import io.github.yamachi4416.httpfs.fs.FsItem;

public class OverWritableResult {
  private boolean overwrite;
  private FsItem item;

  public OverWritableResult() {}
  public OverWritableResult(Path path) {
    overwrite = Files.exists(path);
  }

  public boolean isOverwrite() {
    return overwrite;
  }

  public void setOverwrite(boolean overwrite) {
    this.overwrite = overwrite;
  }

  public FsItem getItem() {
    return item;
  }

  public void setItem(FsItem item) {
    this.item = item;
  }

}
