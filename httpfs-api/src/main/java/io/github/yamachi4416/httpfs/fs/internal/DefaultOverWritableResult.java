package io.github.yamachi4416.httpfs.fs.internal;

import java.nio.file.Files;
import java.nio.file.Path;

import io.github.yamachi4416.httpfs.fs.FsItem;
import io.github.yamachi4416.httpfs.fs.OverWritableResult;

public class DefaultOverWritableResult implements OverWritableResult {
  private boolean overwrite;
  private FsItem item;

  DefaultOverWritableResult() {}
  DefaultOverWritableResult(Path path) {
    overwrite = Files.exists(path);
  }

  @Override
  public boolean isOverwrite() {
    return overwrite;
  }

  void setOverwrite(boolean overwrite) {
    this.overwrite = overwrite;
  }

  @Override
  public FsItem getItem() {
    return item;
  }

  void setItem(FsItem item) {
    this.item = item;
  }
}
