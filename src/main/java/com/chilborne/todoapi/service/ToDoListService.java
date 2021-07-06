package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.TaskNotFoundException;
import com.chilborne.todoapi.exception.ToDoListNotFoundException;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.web.dto.SingleValueDTO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ToDoListService {

    ToDoList getToDoListById(long id) throws ToDoListNotFoundException;

    ToDoList saveToDoList(ToDoList list);

    List<ToDoList> getAllToDoList();

    void deleteToDoList(long id);

    ToDoList setToDoListName(long id, SingleValueDTO<String> name) throws ToDoListNotFoundException;

    ToDoList setToDoListDescription(long id, SingleValueDTO<String> description) throws ToDoListNotFoundException;

    ToDoList setToDoListActive(long id, SingleValueDTO<Boolean> active) throws ToDoListNotFoundException;

    ToDoList addTaskToDoList(long id, Task task) throws ToDoListNotFoundException;

    ToDoList removeTaskToDoList(long listId, long taskId) throws TaskNotFoundException;
}
