package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.DataNotFoundException;
import com.chilborne.todoapi.exception.TaskNotFoundException;
import com.chilborne.todoapi.exception.ToDoListNotFoundException;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.web.dto.SingleValueDTO;

import java.util.List;

public interface ToDoListService {

    ToDoList getById(long id) throws ToDoListNotFoundException;

    ToDoList save(ToDoList list);

    List<ToDoList> getAll();

    void delete(long id);

    ToDoList setName(long id, SingleValueDTO<String> name) throws ToDoListNotFoundException;

    ToDoList setDescription(long id, SingleValueDTO<String> description) throws ToDoListNotFoundException;

    ToDoList setActive(long id, SingleValueDTO<Boolean> active) throws ToDoListNotFoundException;

    ToDoList addTask(long id, Task task) throws ToDoListNotFoundException;

    ToDoList removeTask(long listId, long taskId) throws TaskNotFoundException;
}
