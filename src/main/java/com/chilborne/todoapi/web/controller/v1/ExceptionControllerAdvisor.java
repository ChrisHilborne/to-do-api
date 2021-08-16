package com.chilborne.todoapi.web.controller.v1;

import com.chilborne.todoapi.exception.DataNotFoundException;
import com.chilborne.todoapi.exception.TaskAlreadyCompletedException;
import com.chilborne.todoapi.exception.UsernameAlreadyExistsException;
import com.chilborne.todoapi.web.error.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;

@ControllerAdvice
public class ExceptionControllerAdvisor {

  private final Logger logger = LoggerFactory.getLogger(ExceptionControllerAdvisor.class);

  @ExceptionHandler({AccessDeniedException.class})
  public ErrorResponse handleAccessDeniedException(HttpServletRequest request, Exception e) {
    String username =
        ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getDetails()).getUsername();
    logger.error("Request: {} - made by User {} - raised {}", request, username, e);
    return new ErrorResponse(HttpStatus.FORBIDDEN, e);
  }

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

  @ExceptionHandler({UsernameAlreadyExistsException.class})
  public ErrorResponse handleUsernameAlreadyExistsException(HttpServletRequest req, Exception e) {
    logger.error("Request: " + req.getRequestURI() + " raised " + e);
    return new ErrorResponse(HttpStatus.BAD_REQUEST, e);
  }

  @ExceptionHandler({ConstraintViolationException.class})
  public ErrorResponse handleConstraintViolationException(
      HttpServletRequest req, ConstraintViolationException e) {
    logger.error("Request: " + req.getRequestURI() + " raised " + e);
    HashMap<String, String> errors = new HashMap<>();
    for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
      errors.put(
          constraintViolation.getRootBean().toString(),
          constraintViolation.getInvalidValue() + " : " + constraintViolation.getMessage());
    }
    return new ErrorResponse(HttpStatus.BAD_REQUEST, errors);
  }

  @ExceptionHandler({MethodArgumentNotValidException.class})
  public ErrorResponse handleMethodArgumentNotValidException(
      HttpServletRequest req, MethodArgumentNotValidException e) {
    logger.error("Request: " + req.getRequestURI() + " raised " + e);
    HashMap<String, String> errors = new HashMap<>();
    for (FieldError error : e.getBindingResult().getFieldErrors()) {
      errors.put(error.getField(), error.getDefaultMessage());
    }
    return new ErrorResponse(HttpStatus.BAD_REQUEST, errors);
  }
}
