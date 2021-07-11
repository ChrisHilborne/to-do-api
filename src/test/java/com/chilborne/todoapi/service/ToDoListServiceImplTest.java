package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.ToDoListNotFoundException;
import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.dto.ToDoListDto;
import com.chilborne.todoapi.persistance.mapper.TaskMapper;
import com.chilborne.todoapi.persistance.mapper.ToDoListMapper;
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

    @Mock
    ToDoListMapper mockListMapper;

    @Mock
    TaskMapper mockTaskMapper;

    @InjectMocks
    ToDoListServiceImpl service;

    @Captor
    ArgumentCaptor<ToDoList> toDoListCaptor;

    LocalDateTime now = LocalDateTime.now();

    ToDoList testList;
    ToDoListDto testDto;

    ToDoListMapper toDoListMapper = ToDoListMapper.INSTANCE;
    TaskMapper taskMapper = TaskMapper.INSTANCE;

    @BeforeEach
    void init() {
        testList = new ToDoList("test");
        Task first = new Task(testList, "first");
        Task second = new Task(testList, "second");
        testList.setTasks(List.of(first, second));
        testList.setTimeCreated(now);

        testDto = toDoListMapper.convertToDoList(testList);
    }

    @Test
    void saveToDoList() {
        //given
        given(repository.save(testList)).willReturn(testList);
        given(mockListMapper.convertToDoList(testList)).willReturn(testDto);

        //when
        ToDoListDto result = service.saveToDoList(testList);

        //verify
        verify(repository).save(testList);
        verifyNoMoreInteractions(repository);
        verify(mockListMapper).convertToDoList(testList);
        verifyNoMoreInteractions(mockListMapper);
        assertTrue(testList.equalsDto(result));
    }


    @Test
    void getToDoListById_Success() {
        //given
        given(repository.findById(1L))
                .willReturn(Optional.ofNullable(testList));
        given(mockListMapper.convertToDoList(testList)).willReturn(testDto);

        //when
        ToDoListDto result = service.getToDoListDtoById(1L);

        //verify
        verify(repository).findById(1L);
        verifyNoMoreInteractions(repository);
        verify(mockListMapper).convertToDoList(testList);
        verifyNoMoreInteractions(mockListMapper);
        assertTrue(testList.equalsDto(result));
    }

    @Test
    void getToDoListById_Fail() {
        //given
        given(repository.findById(1L)).willReturn(Optional.empty());

        //when
        Exception e = assertThrows(RuntimeException.class, () -> service.getToDoListDtoById(1L));

        //verify
        verify(repository, times(1)).findById(1L);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mockListMapper);
        assertEquals("ToDoList with id 1 not found", e.getMessage());
    }

    @Test
    void getAllToDoList() {
        //given
        given(repository.findAll()).willReturn(List.of(testList));
        given(mockListMapper.convertToDoList(testList)).willReturn(testDto);

        //when
        List<ToDoListDto> result = service.getAllToDoList();

        //verify
        verify(repository, times(1)).findAll();
        verifyNoMoreInteractions(repository);

        verify(mockListMapper).convertToDoList(testList);
        verifyNoMoreInteractions(mockListMapper);

        assertEquals(result.size(), 1);
        assertTrue(testList.equalsDto(result.get(0)));
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
        ToDoList deactivated = new ToDoList("test", testList.getTasks());
        deactivated.setTimeCreated(now);
        deactivated.setActive(false);
        ToDoListDto deactivatedDto = toDoListMapper.convertToDoList(deactivated);

        given(repository.findById(1L)).willReturn(Optional.ofNullable(testList));
        given(repository.save(any(ToDoList.class))).willReturn(deactivated);
        given(mockListMapper.convertToDoList(deactivated)).willReturn(deactivatedDto);

        //when
        ToDoListDto result = service.setToDoListActive(1L, false);

        //verify
        verify(repository).findById(1L);
        verify(repository).save(any(ToDoList.class));
        verifyNoMoreInteractions(repository);

        verify(mockListMapper).convertToDoList(deactivated);
        verifyNoMoreInteractions(mockListMapper);

        verify(repository).save(toDoListCaptor.capture());
        assertTrue(deactivated.equalsDto(result));
        assertEquals(deactivated, toDoListCaptor.getValue());
    }

    @Test
    void addTask() {
        //given
        Task thirdTask = new Task(testList, "third");
        TaskDto thirdTaskDto = taskMapper.convertTask(thirdTask);

        ToDoList listWithThirdTask = new ToDoList("test", testList.getTasks());
        listWithThirdTask.setTimeCreated(now);
        listWithThirdTask.addTask(thirdTask);
        ToDoListDto listWithThirdTaskDto = toDoListMapper.convertToDoList(listWithThirdTask);

        given(repository.findById(1L)).willReturn(Optional.ofNullable(testList));
        given(mockTaskMapper.convertTaskDto(thirdTaskDto)).willReturn(thirdTask);
        given(repository.save(any(ToDoList.class))).willReturn(listWithThirdTask);
        given(mockListMapper.convertToDoList(listWithThirdTask)).willReturn(listWithThirdTaskDto);

        //when
        ToDoListDto result = service.addTaskToDoList(1L, thirdTaskDto);

        //verify
        verify(repository).findById(1L);
        verify(repository).save(any(ToDoList.class));
        verifyNoMoreInteractions(repository);

        verify(mockTaskMapper).convertTaskDto(thirdTaskDto);
        verifyNoMoreInteractions(mockTaskMapper);

        verify(mockListMapper).convertToDoList(listWithThirdTask);
        verifyNoMoreInteractions(mockListMapper);

        verify(repository).save(toDoListCaptor.capture());
        assertTrue(listWithThirdTask.equalsDto(result));
        assertEquals(listWithThirdTask, toDoListCaptor.getValue());

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
        ToDoListDto minusTaskDto = toDoListMapper.convertToDoList(minusTask);

        //when
        when(repository.findById(1L)).thenReturn(Optional.ofNullable(testList));
        when(repository.save(any(ToDoList.class))).thenReturn(minusTask);
        when(mockListMapper.convertToDoList(minusTask)).thenReturn(minusTaskDto);

        ToDoListDto result = service.removeTaskToDoList(1L, toRemove.getId());

        //verify
        assertTrue(minusTask.equalsDto(result));

        verify(repository).findById(1L);
        verify(repository).save(any(ToDoList.class));
        verifyNoMoreInteractions(repository);

        verify(mockListMapper).convertToDoList(minusTask);
        verifyNoMoreInteractions(mockListMapper);

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
    void updateListShouldReturnUpdatedToDoListIfItAlreadyExists() {
        //given
        testList.setName("this is another name");
        testDto = toDoListMapper.convertToDoList(testList);

        long testListId = testList.getId();


        //when
        when(repository.existsById(testListId)).thenReturn(true);
        when(mockListMapper.convertListDto(testDto)).thenReturn(testList);
        when(repository.save(testList)).thenReturn(testList);
        when(mockListMapper.convertToDoList(testList)).thenReturn(testDto);

        ToDoListDto updated = service.updateToDoList(testListId, testDto);

        //verify
        verify(repository).existsById(testListId);
        verify(repository).save(testList);
        verifyNoMoreInteractions(repository);

        verify(mockListMapper).convertListDto(testDto);
        verify(mockListMapper).convertToDoList(testList);
        verifyNoMoreInteractions(mockListMapper);

        assertTrue(testList.equalsDto(updated));
    }

    @Test
    void updateListShouldThrowToDoListNotFoundExceptionIfItDoesNotExist() {
        //given
        testList.setName("this is another name");
        testDto = toDoListMapper.convertToDoList(testList);

        long testListId = testDto.getListId();


        //when
        when(repository.existsById(testListId)).thenReturn(false);

        Exception e = assertThrows(
                ToDoListNotFoundException.class,
                () -> service.updateToDoList(testListId, testDto));

        //verify
        verify(repository).existsById(testListId);
        verifyNoMoreInteractions(repository);


    }

}