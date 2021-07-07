package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.TaskAlreadyCompletedException;
import com.chilborne.todoapi.exception.TaskNotFoundException;
import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.mapper.TaskMapper;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final TaskMapper mapper;
    private final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    public TaskServiceImpl(TaskRepository repository, TaskMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    @Override
    public TaskDto getTaskById(long id) throws TaskNotFoundException {
        logger.info("Fetching Task id: " + id);
        Task returned = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return mapper.convert(returned);
    }

    @Override
    public TaskDto saveTask(Task task) {
        logger.info("Saving task: " + task);
        Task savedTask = repository.save(task);
        return mapper.convert(savedTask);
    }

    @Override
    public TaskDto completeTask(long id) throws TaskNotFoundException, TaskAlreadyCompletedException {
        logger.info("Completing task id: " + id);
        Task toComplete = repository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
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
    public TaskDto updateTask(long id, TaskDto taskDto) throws TaskNotFoundException {
        logger.info("Updating task id: " + id);
        if (!repository.existsById(id)) throw new TaskNotFoundException(id);
        Task toUpdate = mapper.convert(taskDto);
        toUpdate.setId(id);
        return saveTask(toUpdate);
    }
}
