package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.TaskAlreadyCompletedException;
import com.chilborne.todoapi.exception.TaskNotFoundException;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.persistance.repository.TaskRepository;
import com.chilborne.todoapi.web.dto.SingleValueDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    TaskRepository repository;

    @InjectMocks
    TaskServiceImpl service;

    @Captor
    ArgumentCaptor<Task> taskCaptor;

    Task testTask;

    LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void init() {
        ToDoList testList = new ToDoList("test list");
        testTask = new Task(testList, "test task");
        testTask.setTaskId(50L);
        testTask.setTimeCreated(now);
    }

    @Test
    void getTaskByIdShouldReturnCorrectTaskWhenItExists() {
        //given
        given(repository.findById(50L)).willReturn(Optional.ofNullable(testTask));

        //when
        Task result = service.getTaskById(50L);

        //verify
        assertEquals(testTask, result);
        verify(repository).findById(50L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getTaskByIdShouldThrowTaskNotFoundExceptionWhenItDoesNotExist() {
        //given
        given(repository.findById(50L)).willReturn(Optional.empty());

        //verify
        Exception e = assertThrows(TaskNotFoundException.class, () -> service.getTaskById(50L));
        assertEquals("Task with id: 50 not found", e.getMessage());
    }

    @Test
    void saveTaskShouldReturnSavedTask() {
        //given
        given(repository.save(testTask)).willReturn(testTask);

        //when
        Task result = service.saveTask(testTask);

        //verify
        assertEquals(testTask, result);
        verify(repository).save(testTask);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void setTaskNameShouldReturnUpdatedTask() {
        //given
        SingleValueDTO<String> nameDTO = new SingleValueDTO<>("new name");
        Task newName = new Task("new name");
        given(repository.findById(50L)).willReturn(Optional.ofNullable(testTask));
        given(repository.save(testTask)).willReturn(newName);

        //when
        Task updatedTask = service.setTaskName(testTask.getTaskId(), nameDTO);

        //verify
        verify(repository).findById(50L);
        verify(repository).save(any(Task.class));
        verifyNoMoreInteractions(repository);

        verify(repository).save(taskCaptor.capture());
        Task savedTask = taskCaptor.getValue();
        assertAll("passedTask is equal to testTask apart from name",
                () -> assertEquals(testTask.getToDoList(), savedTask.getToDoList()),
                () -> assertEquals(testTask.getTaskId(), savedTask.getTaskId()),
                () -> assertEquals(testTask.getTimeCreated(), savedTask.getTimeCreated()),
                () -> assertEquals("new name", savedTask.getName()));

        //check no changes are made between saving and returning updated Task
        assertEquals(savedTask, updatedTask);
    }

    @Test
    void setTaskDescriptionShouldReturnUpdatedTask() {
        //given
        String description = "new description";
        SingleValueDTO<String> descriptionDTO = new SingleValueDTO<>(description);
        Task newDescription = new Task(testTask.getName(), description);
        given(repository.findById(50L)).willReturn(Optional.ofNullable(testTask));
        given(repository.save(testTask)).willReturn(newDescription);

        //when
        Task updatedTask = service.setTaskDescription(testTask.getTaskId(), descriptionDTO);

        //verify
        verify(repository).findById(50L);
        verify(repository).save(any(Task.class));
        verifyNoMoreInteractions(repository);

        verify(repository).save(taskCaptor.capture());
        Task savedTask = taskCaptor.getValue();
        assertAll("savedTask is equal to testTask apart from description",
                () -> assertEquals(testTask.getTaskId(), savedTask.getTaskId()),
                () -> assertEquals(testTask.getToDoList(), savedTask.getToDoList()),
                () -> assertEquals(testTask.getTimeCreated(), savedTask.getTimeCreated()),
                () -> assertEquals(description, savedTask.getDescription()));


        //check no changes are made between saving and returning updated Task
        assertEquals(savedTask, updatedTask);
    }

    @Test
    void setTaskToDoList() {
    }

    @Test
    void completeTaskShouldReturnUpdatedTaskIfTaskHasNotBeenCompletedAlready() {
        //given
        Task mockTask = mock(Task.class);
        testTask.complete();

        //when
        when(mockTask.complete()).thenReturn(true);
        when(repository.findById(50L)).thenReturn(Optional.of(mockTask));
        when(repository.save(mockTask)).thenReturn(testTask);

        Task completeTask = service.completeTask(50L);

        //verify
        verify(mockTask).complete();
        verifyNoMoreInteractions(mockTask);

        verify(repository).findById(50L);
        verify(repository).save(mockTask);
        verifyNoMoreInteractions(repository);

        assertEquals(testTask, completeTask);
    }

    @Test
    void completeTaskShouldThrowTaskAlreadyCompletedExceptionIfTaskHasBeenCompletedAlready() {
        //given
        testTask.complete();
        String timeCompleted = testTask.getTimeCompleted()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yy"));

        //when
        when(repository.findById(50L)).thenReturn(Optional.ofNullable(testTask));

        //verify
        Exception e = assertThrows(TaskAlreadyCompletedException.class,
                () -> service.completeTask(50L));
        assertEquals("This task was already completed at " + timeCompleted,
                        e.getMessage());

    }

}