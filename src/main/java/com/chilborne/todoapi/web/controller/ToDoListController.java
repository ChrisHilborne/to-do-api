package com.chilborne.todoapi.web.controller;

import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.service.ToDoListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;

@RestController
@RequestMapping("/list")
public class ToDoListController {

    private final ToDoListService service;

    private final Logger logger = LoggerFactory.getLogger(ToDoListController.class);

    public ToDoListController(ToDoListService service) {
        this.service = service;
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<ToDoList> getToDoList(@PathVariable long id) {
        logger.info("Processing GET Request for ToDoList (id: " + id +")");
        ToDoList result;
        try {
            result = service.getToDoListById(id);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }

    @PostMapping(value = "/new", produces = "application/json")
    public ResponseEntity<ToDoList> postToDoList(@RequestBody ToDoList list) {
        logger.info("Processing POST request for new ToDoList");
        ToDoList result = service.saveToDoList(list);
        return ResponseEntity.ok(result);
    }

    @PutMapping(value = "/{id}/active/{active}")
    public ResponseEntity<ToDoList> setActive(@PathVariable long id, @PathVariable boolean active) {
        logger.info(String.format("Setting Active of ToDoList (id: %d) to %b", id, active));
        return ResponseEntity.ok(new ToDoList("fails"));

    }
}
