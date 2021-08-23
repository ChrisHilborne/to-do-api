package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.TaskAlreadyCompletedException;
import com.chilborne.todoapi.exception.TaskNotFoundException;
import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.mapper.TaskMapper;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.repository.TaskRepository;
import com.chilborne.todoapi.security.access.TaskAccessManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TaskServiceImpl implements TaskService {

  private final TaskRepository taskRepository;
  private final TaskAccessManager taskAccessManager;
  private final TaskMapper mapper;
  private final ToDoListService toDoListService;
  private final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

  public TaskServiceImpl(
    TaskRepository taskRepository,
    TaskAccessManager taskAccessManager,
    TaskMapper mapper,
    ToDoListService toDoListService) {
      this.taskRepository = taskRepository;
      this.taskAccessManager = taskAccessManager;
      this.toDoListService = toDoListService;
      this.mapper = mapper;
  }

  @Override
  public TaskDto getTaskDtoById(long id) throws TaskNotFoundException {
    logger.info("Fetching Task id: " + id);
    Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    taskAccessManager.checkAccess(task);
    return mapper.convertTask(task);
  }

  private Task getTask(long id) throws TaskNotFoundException {
    logger.info("Fetching Task id: {}", id);
    Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
    taskAccessManager.checkAccess(task);
    return task;
  }

  private TaskDto saveTask(Task task) {
    logger.info("Saving task: " + task);
    Task savedTask = taskRepository.save(task);
    return mapper.convertTask(savedTask);
  }

  @Override
  public TaskDto completeTask(long id) throws TaskNotFoundException, TaskAlreadyCompletedException {
    logger.info("Completing task id: " + id);
    Task toComplete = getTask(id);
    if (toComplete.isActive()) {
      toComplete.setActive(false);
      toComplete.setTimeCompleted(LocalDateTime.now());
      return saveTask(toComplete);
    } else {
      throw new TaskAlreadyCompletedException(
          "This task was already completed at "
              + toComplete
                  .getTimeCompleted()
                  .format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yy")));
    }
  }

  @Override
  public TaskDto updateTaskNameAndDescription(long id, TaskDto taskDto)
      throws TaskNotFoundException {
    logger.info("Updating task id: {} to {}", id, taskDto);
    if (!taskRepository.existsById(id)) throw new TaskNotFoundException(id);
    Task toUpdate = getTask(id);
    toUpdate.setName(taskDto.getName());
    toUpdate.setDescription(taskDto.getDescription());
    return saveTask(toUpdate);
  }

  @Override
  public void checkTaskAccess(Task task) {
    taskAccessManager.checkAccess(task);
  }

  @Override
  public TaskDto newTask(TaskDto taskDto) {
    //TODO implement method
    return new TaskDto();
  }


}
