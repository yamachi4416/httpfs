package io.github.yamachi4416.httpfs.fs;

public interface OverWritableResult {
  boolean isOverwrite();

  FsItem getItem();
}
