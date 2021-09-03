package com.chilborne.todoapi.persistance.bootstrap;

import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.persistance.model.User;
import com.chilborne.todoapi.persistance.repository.ToDoListRepository;
import com.chilborne.todoapi.persistance.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

//@Component
@Profile("!test")
public class BootstrapData implements CommandLineRunner {

  private final ToDoListRepository toDoListRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder encoder;
  private final Logger logger = LoggerFactory.getLogger(BootstrapData.class);

  public BootstrapData(
      ToDoListRepository toDoListRepository,
      UserRepository userRepository,
      PasswordEncoder encoder) {
    this.toDoListRepository = toDoListRepository;
    this.userRepository = userRepository;
    this.encoder = encoder;
  }

  @Override
  public void run(String... args) throws Exception {
      logger.debug("Bootstrapping data...");
      User user = new User("user", encoder.encode("password"), "email@test.com");

      ToDoList toDoList = new ToDoList("To Do");
      Task washingTask = new Task(toDoList, "Washing");
      Task cleaningTask = new Task(toDoList, "Cleaning");
      toDoList.setUser(user);
      toDoList.setTasks(List.of(washingTask, cleaningTask));

      user.setToDoLists(List.of(toDoList));
      userRepository.save(user);
      toDoListRepository.save(toDoList);
      logger.debug("Data bootstrapped.");
  }

}
