package com.chilborne.todoapi.exception;

public class ToDoListNotFoundException extends DataNotFoundException {

    public ToDoListNotFoundException(long id) {
        super("ToDoList with id " + id + " not found");
    }

    public ToDoListNotFoundException(String message) {
        super(message);
    }
}
