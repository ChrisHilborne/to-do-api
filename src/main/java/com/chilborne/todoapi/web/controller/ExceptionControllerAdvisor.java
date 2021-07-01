package com.chilborne.todoapi.web.controller;

import com.chilborne.todoapi.exception.DataNotFoundException;
import com.chilborne.todoapi.exception.TaskAlreadyCompletedException;
import com.chilborne.todoapi.web.error.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ExceptionControllerAdvisor {

    private final Logger logger = LoggerFactory.getLogger(ExceptionControllerAdvisor.class);

    @ExceptionHandler({DataNotFoundException.class})
    public ErrorResponse handleDataNotFoundException(HttpServletRequest req, Exception e) {
        logger.error("Request: " + req.getRequestURI() + " raised " + e);
        return new ErrorResponse(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler({TaskAlreadyCompletedException.class})
    public ErrorResponse handleTaskAlreadyCompleteException(HttpServletRequest req, Exception e) {
        logger.error("Request: " + req.getRequestURI() + " raised " + e);
        return new ErrorResponse(HttpStatus.ALREADY_REPORTED, e);
    }


}
