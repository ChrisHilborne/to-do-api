package com.chilborne.todoapi.persistance.repository;

import com.chilborne.todoapi.persistance.model.ToDoList;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.List;

public interface ToDoListRepository extends CrudRepository<ToDoList, Long> {

    void deleteById(long id);

    @NotNull
    List<ToDoList> findAll();
}
