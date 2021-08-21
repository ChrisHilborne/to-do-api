package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.TaskAlreadyCompletedException;
import com.chilborne.todoapi.exception.TaskNotFoundException;
import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.model.Task;

public interface TaskService {

  TaskDto getTaskDtoById(long id) throws TaskNotFoundException;

  TaskDto completeTask(long id) throws TaskNotFoundException, TaskAlreadyCompletedException;

  TaskDto updateTaskNameAndDescription(long id, TaskDto task);

  void checkTaskAccess(Task task);

  TaskDto newTask(TaskDto taskDto);
}
