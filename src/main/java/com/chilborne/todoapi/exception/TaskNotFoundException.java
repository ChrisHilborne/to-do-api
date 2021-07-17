package com.chilborne.todoapi.exception;

public class TaskNotFoundException extends DataNotFoundException {

    public TaskNotFoundException(String message) {
        super(message);
    }

    public TaskNotFoundException(long id) {
        super("Task with id:" + id + " not found");
    }
}
