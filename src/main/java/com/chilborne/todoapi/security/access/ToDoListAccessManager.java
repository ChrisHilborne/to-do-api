package com.chilborne.todoapi.security.access;

import com.chilborne.todoapi.persistance.model.ToDoList;
import org.springframework.security.access.AccessDeniedException;

public class ToDoListAccessManager implements AccessManager<ToDoList> {
    @Override
    public void checkAccess(ToDoList toDoList) throws AccessDeniedException {

    }
}
