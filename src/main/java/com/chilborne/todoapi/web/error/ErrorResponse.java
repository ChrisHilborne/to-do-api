package com.chilborne.todoapi.web.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ErrorResponse extends ResponseEntity<ErrorMessage> {

    public ErrorResponse(HttpStatus status, Exception e) {
        this(status, new ErrorMessage(e));
    }

    private ErrorResponse(HttpStatus status, ErrorMessage body) {
        super(body, status);
    }
}
