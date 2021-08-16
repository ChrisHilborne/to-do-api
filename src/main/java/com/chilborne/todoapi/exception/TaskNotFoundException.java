package com.chilborne.todoapi.exception;

public class TaskNotFoundException extends DataNotFoundException {

    public TaskNotFoundException(String message) {
        super(message);
    }

    public TaskNotFoundException(long id) {
        super("No Task with id: " + id + " found");
    }
}
