package com.chilborne.todoapi.web;

public class ErrorMessage {

    private String error;

    protected ErrorMessage() {
    }

    public ErrorMessage(Exception e) {
        this.error = e.getMessage();
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
