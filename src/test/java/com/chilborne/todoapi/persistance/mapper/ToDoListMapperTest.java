package com.chilborne.todoapi.persistance.mapper;

import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.dto.ToDoListDto;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.persistance.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.chilborne.todoapi.TestVariables.*;
import static com.chilborne.todoapi.web.controller.v1.TaskController.TASK_ROOT_URL;
import static com.chilborne.todoapi.web.controller.v1.ToDoListController.TO_DO_LIST_ROOT_URL;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ToDoListMapperImpl.class, TaskMapperImpl.class})
class ToDoListMapperTest {

  @Autowired ToDoListMapperImpl toDoListMapper;
  private User user;
  private ToDoList toDoList;
  private ToDoListDto toDoListDto;
  private Task task;
  private TaskDto taskDto;

  @BeforeEach
  void init() {
    user = new User();
    toDoList = new ToDoList();
    toDoListDto = new ToDoListDto();
    task = new Task();
    taskDto = new TaskDto();

    user.setUsername(USERNAME);
    user.addToDoList(toDoList);

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
  void convertToDoListToDtoShouldMapAllFieldsAndShouldMapTasksToTaskDto() {
    // when
    ToDoListDto mappedList = toDoListMapper.convertToDoList(toDoList);

    // verify
    assertAll("ToDoListDto fields are all correct",
      () -> assertEquals(toDoListDto, mappedList)
    );

    assertAll("Nested Task should also be properly mapped to TaskDto",
      () -> assertEquals(toDoListDto.getTasks(), mappedList.getTasks())
    );
  }

  @Test
  void convertListDtoToListShouldMapAllFieldsExceptUserAndShouldMapTaskDtoToTask() {
    // when
    ToDoList mappedListDto = toDoListMapper.convertListDto(toDoListDto);

    // verify
    assertAll("ToDoList fields are all correct, and user is null",
      () -> assertEquals(LIST_ID, mappedListDto.getId()),
      () -> assertEquals(LIST_NAME, mappedListDto.getName()),
      () -> assertEquals(LIST_DESC, mappedListDto.getDescription()),
      () -> assertEquals(LIST_ACTIVE, mappedListDto.isActive()),
      () -> assertEquals(LIST_DATE_MADE, mappedListDto.getTimeCreated()),
      () -> assertNull(mappedListDto.getUser())
    );

    assertAll("Nested TaskDto should also be properly mapped",
      () -> assertEquals(toDoList.getTasks(), mappedListDto.getTasks())
    );
  }
}