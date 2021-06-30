package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.DataNotFoundException;
import com.chilborne.todoapi.exception.TaskAlreadyCompletedException;
import com.chilborne.todoapi.exception.TaskNotFoundException;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.repository.TaskRepository;
import com.chilborne.todoapi.web.dto.SingleValueDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    public TaskServiceImpl(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public Task getTaskById(long id) throws TaskNotFoundException {
        logger.info("Fetching Task id: " + id);
        return repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with id: " + id + " not found"));
    }

    @Override
    public Task saveTask(Task task) {
        return null;
    }

    @Override
    public Task setTaskName(long id, SingleValueDTO<String> name) throws TaskNotFoundException {
        return null;
    }

    @Override
    public Task setTaskDescription(long id, SingleValueDTO<String> description) throws TaskNotFoundException {
        return null;
    }

    @Override
    public Task setTaskToDoList(long taskId, long toDoListId) throws DataNotFoundException {
        return null;
    }

    @Override
    public Task completeTask(long id) throws TaskNotFoundException, TaskAlreadyCompletedException {
        return null;
    }
}
