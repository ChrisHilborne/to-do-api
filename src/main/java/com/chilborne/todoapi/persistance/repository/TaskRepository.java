package com.chilborne.todoapi.persistance.repository;

import com.chilborne.todoapi.persistance.model.Task;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Task, Long> {
}
