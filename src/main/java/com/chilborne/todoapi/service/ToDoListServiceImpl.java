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
import com.chilborne.todoapi.persistance.repository.TaskRepository;
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

  private final ToDoListRepository toDoListRepository;
  private final TaskRepository taskRepository;
  private final UserService userService;
  private final ToDoListMapper toDoListMapper;
  private final TaskMapper taskMapper;
  private final Logger logger = LoggerFactory.getLogger(ToDoListServiceImpl.class);

  public ToDoListServiceImpl(
    ToDoListRepository toDoListRepository,
    TaskRepository taskRepository,
    UserService userService,
    ToDoListMapper toDoListMapper,
    TaskMapper taskMapper) {
    this.toDoListRepository = toDoListRepository;
    this.taskRepository = taskRepository;
    this.userService = userService;
    this.toDoListMapper = toDoListMapper;
    this.taskMapper = taskMapper;
  }

  @Override
  @Transactional(propagation = Propagation.SUPPORTS)
  public ToDoListDto saveToDoList(ToDoList list) {
    logger.info("Saving ToDoList (name: " + list.getName() + ")");
    ToDoList saved = toDoListRepository.save(list);
    ToDoListDto savedDto = toDoListMapper.convertToDoList(saved);
    return savedDto;
  }

  @Override
  @Transactional
  public ToDoListDto newToDoList(ToDoListDto listDto, String username) {
    logger.info("Saving ToDoList: {} to User: {}", listDto.getName(), username);
    ToDoList toSave = toDoListMapper.convertListDto(listDto);
    User user = userService.getUserIfAuthorized(username);
    toSave.setUser(user);
    ToDoList saved = toDoListRepository.save(toSave);
    return toDoListMapper.convertToDoList(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public ToDoListDto getToDoListDtoById(long id, String username) throws ToDoListNotFoundException {
    logger.info("Fetching ToDoList with id: " + id);
    ToDoList result = getToDoListByIdAndUsername(id, username);
    return toDoListMapper.convertToDoList(result);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ToDoListDto> getAllToDoList(String username) {
    logger.info("Fetching all ToDoLists for User:{}", username);
    return toDoListRepository.findByUserUsername(username).stream()
        .map(toDoListMapper::convertToDoList)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void deleteToDoList(long id, String username) {
    logger.info("Deleting ToDoList:{} belonging to User:{}", id, username);
    if (existsByIdAndUsername(id, username)) {
      toDoListRepository.deleteById(id);
    }
  }

  @Override
  public ToDoListDto updateToDoListNameAndDescription(
      long id, ToDoListDto listDto, String username) {
    ToDoList toUpdate = getToDoListByIdAndUsername(id, username);
    toUpdate.setName(listDto.getName());
    toUpdate.setDescription(listDto.getDescription());
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
  public ToDoListDto removeTaskFromToDoList(long listId, String username, long taskId)
      throws TaskNotFoundException {
    logger.info(String.format("Removing Task (id: %d) from ToDoList (id; %d)", taskId, listId));
    ToDoList toUpdate = getToDoListByIdAndUsername(listId, username);
    if (toUpdate.removeTask(taskId)) {
      return saveToDoList(toUpdate);
    } else
      throw new TaskNotFoundException(
          String.format("list with id:%d does not contain task with id:%d", listId, taskId));
  }

  ToDoList getToDoListByIdAndUsername(long id, String username) throws ToDoListNotFoundException {
    logger.info("Getting ToDoList with id:{}, belonging to User:{}", id, username);
    return toDoListRepository
        .findByIdAndUserUsername(id, username)
        .orElseThrow(() -> new ToDoListNotFoundException(id, username));
  }

  @Override
  public boolean listBelongsToUser(long listId, String username) {
    return toDoListRepository.existsByIdAndUserUsername(listId, username);
  }

  private boolean existsByIdAndUsername(long id, String username) throws ToDoListNotFoundException {
    logger.debug("Checking existence of ToDoList with id:{}, belonging to User:{}", id, username);
    if (toDoListRepository.existsByIdAndUserUsername(id, username)) {
      return true;
    } else {
      throw new ToDoListNotFoundException(id, username);
    }
  }
}
