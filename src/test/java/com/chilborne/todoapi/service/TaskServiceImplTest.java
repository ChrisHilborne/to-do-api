package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.TaskAlreadyCompletedException;
import com.chilborne.todoapi.exception.TaskNotFoundException;
import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.mapper.TaskMapper;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.repository.TaskRepository;
import com.chilborne.todoapi.security.access.TaskAccessManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

  @Mock TaskRepository taskRepository;
  @Mock TaskAccessManager taskAccessManager;
  @Mock TaskMapper taskMapper;
  @InjectMocks TaskServiceImpl taskService;

  @Captor ArgumentCaptor<Task> taskCaptor;
  private Task task;
  private TaskDto taskDto;
  private long taskId;

  @BeforeEach
  void init() {
    task = new Task("test task");
    task.setActive(true);
    taskDto = TaskMapper.INSTANCE.convertTask(task);
    taskId = task.getId();
  }

  @Test
  void getTaskDtoByIdShouldReturnDtoWhenTaskExists() {
    // given
    given(taskRepository.findById(taskId)).willReturn(Optional.of(task));
    given(taskMapper.convertTask(task)).willReturn(taskDto);

    // when
    TaskDto result = taskService.getTaskDtoById(taskId);

    // verify
    assertEquals(taskDto, result);
  }

  @Test
  void getTaskDtoByIdShouldThrowExceptionWHenTaskDoesNotExist() {
    // given
    given(taskRepository.findById(taskId)).willReturn(Optional.empty());

    // verify
    assertThrows(TaskNotFoundException.class, () -> taskService.getTaskDtoById(taskId));
  }

  @Test
  void checkTaskAccessShouldNotThrowExceptionWhenUserHasAccess() {
    // verify
    assertDoesNotThrow(() -> taskService.checkTaskAccess(task));

  }

  @Test
  void checkTaskAccessShouldThrowExceptionWhenUserDoesNotHaveAccess() {
    // given
    doThrow(new AccessDeniedException("No Access")).when(taskAccessManager).checkAccess(task);

    // verify
    assertThrows(AccessDeniedException.class, () -> taskService.checkTaskAccess(task));
  }

  @Test
  void completeTaskShouldReturnedUpdatedTaskDtoWhenTaskHasNotBeenCompletedAlready() {
    // given
    given(taskRepository.findById(taskId)).willReturn(Optional.of(task));
    given(taskRepository.save(any(Task.class))).willReturn(task);
    given(taskMapper.convertTask(any(Task.class))).willReturn(taskDto);

    // when
    TaskDto result = taskService.completeTask(taskId);

    // verify
    assertFalse(task.isActive());
    assertNotNull(task.getTimeCompleted());

    verify(taskMapper).convertTask(taskCaptor.capture());
    Task capturedTask = taskCaptor.getValue();
    assertFalse(capturedTask.isActive());
    assertNotNull(capturedTask.getTimeCompleted());
  }

  @Test
  void completeTaskShouldThrowTaskAlreadyCompletedExceptionIfItHasAlreadyBeenCompleted() {
    // given
    task.setActive(false);
    task.setTimeCompleted(LocalDateTime.now());

    given(taskRepository.findById(taskId)).willReturn(Optional.of(task));

    // verify
    assertThrows(TaskAlreadyCompletedException.class, () -> taskService.completeTask(taskId));
  }

  @Test
  void updateTaskNameAndDescriptionShouldUpdatedTask() {
    // given
    taskDto.setName("New Name");
    taskDto.setDescription("Description");

    given(taskRepository.findById(taskId)).willReturn(Optional.of(task));
    given(taskRepository.existsById(taskId)).willReturn(true);
    given(taskRepository.save(any(Task.class))).willReturn(task);
    given(taskMapper.convertTask(any(Task.class)))
        .willReturn(taskDto);

    // when
    TaskDto result = taskService.updateTaskNameAndDescription(taskId, taskDto);

    // verify
    assertEquals(taskDto.getName(), task.getName());
    assertEquals(taskDto.getDescription(), task.getDescription());
  }
}
