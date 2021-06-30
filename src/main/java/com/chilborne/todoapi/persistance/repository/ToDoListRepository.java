package com.chilborne.todoapi.persistance.repository;

import com.chilborne.todoapi.persistance.model.ToDoList;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface ToDoListRepository extends CrudRepository<ToDoList, Long> {

    void deleteById(Long id);

    List<ToDoList> findAll();
}
