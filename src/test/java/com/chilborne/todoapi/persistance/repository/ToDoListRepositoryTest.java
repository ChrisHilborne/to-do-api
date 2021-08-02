package com.chilborne.todoapi.persistance.repository;

import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.persistance.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ToDoListRepositoryTest {

  static final String USERNAME = "user";
  static final String PASSWORD = "secret";
  static final String EMAIL = "email@me.please";
  static final String TO_DO = "To Do";
  static final String LIST_DESCRIPTION = "describing";
  @Autowired ToDoListRepository toDoListRepository;
  @Autowired UserRepository userRepository;
  private ToDoList list;
  private User user;

  @BeforeEach
  void initData() {
    user = new User(USERNAME, PASSWORD, EMAIL);
    list = new ToDoList(TO_DO, LIST_DESCRIPTION);

    list.setUser(user);
    user.addToDoList(list);

    userRepository.save(user);
    toDoListRepository.save(list);
  }

  @Test
  void getToDoListByUsernameAndIdShouldReturnCorrectToDoList() {
    // when
    Optional<ToDoList> result = toDoListRepository.findByIdAndUserUsername(list.getId(), USERNAME);

    // verify
    assertAll(
        "Optional contains ToDoList: " + list.toString(),
        () -> assertTrue(result.isPresent()),
        () -> assertEquals(list, result.get()));
  }

  @Test
  void getToDoListByUsernameAndIdShouldReturnEmptyOptionalWhenToDoListWithIdDoesNotBelongToUser() {
    // when
    Optional<ToDoList> result = toDoListRepository.findByIdAndUserUsername(list.getId(), "fails");

    // verify
    assertTrue(result.isEmpty());
  }

  @Test
  void getToDoListByUsernameAndIdShouldReturnEmptyOptionalWhenToDoListIdIsIncorrect() {
    // when
    Optional<ToDoList> result =
        toDoListRepository.findByIdAndUserUsername((long) Integer.MAX_VALUE, USERNAME);

    // verify
    assertTrue(result.isEmpty());
  }

  @Test
  void getUsersToDoListShouldReturnOnlyToDoListsWhichBelongToGivenUsername() {
    // given
    // create new User with new ToDoList that should not be included in returned List
    User secondUser = new User("second", "password", "email@gmail.es");
    ToDoList secondList = new ToDoList("Somewhat important", "not that important");
    userRepository.save(secondUser);
    toDoListRepository.save(secondList);

    // when
    List<ToDoList> result = toDoListRepository.findByUserUsername(USERNAME);

    // verify
    assertAll(
        "Returned List<ToDoList> only contains ToDoList: " + list.toString(),
        () -> assertEquals(1, result.size()),
        () -> assertEquals(list, result.get(0)));
  }

  @Test
  void existsByIdAndUsernameShouldReturnTrueWhenToDoListWithIdBelongingToUserExists() {
    // when
    boolean exists = toDoListRepository.existsByIdAndUserUsername(list.getId(), USERNAME);

    // verify
    assertTrue(exists);
  }

  @Test
  void existsByIdAndUsernameShouldReturnFalseWhenToDoListWithIdDoesNotBelongToUser() {
    // given
    user.setUsername("fails");
    userRepository.save(user);

    // when
    boolean exists = toDoListRepository.existsByIdAndUserUsername(list.getId(), USERNAME);

    // verify
    assertFalse(exists);
  }

  @Test
  void existsByIdAndUsernameShouldReturnFalseWhenToDoListWithIdBelongingToUserDoesNotExist() {
    // when
    boolean exists =toDoListRepository.existsByIdAndUserUsername((long) Integer.MAX_VALUE, USERNAME);

    // verify
    assertFalse(exists);
  }
}
