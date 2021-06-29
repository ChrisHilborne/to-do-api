package com.chilborne.todoapi.web.dto;

public class SingleValueDTO<T> {

    private T value;

    public SingleValueDTO(T value) {
        this.value = value;
    }

    protected SingleValueDTO() {

    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
