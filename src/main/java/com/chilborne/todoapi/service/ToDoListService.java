package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.TaskNotFoundException;
import com.chilborne.todoapi.exception.ToDoListNotFoundException;
import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.dto.ToDoListDto;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ToDoListService {

    ToDoListDto getToDoListDtoById(long id) throws ToDoListNotFoundException;

    ToDoListDto saveToDoList(ToDoList list);

    ToDoListDto saveToDoList(ToDoListDto listDto);

    List<ToDoListDto> getAllToDoList();

    void deleteToDoList(long id);

    ToDoListDto updateToDoList(long id, ToDoListDto toDoList);

    ToDoListDto setToDoListActive(long id, boolean active) throws ToDoListNotFoundException;

    ToDoListDto addTaskToDoList(long id, TaskDto task) throws ToDoListNotFoundException;

    ToDoListDto removeTaskToDoList(long listId, long taskId) throws TaskNotFoundException;
}
