package com.chilborne.todoapi.persistance.mapper;

;
import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.chilborne.todoapi.web.controller.v1.TaskController.TASK_ROOT_URL;
import static org.junit.jupiter.api.Assertions.*;
import static com.chilborne.todoapi.TestVariables.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {TaskMapperImpl.class})
class TaskMapperTest {

  @Autowired TaskMapperImpl taskMapper;
  private Task task;
  private TaskDto taskDto;
  private ToDoList toDoList;

  @BeforeEach
  void init() {
    toDoList = new ToDoList();
    toDoList.setId(LIST_ID);
    toDoList.addTask(task);

    task = new Task();
    task.setId(TASK_ID);
    task.setName(TASK_NAME);
    task.setDescription(TASK_DESC);
    task.setActive(TASK_ACTIVE);
    task.setTimeCreated(TASK_DATE_MADE);
    task.setTimeCompleted(TASK_DATE_FINISHED);
    task.setToDoList(toDoList);

    taskDto = new TaskDto();
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
  void convertTaskToDtoShouldMapAllFieldsAndAddUrl() {
    // when
    TaskDto mapped = taskMapper.convertTask(task);

    // verify
    assertAll("Check TaskDto Properties",
      () -> assertEquals(taskDto, mapped)
    );
  }

  @Test
  void convertTaskDtoToTaskShouldMapAllFieldsExceptToDoListWhichShouldBeNull() {
    // when
    Task mapped = taskMapper.convertTaskDto(taskDto);

    // verify
    assertAll("Check TaskDto Properties",
      () -> assertEquals(task, mapped)
    );
  }
}