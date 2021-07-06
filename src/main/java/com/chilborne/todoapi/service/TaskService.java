package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.TaskAlreadyCompletedException;
import com.chilborne.todoapi.exception.TaskNotFoundException;
import com.chilborne.todoapi.persistance.model.Task;

public interface TaskService {
    Task getTaskById(long id) throws TaskNotFoundException;

    Task saveTask(Task task);

    Task completeTask(long id) throws TaskNotFoundException, TaskAlreadyCompletedException;

    Task updateTask(long id, Task task);
}
