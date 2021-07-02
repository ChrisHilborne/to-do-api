package com.chilborne.todoapi.persistance.repository;

import com.chilborne.todoapi.persistance.model.ToDoList;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface ToDoListRepository extends CrudRepository<ToDoList, Long> {

    void deleteById(long id);

    @NotNull
    List<ToDoList> findAll();
}
