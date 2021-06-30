package com.chilborne.todoapi.web.controller;

import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.service.TaskService;
import com.chilborne.todoapi.web.dto.SingleValueDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService service;
    private final Logger logger = LoggerFactory.getLogger(TaskController.class);

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(@PathVariable long id) {
        logger.info("Processing Request for Task id: " + id);
        Task result = service.getTaskById(id);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Task> completeTask(@PathVariable long id) {
        logger.info("Processing Request to Complete Task id: " + id);
        Task result = service.completeTask(id);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/name")
    public ResponseEntity<Task> setTaskName(
            @PathVariable long id,
            @RequestBody SingleValueDTO<String> nameDTO)
    {
        logger.info(String.format(
                "Processing Request to set Task (id:%d) name to: %s",
                id,
                nameDTO.getValue())
        );
        Task result = service.setTaskName(id, nameDTO);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/description")
    public ResponseEntity<Task> setTaskDescription(
            @PathVariable long id,
            @RequestBody SingleValueDTO<String> descriptionDTO)
    {
        logger.info(String.format(
                "Processing Request to set Task (id:%d) description to: %s",
                id,
                descriptionDTO.getValue())
        );
        Task result = service.setTaskDescription(id, descriptionDTO);
        return ResponseEntity.ok(result);
    }
}
