package com.example.workflowdemo.domain;

public class Permission {

    private long perm_id;
    private String code;
    private String name;
    private String type;
    private String description;

    public long getPerm_id() {
        return perm_id;
    }

    public void setPerm_id(long perm_id) {
        this.perm_id = perm_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
