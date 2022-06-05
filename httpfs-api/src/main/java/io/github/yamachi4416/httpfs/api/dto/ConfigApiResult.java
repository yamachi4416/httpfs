package io.github.yamachi4416.httpfs.api.dto;

public class ConfigApiResult {
  private long maxFileSize;
  private int maxFileCount;

  public void setMaxFileSize(long maxFileSize) {
    this.maxFileSize = maxFileSize;
  }

  public long getMaxFileSize() {
    return maxFileSize;
  }

  public void setMaxFileCount(int maxFileCount) {
    this.maxFileCount = maxFileCount;
  }

  public int getMaxFileCount() {
    return maxFileCount;
  }
}
