package com.chilborne.todoapi.web.controller;

import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.service.ToDoListService;
import com.chilborne.todoapi.web.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/list")
public class ToDoListController {

    private final ToDoListService service;

    private final Logger logger = LoggerFactory.getLogger(ToDoListController.class);

    public ToDoListController(ToDoListService service) {
        this.service = service;
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> getToDoList(@PathVariable long id) {
        logger.info("Processing GET Request for ToDoList (id: " + id +")");
        ToDoList result;
        try {
            result = service.getToDoListById(id);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage(e));
        }

    }

    @PostMapping(value = "/new", produces = "application/json")
    public ResponseEntity<ToDoList> postToDoList(@RequestBody ToDoList list) {
        logger.info("Processing POST request for new ToDoList");
        ToDoList result = service.saveToDoList(list);
        return ResponseEntity.ok(result);
    }

    @PutMapping(value = "/{id}/active/{active}")
    public ResponseEntity<?> setActive(@PathVariable long id, @PathVariable boolean active) {
        logger.info(String.format("Setting Active of ToDoList (id: %d) to %b", id, active));
        ToDoList result;
        try {
            result = service.setActive(id, active);
            return ResponseEntity.ok(result);
        }
        catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage(e));
        }

    }

    @PutMapping(value = "/{id}/task/add", produces = "application/json")
    public ResponseEntity<?> addTaskToList(@PathVariable long id, @RequestBody Task task) {
        logger.info(String.format("Processing Request to add new Task (name: %s) to ToDoList (id: %d)", task.getName(), id));
        ToDoList result;
        try {
            result = service.addTask(id, task);
            return ResponseEntity.ok(result);
        }
        catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
