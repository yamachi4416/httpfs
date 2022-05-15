package io.github.yamachi4416.httpfs.api.dto;

import javax.validation.constraints.NotEmpty;

public class DeleteItemsParam {
  @NotEmpty
  private String[] names;

  public String[] getNames() {
    return names;
  }

  public void setNames(String[] names) {
    this.names = names;
  }
}
