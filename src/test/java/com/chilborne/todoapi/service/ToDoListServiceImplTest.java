package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.ToDoListNotFoundException;
import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.dto.ToDoListDto;
import com.chilborne.todoapi.persistance.mapper.TaskMapper;
import com.chilborne.todoapi.persistance.mapper.ToDoListMapper;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.persistance.model.User;
import com.chilborne.todoapi.persistance.repository.TaskRepository;
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

@ExtendWith({MockitoExtension.class})
class ToDoListServiceImplTest {

  static final String USERNAME = "user";
  static final String PASSWORD = "password";
  static final LocalDateTime NOW = LocalDateTime.now();
  static final long ID = 1L;
  @Mock ToDoListRepository toDoListRepository;
  @Mock TaskRepository taskRepository;
  @Mock ToDoListMapper mockListMapper;
  @Mock TaskMapper mockTaskMapper;
  @InjectMocks ToDoListServiceImpl service;

  private User user;
  private ToDoList testList;
  private ToDoListDto testListDto;
  @Captor private ArgumentCaptor<ToDoList> listCaptor;

  @BeforeEach
  void init() {
    testList = new ToDoList("test");
    testList.setId(ID);
    testList.setDescription("description 1");
    testList.setTimeCreated(NOW);

    testListDto = new ToDoListDto();
    testListDto.setName(testList.getName());
    testListDto.setListId(ID);
    testListDto.setDescription(testList.getDescription());
    testListDto.setDateTimeMade(NOW);

    user = new User(USERNAME, PASSWORD);
    user.addToDoList(testList);
    testList.setUser(user);
    testListDto.setUsername(user.getUsername());

  }

  @Test
  void saveToDoList() {
    // given
    given(toDoListRepository.save(testList)).willReturn(testList);
    given(mockListMapper.convertToDoList(testList)).willReturn(testListDto);

    // when
    ToDoListDto result = service.saveToDoList(testList);

    // verify
    verify(toDoListRepository).save(testList);
    verifyNoMoreInteractions(toDoListRepository);
    verify(mockListMapper).convertToDoList(testList);
    verifyNoMoreInteractions(mockListMapper);
  }

  @Test
  void getToDoListById_Success() {
    // given
    given(toDoListRepository.findByIdAndUserUsername(ID, USERNAME))
        .willReturn(Optional.ofNullable(testList));
    given(mockListMapper.convertToDoList(testList)).willReturn(testListDto);

    // when
    ToDoListDto result = service.getToDoListDtoById(ID, USERNAME);

    // verify
    assertEquals(testListDto, result);
  }

  @Test
  void getToDoListById_Fail() {
    // given
    given(toDoListRepository.findByIdAndUserUsername(ID, USERNAME)).willReturn(Optional.empty());

    // verify
    assertThrows(RuntimeException.class, () -> service.getToDoListDtoById(ID, USERNAME));
  }

  @Test
  void getAllToDoList() {
    // given
    given(toDoListRepository.findByUserUsername(USERNAME)).willReturn(List.of(testList));
    given(mockListMapper.convertToDoList(testList)).willReturn(testListDto);

    // when
    List<ToDoListDto> result = service.getAllToDoList(USERNAME);

    // verify
    verify(toDoListRepository, times(1)).findByUserUsername(USERNAME);
    verifyNoMoreInteractions(toDoListRepository);

    verify(mockListMapper).convertToDoList(testList);
    verifyNoMoreInteractions(mockListMapper);

    assertEquals(result.size(), 1);
  }

  @Test
  void deleteToDoListShouldDeleteListWhenItExistsAndBelongsToUser() {
    // given
    given(toDoListRepository.existsByIdAndUserUsername(ID, USERNAME)).willReturn(true);

    // when
    service.deleteToDoList(ID, USERNAME);

    // verify
    verify(toDoListRepository, times(1)).deleteById(ID);
  }

  @Test
  void deleteToDoListShouldThrowToDoListNotFoundExceptionIfListDoesNotExist() {
    // given
    given(toDoListRepository.existsByIdAndUserUsername(ID, user.getUsername())).willReturn(false);

    // verify
    assertThrows(ToDoListNotFoundException.class, () -> service.deleteToDoList(ID, USERNAME));
  }

