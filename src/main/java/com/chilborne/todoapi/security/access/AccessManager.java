package com.chilborne.todoapi.security.access;

import org.springframework.security.access.AccessDeniedException;

public interface AccessManager<T> {

    void checkAccess(T t) throws AccessDeniedException;
}
