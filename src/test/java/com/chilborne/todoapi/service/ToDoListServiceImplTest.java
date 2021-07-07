package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.ToDoListNotFoundException;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.persistance.repository.ToDoListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToDoListServiceImplTest {

    @Mock
    ToDoListRepository repository;

    @InjectMocks
    ToDoListServiceImpl service;

    @Captor
    ArgumentCaptor<ToDoList> toDoListCaptor;

    LocalDateTime now = LocalDateTime.now();

    ToDoList testList;

    @BeforeEach
    void init() {
        testList = new ToDoList("test");
        Task first = new Task(testList, "first");
        Task second = new Task(testList, "second");
        testList.setTasks(List.of(first, second));
        testList.setTimeCreated(now);
    }

    @Test
    void saveToDoList() {
        //given
        given(repository.save(testList)).willReturn(testList);

        //when
        ToDoList result = service.saveToDoList(testList);

        //verify
        verify(repository, times(1)).save(testList);
        verifyNoMoreInteractions(repository);
        assertEquals(testList, result);
    }


    @Test
    void getToDoListById_Success() {
        //given
        given(repository.findById(1L))
                .willReturn(Optional.ofNullable(testList));

        //when
        ToDoList result = service.getToDoListById(1L);

        //verify
        verify(repository, times(1)).findById(1L);
        verifyNoMoreInteractions(repository);
        assertEquals(testList, result);
    }

    @Test
    void getToDoListById_Fail() {
        //given
        given(repository.findById(1L)).willReturn(Optional.empty());

        //when
        Exception e = assertThrows(RuntimeException.class, () -> service.getToDoListById(1L));

        //verify
        verify(repository, times(1)).findById(1L);
        verifyNoMoreInteractions(repository);
        assertEquals("ToDoList with id 1 not found", e.getMessage());
    }

    @Test
    void getAllToDoList() {
        //given
        given(repository.findAll()).willReturn(List.of(testList));

        //when
        List<ToDoList> result = service.getAllToDoList();

        //verify
        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);
        assertEquals(List.of(testList), result);
    }

    @Test
    void deleteToDoList() {
        //when
        service.deleteToDoList(1L);

        //verify
        verify(repository, times(1)).deleteById(1L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void setActive() {
        //given
        boolean active = false;
        ToDoList deactivated = new ToDoList("test", testList.getTasks());
        deactivated.setTimeCreated(now);
        deactivated.setActive(active);

        given(repository.findById(1L)).willReturn(Optional.ofNullable(testList));
        given(repository.save(any(ToDoList.class))).willReturn(deactivated);

        //when
        ToDoList result = service.setToDoListActive(1L, active);

        //verify
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(ToDoList.class));
        verifyNoMoreInteractions(repository);
        verify(repository).save(toDoListCaptor.capture());

        assertEquals(deactivated, result);
        assertEquals(deactivated, toDoListCaptor.getValue());
    }

    @Test
    void addTask() {
        //given
        Task thirdTask = new Task(testList, "third");
        ToDoList newTask = new ToDoList("test", testList.getTasks());
        newTask.setTimeCreated(now);
        newTask.addTask(thirdTask);

        given(repository.findById(1L)).willReturn(Optional.ofNullable(testList));
        given(repository.save(any(ToDoList.class))).willReturn(newTask);

        //when
        ToDoList result = service.addTaskToDoList(1L, thirdTask);

        //verify
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(ToDoList.class));
        verifyNoMoreInteractions(repository);
        verify(repository).save(toDoListCaptor.capture());

        assertEquals(newTask, result);
        assertEquals(newTask, toDoListCaptor.getValue());

        //check that thirdTask has had it's ToDoList set to testList
        assertEquals(thirdTask.getToDoList(), testList);
    }

    @Test
    void removeTask() {
        //given
        Task toRemove = testList.getTasks().get(1);
        toRemove.setId(5L);
        ToDoList minusTask = new ToDoList("test", testList.getTasks());
        minusTask.setTimeCreated(now);
        minusTask.removeTask(toRemove.getId());


        //when
        when(repository.findById(1L)).thenReturn(Optional.ofNullable(testList));
        when(repository.save(any(ToDoList.class))).thenReturn(minusTask);
        ToDoList result = service.removeTaskToDoList(1L, toRemove.getId());

        //verify
        assertEquals(minusTask, result);

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(ToDoList.class));

        verify(repository).save(toDoListCaptor.capture());
        assertEquals(minusTask, toDoListCaptor.getValue());

        verifyNoMoreInteractions(repository);

    }

    @Test
    void removeTaskShouldThrowExceptionIfTaskIsNotPresentInList() {
        //when
        when(repository.findById(0L)).thenReturn(Optional.ofNullable(testList));

        //verify
        Exception e = assertThrows(RuntimeException.class,
                () -> service.removeTaskToDoList(testList.getId(), 500L));
        assertEquals("List with id 0 does not contain task with id 500", e.getMessage());
    }


    @Test
    void updateListShouldReturnUpdatedTaskIfTaskAlreadyExists() {
        //given
        testList.setName("this is another name");
        long testListId = testList.getId();

        //when
        when(repository.existsById(testListId)).thenReturn(true);
        when(repository.save(testList)).thenReturn(testList);

        ToDoList updated = service.updateToDoList(testListId, testList);

        //verify
        verify(repository).existsById(testListId);
        verify(repository).save(testList);
        verifyNoMoreInteractions(repository);

        assertEquals(testList, updated);
    }

    @Test
    void updateListShouldThrowToDoListNotFoundExceptionIfListDoesNotExist() {
        //given
        testList.setName("this is another name");
        long testListId = testList.getId();

        //when
        when(repository.existsById(testListId)).thenReturn(false);

        Exception e = assertThrows(
                ToDoListNotFoundException.class,
                () -> service.updateToDoList(testListId, testList));

        //verify
        verify(repository).existsById(testListId);
        verifyNoMoreInteractions(repository);


    }

}