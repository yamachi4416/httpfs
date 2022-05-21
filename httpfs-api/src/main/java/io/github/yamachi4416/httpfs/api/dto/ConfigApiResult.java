package io.github.yamachi4416.httpfs.api.dto;

public class ConfigApiResult {
  private long maxFileSize;
  private long maxRequestSize;

  public void setMaxFileSize(long maxFileSize) {
    this.maxFileSize = maxFileSize;
  }

  public long getMaxFileSize() {
    return maxFileSize;
  }

  public void setMaxRequestSize(long maxRequestSize) {
    this.maxRequestSize = maxRequestSize;
  }

  public long getMaxRequestSize() {
    return maxRequestSize;
  }
}
