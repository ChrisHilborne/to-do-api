package com.chilborne.todoapi.service;

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

    @BeforeEach
    void init() {
        ToDoList testList = new ToDoList("test list");
        testTask = new Task(testList, "test task");
        testTask.setTaskId(50L);
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
        assertEquals(newName, updatedTask);
        verify(repository).findById(50L);
        verify(repository).save(any(Task.class));
        verifyNoMoreInteractions(repository);

        verify(repository).save(taskCaptor.capture());
        Task passedTask = taskCaptor.getValue();
        assertAll("passedTask is equal to testTask apart from name",
                () -> assertEquals(testTask.getTaskId(), passedTask.getTaskId()),
                () -> assertEquals(testTask.getTimeCreated(), passedTask.getTimeCreated()),
                () -> assertEquals("new name", passedTask.getName()));
    }

    @Test
    void setTaskDescription() {
    }

    @Test
    void setTaskToDoList() {
    }

    @Test
    void completeTask() {
    }
}