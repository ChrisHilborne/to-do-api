package com.chilborne.todoapi.persistance.repository;

import com.chilborne.todoapi.persistance.model.ToDoList;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ToDoListRepository extends CrudRepository<ToDoList, Long> {

  void deleteById(long id);

  @NotNull
  List<ToDoList> findAll();

  Optional<ToDoList> findByIdAndUserUsername(
      long id, String username);

  List<ToDoList> findByUserUsername(String username);

  boolean existsByIdAndUserUsername(long id, String username);
}
