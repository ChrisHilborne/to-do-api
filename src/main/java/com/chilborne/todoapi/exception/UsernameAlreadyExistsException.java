package com.chilborne.todoapi.exception;

public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException() {
    }

    public UsernameAlreadyExistsException(String username) {
        super(String.format("Username: %s already exists", username));
    }

    public UsernameAlreadyExistsException(String username, Throwable cause) {
        super(String.format("Username: %s already exists", username), cause);
    }
}
