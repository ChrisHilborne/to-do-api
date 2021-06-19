package com.chilborne.todoapi.repository;

import com.chilborne.todoapi.model.Task;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Task, Long> {
}
