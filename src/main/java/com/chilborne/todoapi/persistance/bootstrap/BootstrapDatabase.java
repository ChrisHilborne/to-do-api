package com.chilborne.todoapi.persistance.bootstrap;

import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.persistance.repository.ToDoListRepository;
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

    private final ToDoListRepository repository;
    private final Logger logger = LoggerFactory.getLogger(BootstrapDatabase.class);

    public BootstrapDatabase(ToDoListRepository repository) {
        this.repository = repository;
    }

    @Bean
    CommandLineRunner init() {
        return args -> {
            logger.debug("Bootstrapping data...");
            ToDoList toDoList = new ToDoList("To Do");
            Task washingTask = new Task(toDoList, "Washing");
            Task cleaningTask = new Task(toDoList, "Cleaning");
            toDoList.setTasks(List.of(washingTask, cleaningTask));

            repository.save(toDoList);
            logger.debug("Data bootstrapped.");
        };
    }
}
