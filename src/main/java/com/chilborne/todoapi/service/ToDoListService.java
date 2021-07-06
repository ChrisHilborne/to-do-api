package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.TaskNotFoundException;
import com.chilborne.todoapi.exception.ToDoListNotFoundException;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ToDoListService {

    ToDoList getToDoListById(long id) throws ToDoListNotFoundException;

    ToDoList saveToDoList(ToDoList list);

    List<ToDoList> getAllToDoList();

    void deleteToDoList(long id);

    ToDoList updateToDoList(long id, ToDoList toDoList);

    ToDoList setToDoListActive(long id, boolean active) throws ToDoListNotFoundException;

    ToDoList addTaskToDoList(long id, Task task) throws ToDoListNotFoundException;

    ToDoList removeTaskToDoList(long listId, long taskId) throws TaskNotFoundException;
}
