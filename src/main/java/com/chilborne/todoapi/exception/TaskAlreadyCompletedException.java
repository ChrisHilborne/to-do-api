package com.chilborne.todoapi.exception;

public class TaskAlreadyCompletedException extends RuntimeException {

    public TaskAlreadyCompletedException(String message) {
        super(message);
    }
}
