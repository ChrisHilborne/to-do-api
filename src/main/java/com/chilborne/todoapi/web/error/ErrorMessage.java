package com.chilborne.todoapi.web.error;

import java.util.HashMap;
import java.util.Map;

public class ErrorMessage  {

    private Map<String, String> error;

    protected ErrorMessage() {
    }

    public ErrorMessage(Map<String, String> error) {
        this.error = error;
    }

    public ErrorMessage(Exception e) {
        this.error = new HashMap<>();
        String name = e.getClass().getSimpleName();
        String message = e.getMessage();
        this.error.put(name, message);
    }

    public Map<String, String> getError() {
        return error;
    }

    public void setError(Map<String, String> error) {
        this.error = error;
    }
}