  @Test
  void setActive() {
    // given
    given(toDoListRepository.findByIdAndUserUsername(ID, USERNAME)).willReturn(Optional.of(testList));
    given(toDoListRepository.save(any(ToDoList.class))).willReturn(testList);
    given(mockListMapper.convertToDoList(testList)).willReturn(testListDto);

    // when
    ToDoListDto result = service.setToDoListActive(ID, USERNAME, false);

    // verify
    verify(toDoListRepository).save(listCaptor.capture());
    ToDoList capturedList = listCaptor.getValue();
    assertFalse(capturedList.isActive());
  }

  @Test
  void addTask() {
    // given
    Task testTask = new Task(testList, "test");
    TaskDto testTaskDto = new TaskDto(testList, "test");

    given(toDoListRepository.findByIdAndUserUsername(ID, USERNAME))
        .willReturn(Optional.of(testList));
    given(mockTaskMapper.convertTaskDto(testTaskDto)).willReturn(testTask);
    given(toDoListRepository.save(any(ToDoList.class))).willReturn(testList);
    given(mockListMapper.convertToDoList(testList)).willReturn(testListDto);

    // when
    service.addTaskToDoList(ID, USERNAME, testTaskDto);

    // verify
    verify(toDoListRepository).save(listCaptor.capture());
    ToDoList capturedList = listCaptor.getValue();

    assertEquals(1, capturedList.getTasks().size());
    assertEquals(testTask, capturedList.getTasks().get(0));
  }

  @Test
  void removeTask() {
    // given
    Task testTask = new Task(testList, "test");
    testList.addTask(testTask);
    TaskDto testTaskDto = new TaskDto(testList, "test");

    given(toDoListRepository.findByIdAndUserUsername(ID, USERNAME)).willReturn(Optional.of(testList));
    given(toDoListRepository.save(any(ToDoList.class))).willReturn(testList);
    given(mockListMapper.convertToDoList(testList)).willReturn(testListDto);

    // when
    service.removeTaskFromToDoList(ID, USERNAME, testTask.getId());

    // verify
    verify(toDoListRepository).save(listCaptor.capture());
    ToDoList capturedList = listCaptor.getValue();

    assertEquals(0, capturedList.getTasks().size());
  }

  @Test
  void removeTaskShouldThrowExceptionIfTaskIsNotPresentInList() {
    // when
    when(toDoListRepository.findByIdAndUserUsername(0L, USERNAME))
        .thenReturn(Optional.ofNullable(testList));

    // verify
    assertThrows(
        RuntimeException.class,
        () -> service.removeTaskFromToDoList(testList.getId(), USERNAME, 500L));
  }

  @Test
  void updateListShouldUpdateToDoListIfItAlreadyExists() {
    // given
    testListDto.setName("this is another name");
    testListDto.setDescription("this is another description");
    long testListId = testList.getId();

    // when
    when(toDoListRepository.findByIdAndUserUsername(testListId, USERNAME))
        .thenReturn(Optional.of(testList));
    when(toDoListRepository.save(any(ToDoList.class))).thenReturn(testList);
    when(mockListMapper.convertToDoList(testList)).thenReturn(testListDto);

    ToDoListDto updated = service.updateToDoListNameAndDescription(testListId, testListDto, USERNAME);

    // verify
    assertEquals(testListDto.getName(), testList.getName());
    assertEquals(testListDto.getDescription(), testList.getDescription());
  }

  @Test
  void updateListShouldThrowToDoListNotFoundExceptionIfItDoesNotExist() {
    // given
    testList.setName("this is another name");

    long testListId = testList.getId();

    // when
    when(toDoListRepository.findByIdAndUserUsername(testListId, USERNAME))
        .thenReturn(Optional.empty());

    // verify
    assertThrows(
        ToDoListNotFoundException.class,
        () -> service.updateToDoListNameAndDescription(testListId, testListDto, USERNAME));
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
