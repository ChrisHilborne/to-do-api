package com.chilborne.todoapi.web.controller;

import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping(value = "/task", consumes = "application/json", produces = "application/json")
public class TaskController {

    private final TaskService service;
    private final Logger logger = LoggerFactory.getLogger(TaskController.class);

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(
            @PathVariable long id) {
        logger.info("Processing GET Request for Task id: " + id);
        TaskDto result = service.getTaskById(id);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskDto> completeTask(
            @PathVariable long id) {
        logger.info("Processing PATCH Request to Complete Task id: " + id);
        TaskDto result = service.completeTask(id);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable long id,
            @Valid @RequestBody TaskDto task) {
        logger.info("Processing PUT Request to update Task id: " + id);
        TaskDto result = service.updateTask(id, task);
        return ResponseEntity.ok(result);
    }


}
