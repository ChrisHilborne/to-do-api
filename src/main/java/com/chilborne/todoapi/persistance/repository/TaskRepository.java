package com.chilborne.todoapi.persistance.repository;

import com.chilborne.todoapi.persistance.model.Task;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TaskRepository extends CrudRepository<Task, Long> {

  Optional<Task> findById(long id);
}
