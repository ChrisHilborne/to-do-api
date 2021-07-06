package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.TaskAlreadyCompletedException;
import com.chilborne.todoapi.exception.TaskNotFoundException;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.repository.TaskRepository;
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
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Override
    public Task saveTask(Task task) {
        logger.info("Saving task: " + task);
        return repository.save(task);
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

    @Override
    public Task updateTask(long id, Task task) throws TaskNotFoundException {
        logger.info("Updating task id: " + id);
        if (!repository.existsById(id)) throw new TaskNotFoundException(id);
        task.setTaskId(id);
        return saveTask(task);
    }
}
