package com.chilborne.todoapi.web.dto;

public class SingleValueDTO {

    private String value;

    public SingleValueDTO(String value) {
        this.value = value;
    }

    protected SingleValueDTO() {

    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
