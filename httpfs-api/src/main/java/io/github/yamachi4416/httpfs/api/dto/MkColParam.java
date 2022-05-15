package io.github.yamachi4416.httpfs.api.dto;

import javax.validation.constraints.NotEmpty;

public class MkColParam {
  @NotEmpty
  private String dirname;

  public String getDirname() {
    return dirname;
  }

  public void setDirname(String dirname) {
    this.dirname = dirname;
  }
}
