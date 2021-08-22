package com.chilborne.todoapi.persistance.mapper;

import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.dto.ToDoListDto;
import com.chilborne.todoapi.persistance.dto.UserDto;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.persistance.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static com.chilborne.todoapi.TestVariables.*;
import static com.chilborne.todoapi.TestVariables.TASK_DATE_FINISHED;
import static com.chilborne.todoapi.web.controller.v1.TaskController.TASK_ROOT_URL;
import static com.chilborne.todoapi.web.controller.v1.ToDoListController.TO_DO_LIST_ROOT_URL;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {UserMapperImpl.class, ToDoListMapperImpl.class, TaskMapperImpl.class})
class UserMapperTest {

  @Autowired UserMapperImpl userMapper;

  private User user;
  private UserDto userDto;
  private ToDoList toDoList;
  private ToDoListDto toDoListDto;
  private Task task;
  private TaskDto taskDto;

  @BeforeEach
  void init() {
    user = new User();
    userDto = new UserDto();
    toDoList = new ToDoList();
    toDoListDto = new ToDoListDto();
    task = new Task();
    taskDto = new TaskDto();

    user.setUsername(USERNAME);
    user.setPassword(PASSWORD);
    user.setEmail(EMAIL);
    user.addToDoList(toDoList);

    userDto.setUsername(USERNAME);
    userDto.setPassword(PASSWORD);
    userDto.setEmail(EMAIL);
    userDto.setToDoLists(Arrays.asList(toDoListDto));

    toDoList.setId(LIST_ID);
    toDoList.setUser(user);
    toDoList.setName(LIST_NAME);
    toDoList.setDescription(LIST_DESC);
    toDoList.setActive(LIST_ACTIVE);
    toDoList.setTimeCreated(LIST_DATE_MADE);
    toDoList.addTask(task);

    toDoListDto.setListId(LIST_ID);
    toDoListDto.setUsername(USERNAME);
    toDoListDto.setName(LIST_NAME);
    toDoListDto.setDescription(LIST_DESC);
    toDoListDto.setActive(LIST_ACTIVE);
    toDoListDto.setDateTimeMade(LIST_DATE_MADE);
    toDoListDto.setUrl(TO_DO_LIST_ROOT_URL + "/" + LIST_ID);
    toDoListDto.getTasks().add(taskDto);

    task.setId(TASK_ID);
    task.setName(TASK_NAME);
    task.setDescription(TASK_DESC);
    task.setActive(TASK_ACTIVE);
    task.setTimeCreated(TASK_DATE_MADE);
    task.setTimeCompleted(TASK_DATE_FINISHED);
    task.setToDoList(toDoList);

    taskDto.setTaskId(TASK_ID);
    taskDto.setName(TASK_NAME);
    taskDto.setDescription(TASK_DESC);
    taskDto.setActive(TASK_ACTIVE);
    taskDto.setDateTimeMade(TASK_DATE_MADE);
    taskDto.setDateTimeFinished(TASK_DATE_FINISHED);
    taskDto.setUrl(TASK_ROOT_URL + "/" + TASK_ID);
    taskDto.setListId(toDoList.getId());
  }

  @Test
  void convertUserShouldCorrectlyMapAllFieldsAndNestedToDoList() {
    // when
    UserDto mappedUser = userMapper.convertUser(user);

    // verify
    assertAll("Verify UserDto properties",
      () -> assertEquals(userDto, mappedUser));

    assertAll("Verify ToDoListDto",
      () -> assertEquals(userDto.getToDoLists(), mappedUser.getToDoLists()));
  }

  @Test
  void convertUserDtoShouldCorrectlyMapAllFieldsAndNestedToDoListDto() {
    // when
    User mappedUserDto = userMapper.convertUserDto(userDto);

    // verify
    assertAll("Verify User properties",
      () -> assertEquals(user, mappedUserDto));

    assertAll("Verify ToDoList",
      () -> assertEquals(user.getToDoLists(), mappedUserDto.getToDoLists()));

  }


}