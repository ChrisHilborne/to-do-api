package com.chilborne.todoapi.exception;

public class ToDoListNotFoundException extends DataNotFoundException {

    public ToDoListNotFoundException(long id) {
        super("to_do_list with id:" + id + " not found");
    }

    public ToDoListNotFoundException(String message) {
        super(message);
    }
}
