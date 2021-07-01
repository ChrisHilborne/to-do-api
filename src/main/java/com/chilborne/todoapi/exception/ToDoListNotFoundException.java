package com.chilborne.todoapi.exception;

public class ToDoListNotFoundException extends DataNotFoundException {

    public ToDoListNotFoundException(String message) {
        super(message);
    }
}
