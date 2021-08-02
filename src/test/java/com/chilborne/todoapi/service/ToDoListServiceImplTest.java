package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.ToDoListNotFoundException;
import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.dto.ToDoListDto;
import com.chilborne.todoapi.persistance.mapper.TaskMapper;
import com.chilborne.todoapi.persistance.mapper.ToDoListMapper;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.persistance.model.User;
import com.chilborne.todoapi.persistance.repository.ToDoListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

  static final String USERNAME = "user";
  static final String PASSWORD = "password";
  static final LocalDateTime NOW = LocalDateTime.now();
  @Mock ToDoListRepository repository;
  @Mock ToDoListMapper mockListMapper;
  @Mock TaskMapper mockTaskMapper;
  @InjectMocks ToDoListServiceImpl service;
  ToDoListMapper toDoListMapper = ToDoListMapper.INSTANCE;
  TaskMapper taskMapper = TaskMapper.INSTANCE;
  private User user;
  private ToDoList testList;
  private ToDoListDto testDto;

  @BeforeEach
  void init() {
    testList = new ToDoList("test");
    Task first = new Task(testList, "first");
    Task second = new Task(testList, "second");
    testList.setTasks(List.of(first, second));
    testList.setTimeCreated(NOW);

    user = new User(USERNAME, PASSWORD);
    user.addToDoList(testList);
    testList.setUser(user);

    testDto = toDoListMapper.convertToDoList(testList);
  }

  @Test
  void saveToDoList() {
    // given
    given(repository.save(testList)).willReturn(testList);
    given(mockListMapper.convertToDoList(testList)).willReturn(testDto);

    // when
    ToDoListDto result = service.saveToDoList(testList);

    // verify
    verify(repository).save(testList);
    verifyNoMoreInteractions(repository);
    verify(mockListMapper).convertToDoList(testList);
    verifyNoMoreInteractions(mockListMapper);
    assertTrue(toDoListMapper.compare(testList, result));
  }

  @Test
  void getToDoListById_Success() {
    // given
    given(repository.findByIdAndUserUsername(1L, USERNAME))
        .willReturn(Optional.ofNullable(testList));
    given(mockListMapper.convertToDoList(testList)).willReturn(testDto);

    // when
    ToDoListDto result = service.getToDoListDtoByIdAndUsername(1L, USERNAME);

    // verify
    assertTrue(toDoListMapper.compare(testList, result));
  }

  @Test
  void getToDoListById_Fail() {
    // given
    given(repository.findByIdAndUserUsername(1L, USERNAME)).willReturn(Optional.empty());

    // verify
    assertThrows(RuntimeException.class, () -> service.getToDoListDtoByIdAndUsername(1L, USERNAME));
  }

  @Test
  void getAllToDoList() {
    // given
    given(repository.findByUserUsername(USERNAME)).willReturn(List.of(testList));
    given(mockListMapper.convertToDoList(testList)).willReturn(testDto);

    // when
    List<ToDoListDto> result = service.getAllToDoList(USERNAME);

    // verify
    verify(repository, times(1)).findByUserUsername(USERNAME);
    verifyNoMoreInteractions(repository);

    verify(mockListMapper).convertToDoList(testList);
    verifyNoMoreInteractions(mockListMapper);

    assertEquals(result.size(), 1);
    assertTrue(toDoListMapper.compare(testList, result.get(0)));
  }

  @Test
  void deleteToDoListShouldDeleteListWhenItExistsAndBelongsToUser() {
    // given
    given(repository.existsByIdAndUserUsername(1L, USERNAME)).willReturn(true);

    // when
    service.deleteToDoList(1L, USERNAME);

    // verify
    verify(repository, times(1)).deleteById(1L);
  }

  @Test
  void deleteToDoListShouldThrowToDoListNotFoundExceptionIfListDoesNotExist() {
    // given
    given(repository.existsByIdAndUserUsername(1L, user.getUsername())).willReturn(false);

    // verify
    assertThrows(ToDoListNotFoundException.class, () -> service.deleteToDoList(1L, USERNAME));
  }

  @Test
  void setActive() {
    // given
    ToDoList deactivated = new ToDoList("test", testList.getTasks());
    deactivated.setTimeCreated(NOW);
    deactivated.setActive(false);
    ToDoListDto deactivatedDto = toDoListMapper.convertToDoList(deactivated);

    given(repository.findByIdAndUserUsername(1L, USERNAME))
        .willReturn(Optional.ofNullable(testList));
    given(repository.save(any(ToDoList.class))).willReturn(deactivated);
    given(mockListMapper.convertToDoList(deactivated)).willReturn(deactivatedDto);

    // when
    ToDoListDto result = service.setToDoListActive(1L, USERNAME, false);

    // verify
    assertTrue(toDoListMapper.compare(deactivated, result));
  }

  @Test
  void addTask() {
    // given
    Task thirdTask = new Task(testList, "third");
    TaskDto thirdTaskDto = taskMapper.convertTask(thirdTask);

    ToDoList listWithThirdTask = new ToDoList("test", testList.getTasks());
    listWithThirdTask.setTimeCreated(NOW);
    listWithThirdTask.addTask(thirdTask);
    ToDoListDto listWithThirdTaskDto = toDoListMapper.convertToDoList(listWithThirdTask);

    given(repository.findByIdAndUserUsername(1L, USERNAME))
        .willReturn(Optional.ofNullable(testList));
    given(mockTaskMapper.convertTaskDto(thirdTaskDto)).willReturn(thirdTask);
    given(repository.save(any(ToDoList.class))).willReturn(listWithThirdTask);
    given(mockListMapper.convertToDoList(listWithThirdTask)).willReturn(listWithThirdTaskDto);

    // when
    ToDoListDto result = service.addTaskToDoList(1L, USERNAME, thirdTaskDto);

    // verify
    assertTrue(toDoListMapper.compare(listWithThirdTask, result));

    // check that thirdTask has had it's ToDoList set to testList
    assertEquals(thirdTask.getToDoList(), testList);
  }

  @Test
  void removeTask() {
    // given
    Task toRemove = testList.getTasks().get(1);
    toRemove.setId(5L);

    ToDoList minusTask = new ToDoList("test", testList.getTasks());
    minusTask.setTimeCreated(NOW);
    minusTask.removeTask(toRemove.getId());
    ToDoListDto minusTaskDto = toDoListMapper.convertToDoList(minusTask);

    // when
    when(repository.findByIdAndUserUsername(1L, USERNAME))
        .thenReturn(Optional.ofNullable(testList));
    when(repository.save(any(ToDoList.class))).thenReturn(minusTask);
    when(mockListMapper.convertToDoList(minusTask)).thenReturn(minusTaskDto);

    ToDoListDto result = service.removeTaskToDoList(1L, USERNAME, toRemove.getId());

    // verify
    assertTrue(toDoListMapper.compare(minusTask, result));
  }

  @Test
  void removeTaskShouldThrowExceptionIfTaskIsNotPresentInList() {
    // when
    when(repository.findByIdAndUserUsername(0L, USERNAME))
        .thenReturn(Optional.ofNullable(testList));

    // verify
    assertThrows(
        RuntimeException.class, () -> service.removeTaskToDoList(testList.getId(), USERNAME, 500L));
  }

  @Test
  void updateListShouldReturnUpdatedToDoListIfItAlreadyExists() {
    // given
    testList.setName("this is another name");
    testDto = toDoListMapper.convertToDoList(testList);

    long testListId = testList.getId();

    // when
    when(repository.existsById(testListId)).thenReturn(true);
    when(mockListMapper.convertListDto(testDto)).thenReturn(testList);
    when(repository.save(testList)).thenReturn(testList);
    when(mockListMapper.convertToDoList(testList)).thenReturn(testDto);

    ToDoListDto updated = service.updateToDoList(testListId, testDto, USERNAME);

    // verify
    verify(repository).existsById(testListId);
    verify(repository).save(testList);
    verifyNoMoreInteractions(repository);

    verify(mockListMapper).convertListDto(testDto);
    verify(mockListMapper).convertToDoList(testList);
    verifyNoMoreInteractions(mockListMapper);

    assertTrue(toDoListMapper.compare(testList, updated));
  }

  @Test
  void updateListShouldThrowToDoListNotFoundExceptionIfItDoesNotExist() {
    // given
    testList.setName("this is another name");
    testDto = toDoListMapper.convertToDoList(testList);

    long testListId = testDto.getListId();

    // when
    when(repository.existsById(testListId)).thenReturn(false);

    // verify
    assertThrows(
        ToDoListNotFoundException.class, () -> service.updateToDoList(testListId, testDto, USERNAME));
    verify(repository).existsById(testListId);
    verifyNoMoreInteractions(repository);
  }

  @Test
  void
      getToDoListByIdAndUsernameShouldReturnToDoListWithPassedIdBelongingToUserWithPassedUsername() {
    // given
    given(repository.findByIdAndUserUsername(testList.getId(), user.getUsername()))
        .willReturn(Optional.of(testList));

    // when
    ToDoList result = service.getToDoListByIdAndUsername(testList.getId(), user.getUsername());

    // verify
    assertEquals(testList, result);
  }

  @Test
  void
      getToDoListByIdAndUsernameShouldThrowToDoListNotFoundExceptionWhenRepositoryReturnsEmptyOptional() {
    // given
    given(repository.findByIdAndUserUsername(testList.getId(), user.getUsername()))
        .willReturn(Optional.empty());

    // verify
    assertThrows(
        ToDoListNotFoundException.class,
        () -> service.getToDoListByIdAndUsername(testList.getId(), user.getUsername()));
  }
}
