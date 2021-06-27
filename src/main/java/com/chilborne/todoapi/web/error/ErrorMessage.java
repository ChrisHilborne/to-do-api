package com.chilborne.todoapi.web.error;

public class ErrorMessage  {

    private String error;

    protected ErrorMessage() {
    }

    public ErrorMessage(Exception e) {
        this.error = e.getClass().getSimpleName() + " -> " + e.getMessage();
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
