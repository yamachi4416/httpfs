package io.github.yamachi4416.httpfs.api.dto;

import javax.validation.constraints.NotEmpty;

public class MoveItemParam {
  @NotEmpty
  private String destination;
  @NotEmpty
  private String[] names;
  private boolean overwrite;

  public String getDestination() {
    return destination;
  }

  public void setDestination(String destination) {
    this.destination = destination;
  }

  public boolean isOverwrite() {
    return overwrite;
  }

  public void setOverwrite(boolean overwrite) {
    this.overwrite = overwrite;
  }

  public String[] getNames() {
    return names;
  }

  public void setNames(String[] names) {
    this.names = names;
  }
}
