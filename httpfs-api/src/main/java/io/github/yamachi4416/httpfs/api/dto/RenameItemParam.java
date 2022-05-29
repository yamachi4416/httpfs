package io.github.yamachi4416.httpfs.api.dto;

import javax.validation.constraints.NotEmpty;

public class RenameItemParam {
    @NotEmpty
    private String name;
    @NotEmpty
    private String newName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
