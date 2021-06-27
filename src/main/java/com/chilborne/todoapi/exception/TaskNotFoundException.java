package com.chilborne.todoapi.exception;

public class TaskNotFoundException extends DataNotFoundException {

    public TaskNotFoundException(String message) {
        super(message);
    }
}
