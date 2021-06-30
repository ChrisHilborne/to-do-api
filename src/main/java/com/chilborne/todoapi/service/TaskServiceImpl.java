package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.TaskAlreadyCompletedException;
import com.chilborne.todoapi.exception.TaskNotFoundException;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.repository.TaskRepository;
import com.chilborne.todoapi.web.dto.SingleValueDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
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
        logger.info("Saving task: " + task);
        return repository.save(task);
    }

    @Override
    public Task setTaskName(long id, SingleValueDTO<String> name) throws TaskNotFoundException {
        logger.info(String.format("Setting Task (id:%d) name to: %s", id, name.getValue()));
        Task toUpdate = getTaskById(id);
        toUpdate.setName(name.getValue());
        return saveTask(toUpdate);
    }

    @Override
    public Task setTaskDescription(long id, SingleValueDTO<String> description) throws TaskNotFoundException {
        logger.info(String.format("Setting Task (id:%d) description to: %s", id, description.getValue()));
        Task toUpdate = getTaskById(id);
        toUpdate.setDescription(description.getValue());
        return saveTask(toUpdate);
    }

    @Override
    public Task completeTask(long id) throws TaskNotFoundException, TaskAlreadyCompletedException {
        logger.info("Completing task id: " + id);
        Task toComplete = getTaskById(id);
        if (toComplete.complete()) {
            return saveTask(toComplete);
        }
        else {
            throw new TaskAlreadyCompletedException("This task was already completed at "
                            + toComplete.getTimeCompleted().format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yy"))
            );
        }
    }
}
