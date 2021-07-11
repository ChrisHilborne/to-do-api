package com.chilborne.todoapi.web.controller.v1;

import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.dto.ToDoListDto;
import com.chilborne.todoapi.persistance.validation.OnPersist;
import com.chilborne.todoapi.service.ToDoListService;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.service.ToDoListServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "v1/list", consumes = "application/json", produces = "application/json")
public class ToDoListController {

    private final ToDoListService service;

    private final Logger logger = LoggerFactory.getLogger(ToDoListController.class);

    public ToDoListController(ToDoListServiceImpl service) {
        this.service = service;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ToDoListDto> getToDoListById(
            @PathVariable long id) {
        logger.info("Processing GET Request for ToDoList (id: " + id +")");
        ToDoListDto result = service.getToDoListDtoById(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "")
    @Validated({OnPersist.class})
    public ResponseEntity<ToDoListDto> newToDoList(
            @Valid @RequestBody ToDoListDto list) {
        logger.info("Processing POST request for new ToDoList");
        ToDoListDto result = service.saveToDoList(list);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PatchMapping(value = "/{id}/active/{active}")
    public ResponseEntity<ToDoListDto> setActive(
            @PathVariable long id,
            @PathVariable boolean active) {
        logger.info(String.format("Setting Active of ToDoList (id: %d) to %b", id, active));
        ToDoListDto result = service.setToDoListActive(id, active);
        return ResponseEntity.ok(result);

    }

    @PatchMapping(value = "/{id}/task/add")
    public ResponseEntity<ToDoListDto> addTaskToList(
            @PathVariable long id,
            @Valid @RequestBody TaskDto task) {
        logger.info(String.format("Processing PATCH Request to add new Task (name: %s) to ToDoList (id: %d)", task.getName(), id));
        ToDoListDto result = service.addTaskToDoList(id, task);
        return ResponseEntity.ok(result);
    }

    @PatchMapping(value = "/{listId}/task/remove/{taskId}")
    public ResponseEntity<ToDoListDto> removeTaskFromList(
            @PathVariable long listId,
            @PathVariable long taskId) {
        logger.info(String.format("Removing Task with id %d from ToDoList with id %d", taskId, listId));
        ToDoListDto result = service.removeTaskToDoList(listId, taskId);
        return ResponseEntity.ok(result);
    }

    @PutMapping(value ="/{id}")
    public ResponseEntity<ToDoListDto> updateToDoList(
            @PathVariable long id,
            @Valid @RequestBody ToDoListDto toDoList) {
        logger.info("Processing PUT Request to update List id: " + id);
        ToDoListDto result = service.updateToDoList(id, toDoList);
        return ResponseEntity.ok(result);
    }
}
