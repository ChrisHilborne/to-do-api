package com.chilborne.todoapi.bootstrap;

import com.chilborne.todoapi.model.Task;
import com.chilborne.todoapi.model.ToDoList;
import com.chilborne.todoapi.repository.ToDoListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class LoadDataBase {

    private final ToDoListRepository repository;
    private final Logger logger = LoggerFactory.getLogger(LoadDataBase.class);

    public LoadDataBase(ToDoListRepository repository) {
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
