package com.chilborne.todoapi.persistance.repository;

import com.chilborne.todoapi.persistance.model.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

public interface TaskRepository extends CrudRepository<Task, Long> {
}
