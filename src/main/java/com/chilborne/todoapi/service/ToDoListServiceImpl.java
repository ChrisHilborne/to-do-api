package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.TaskNotFoundException;
import com.chilborne.todoapi.exception.ToDoListNotFoundException;
import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.dto.ToDoListDto;
import com.chilborne.todoapi.persistance.mapper.TaskMapper;
import com.chilborne.todoapi.persistance.mapper.ToDoListMapper;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.persistance.model.User;
import com.chilborne.todoapi.persistance.repository.ToDoListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ToDoListServiceImpl implements ToDoListService {

  private final ToDoListRepository repository;
  private final UserService userService;
  private final ToDoListMapper toDoListMapper;
  private final TaskMapper taskMapper;
  private final Logger logger = LoggerFactory.getLogger(ToDoListServiceImpl.class);

  public ToDoListServiceImpl(
      ToDoListRepository repository,
      UserService userService,
      ToDoListMapper toDoListMapper,
      TaskMapper taskMapper) {
    this.repository = repository;
    this.userService = userService;
    this.toDoListMapper = toDoListMapper;
    this.taskMapper = taskMapper;
  }

  @Override
  @Transactional(propagation = Propagation.SUPPORTS)
  public ToDoListDto saveToDoList(ToDoList list) {
    logger.info("Saving ToDoList (name: " + list.getName() + ")");
    ToDoList saved = repository.save(list);
    return toDoListMapper.convertToDoList(saved);
  }

  @Override
  @Transactional
  public ToDoListDto newToDoList(ToDoListDto listDto, String username) {
    logger.info("Saving ToDoList: {} to User: {}", listDto.getName(), username);
    ToDoList toSave = toDoListMapper.convertListDto(listDto);
    User user = userService.getUser(username);
    toSave.setUser(user);
    ToDoList saved = repository.save(toSave);
    return toDoListMapper.convertToDoList(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public ToDoListDto getToDoListDtoByIdAndUsername(long id, String username) throws ToDoListNotFoundException {
    logger.info("Fetching ToDoList with id: " + id);
    ToDoList result = getToDoListByIdAndUsername(id, username);
    return toDoListMapper.convertToDoList(result);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ToDoListDto> getAllToDoList(String username) {
    logger.info("Fetching all ToDoLists for User:{}", username);
    return repository.findByUserUsername(username).stream()
        .map(toDoListMapper::convertToDoList)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void deleteToDoList(long id, String username) {
    logger.info("Deleting ToDoList:{} belonging to User:{}", id, username);
    if (existsByIdAndUsername(id, username)) {
      repository.deleteById(id);
      }
  }

  @Override
  public ToDoListDto updateToDoList(long id, ToDoListDto listDto, String username) {
    repository.existsByIdAndUserUsername(id, username);
    ToDoList toUpdate = toDoListMapper.convertListDto(listDto);
    toUpdate.setId(id);
    User listOwner = userService.getUser(username);
    toUpdate.setUser(listOwner);
    return saveToDoList(toUpdate); 
  }

  @Override
  public ToDoListDto setToDoListActive(long id, String username, boolean active)
      throws ToDoListNotFoundException {
    logger.info(String.format("Setting ToDoList (id: %d) Active to: %b", id, active));
    ToDoList toUpdate = getToDoListByIdAndUsername(id, username);
    toUpdate.setActive(active);
    return saveToDoList(toUpdate);
  }

  @Override
  public ToDoListDto addTaskToDoList(long listId, String username, TaskDto taskDto)
      throws ToDoListNotFoundException {
    logger.info(
        String.format("Adding Task (name: %s) to ToDoList (id: %d)", taskDto.getName(), listId));
    ToDoList toUpdate = getToDoListByIdAndUsername(listId, username);
    Task newTask = taskMapper.convertTaskDto(taskDto);
    newTask.setToDoList(toUpdate);
    toUpdate.addTask(newTask);
    return saveToDoList(toUpdate);
  }

  @Override
  public ToDoListDto removeTaskToDoList(long listId, String username, long taskId)
      throws TaskNotFoundException {
    logger.info(String.format("Removing Task (id: %d) from ToDoList (id; %d)", taskId, listId));
    ToDoList toUpdate = getToDoListByIdAndUsername(listId, username);
    if (toUpdate.removeTask(taskId)) {
      return saveToDoList(toUpdate);
    } else
      throw new TaskNotFoundException(
          String.format("list with id:%d does not contain task with id:%d", listId, taskId));
  }

  /**
   * Private method to return ToDoList object with null check to methods in ToDoListService. Allows
   * us to avoid multiple uses of ToDoListMapper.
   */
  private ToDoList getToDoList(long id) throws ToDoListNotFoundException {
    logger.info("Getting ToDoList with id: " + id);
    return repository.findById(id).orElseThrow(() -> new ToDoListNotFoundException(id));
  }

  ToDoList getToDoListByIdAndUsername(long id, String username)
      throws ToDoListNotFoundException {
    logger.info("Getting ToDoList with id:{}, belonging to User:{}", id, username);
    return repository
        .findByIdAndUserUsername(id, username)
        .orElseThrow(() -> new ToDoListNotFoundException(id, username));
  }

  private boolean existsByIdAndUsername(long id, String username) throws ToDoListNotFoundException {
    logger.debug("Checking existence of ToDoList with id:{}, belonging to User:{}", id, username);
    if (repository.existsByIdAndUserUsername(id, username)) {
      return true;
    }
    else {
      throw new ToDoListNotFoundException(id, username);
    }
  }
}
