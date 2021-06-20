package com.chilborne.todoapi.web.controller;

import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.service.ToDoListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public ToDoList getToDoList(@PathVariable long id) {
        return new ToDoList("fails");
    }

    @PostMapping(value = "/new", produces = "application/json")
    public ToDoList newToDoList(@RequestBody ToDoList list) {
        return new ToDoList("fails");
    }
}
