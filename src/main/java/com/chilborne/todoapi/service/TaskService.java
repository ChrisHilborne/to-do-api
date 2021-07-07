package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.TaskAlreadyCompletedException;
import com.chilborne.todoapi.exception.TaskNotFoundException;
import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.model.Task;

public interface TaskService {
    TaskDto getTaskById(long id) throws TaskNotFoundException;

    TaskDto saveTask(Task task);

    TaskDto completeTask(long id) throws TaskNotFoundException, TaskAlreadyCompletedException;

    TaskDto updateTask(long id, TaskDto task);
}
