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
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@Configuration
@ActiveProfiles("!test")
public class BootstrapDatabase {

    private final ToDoListRepository toDoListRepository;
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(BootstrapDatabase.class);

    public BootstrapDatabase(
            ToDoListRepository toDoListRepository,
            UserRepository userRepository) {
        this.toDoListRepository = toDoListRepository;
        this.userRepository = userRepository;
    }

    @Bean
    CommandLineRunner init() {
        return args -> {
            logger.debug("Bootstrapping data...");
            User user = new User("user", "password", "email@test.com");

            ToDoList toDoList = new ToDoList("To Do");
            Task washingTask = new Task(toDoList, "Washing");
            Task cleaningTask = new Task(toDoList, "Cleaning");
            toDoList.setUser(user);
            toDoList.setTasks(List.of(washingTask, cleaningTask));

            user.setToDoLists(List.of(toDoList));
            userRepository.save(user);
            toDoListRepository.save(toDoList);
            logger.debug("Data bootstrapped.");
        };
    }
}
