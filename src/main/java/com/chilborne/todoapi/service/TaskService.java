package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.TaskAlreadyCompletedException;
import com.chilborne.todoapi.exception.TaskNotFoundException;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.web.dto.SingleValueDTO;

public interface TaskService {

    Task getTaskById(long id) throws TaskNotFoundException;

    Task saveTask(Task task);

    Task setTaskName(long id, SingleValueDTO<String> name) throws TaskNotFoundException;

    Task setTaskDescription(long id, SingleValueDTO<String> description) throws TaskNotFoundException;

    Task completeTask(long id) throws TaskNotFoundException, TaskAlreadyCompletedException;
}
