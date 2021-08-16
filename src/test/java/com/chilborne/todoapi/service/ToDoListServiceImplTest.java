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
  @Mock ToDoListRepository toDoListRepository;
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
    given(toDoListRepository.save(testList)).willReturn(testList);
    given(mockListMapper.convertToDoList(testList)).willReturn(testDto);

    // when
    ToDoListDto result = service.saveToDoList(testList);

    // verify
    verify(toDoListRepository).save(testList);
    verifyNoMoreInteractions(toDoListRepository);
    verify(mockListMapper).convertToDoList(testList);
    verifyNoMoreInteractions(mockListMapper);
    assertTrue(toDoListMapper.compare(testList, result));
  }

  @Test
  void getToDoListById_Success() {
    // given
    given(toDoListRepository.findByIdAndUserUsername(1L, USERNAME))
        .willReturn(Optional.ofNullable(testList));
    given(mockListMapper.convertToDoList(testList)).willReturn(testDto);

    // when
    ToDoListDto result = service.getToDoListDtoById(1L, USERNAME);

    // verify
    assertTrue(toDoListMapper.compare(testList, result));
  }

  @Test
  void getToDoListById_Fail() {
    // given
    given(toDoListRepository.findByIdAndUserUsername(1L, USERNAME)).willReturn(Optional.empty());

    // verify
    assertThrows(RuntimeException.class, () -> service.getToDoListDtoById(1L, USERNAME));
  }

  @Test
  void getAllToDoList() {
    // given
    given(toDoListRepository.findByUserUsername(USERNAME)).willReturn(List.of(testList));
    given(mockListMapper.convertToDoList(testList)).willReturn(testDto);

    // when
    List<ToDoListDto> result = service.getAllToDoList(USERNAME);

    // verify
    verify(toDoListRepository, times(1)).findByUserUsername(USERNAME);
    verifyNoMoreInteractions(toDoListRepository);

    verify(mockListMapper).convertToDoList(testList);
    verifyNoMoreInteractions(mockListMapper);

    assertEquals(result.size(), 1);
    assertTrue(toDoListMapper.compare(testList, result.get(0)));
  }

  @Test
  void deleteToDoListShouldDeleteListWhenItExistsAndBelongsToUser() {
    // given
    given(toDoListRepository.existsByIdAndUserUsername(1L, USERNAME)).willReturn(true);

    // when
    service.deleteToDoList(1L, USERNAME);

    // verify
    verify(toDoListRepository, times(1)).deleteById(1L);
  }

  @Test
  void deleteToDoListShouldThrowToDoListNotFoundExceptionIfListDoesNotExist() {
    // given
    given(toDoListRepository.existsByIdAndUserUsername(1L, user.getUsername())).willReturn(false);

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

    given(toDoListRepository.findByIdAndUserUsername(1L, USERNAME))
        .willReturn(Optional.ofNullable(testList));
    given(toDoListRepository.save(any(ToDoList.class))).willReturn(deactivated);
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

    given(toDoListRepository.findByIdAndUserUsername(1L, USERNAME))
        .willReturn(Optional.ofNullable(testList));
    given(mockTaskMapper.convertTaskDto(thirdTaskDto)).willReturn(thirdTask);
    given(toDoListRepository.save(any(ToDoList.class))).willReturn(listWithThirdTask);
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
    when(toDoListRepository.findByIdAndUserUsername(1L, USERNAME))
        .thenReturn(Optional.ofNullable(testList));
    when(toDoListRepository.save(any(ToDoList.class))).thenReturn(minusTask);
    when(mockListMapper.convertToDoList(minusTask)).thenReturn(minusTaskDto);

    ToDoListDto result = service.removeTaskFromToDoList(1L, USERNAME, toRemove.getId());

    // verify
    assertTrue(toDoListMapper.compare(minusTask, result));
  }

  @Test
  void removeTaskShouldThrowExceptionIfTaskIsNotPresentInList() {
    // when
    when(toDoListRepository.findByIdAndUserUsername(0L, USERNAME))
        .thenReturn(Optional.ofNullable(testList));

    // verify
    assertThrows(
        RuntimeException.class, () -> service.removeTaskFromToDoList(testList.getId(), USERNAME, 500L));
  }

  @Test
  void updateListShouldUpdateToDoListIfItAlreadyExists() {
    // given
    testDto.setName("this is another name");
    testDto.setDescription("this is another description");
    long testListId = testList.getId();

    // when
    when(toDoListRepository.findByIdAndUserUsername(testListId, USERNAME)).thenReturn(Optional.of(testList));
    when(toDoListRepository.save(any(ToDoList.class))).thenReturn(testList);
    when(mockListMapper.convertToDoList(testList)).thenReturn(testDto);

    ToDoListDto updated = service.updateToDoListNameAndDescription(testListId, testDto, USERNAME);

    // verify
    assertEquals(testDto.getName(), testList.getName());
    assertEquals(testDto.getDescription(), testList.getDescription());
  }

  @Test
  void updateListShouldThrowToDoListNotFoundExceptionIfItDoesNotExist() {
    // given
    testList.setName("this is another name");
    testDto = toDoListMapper.convertToDoList(testList);

    long testListId = testDto.getListId();

    // when
    when(toDoListRepository.findByIdAndUserUsername(testListId, USERNAME)).thenReturn(Optional.empty());

    // verify
    assertThrows(
        ToDoListNotFoundException.class, () -> service.updateToDoListNameAndDescription(testListId, testDto, USERNAME));
  }

  @Test
  void
      getToDoListByIdAndUsernameShouldReturnToDoListWithPassedIdBelongingToUserWithPassedUsername() {
    // given
    given(toDoListRepository.findByIdAndUserUsername(testList.getId(), user.getUsername()))
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
    given(toDoListRepository.findByIdAndUserUsername(testList.getId(), user.getUsername()))
        .willReturn(Optional.empty());

    // verify
    assertThrows(
        ToDoListNotFoundException.class,
        () -> service.getToDoListByIdAndUsername(testList.getId(), user.getUsername()));
  }
}
