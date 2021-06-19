package com.chilborne.todoapi.repository;

import com.chilborne.todoapi.model.ToDoList;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface ToDoListRepository extends CrudRepository<ToDoList, Long> {

    Optional<ToDoList> findByName(String name);
}
