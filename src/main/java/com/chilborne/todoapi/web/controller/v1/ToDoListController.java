package com.chilborne.todoapi.web.controller.v1;

import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.dto.ToDoListDto;
import com.chilborne.todoapi.persistance.validation.OnPersist;
import com.chilborne.todoapi.service.ToDoListService;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.service.ToDoListServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "To Do List Controller")
@RestController
@RequestMapping(path = "v1/list", consumes = "application/json", produces = "application/json")
public class ToDoListController {

    public static final String TO_DO_LIST_ROOT_URL = "http://localhost:8080/vi/list";

    private final ToDoListService service;

    private final Logger logger = LoggerFactory.getLogger(ToDoListController.class);

    public ToDoListController(ToDoListServiceImpl service) {
        this.service = service;
    }

    @ApiOperation(value = "Find To-Do List by Id")
    @GetMapping(value = "/{id}")
    public ResponseEntity<ToDoListDto> getToDoListById(
            @PathVariable long id) {
        logger.info("Processing GET Request for ToDoList (id: " + id +")");
        ToDoListDto result = service.getToDoListDtoById(id);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "New To-Do List", code = 401)
    @PostMapping(value = "")
    @Validated({OnPersist.class})
    public ResponseEntity<ToDoListDto> newToDoList(
            @Valid @RequestBody ToDoListDto list) {
        logger.info("Processing POST request for new ToDoList");
        ToDoListDto result = service.saveToDoList(list);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @ApiOperation(value = "Change whether To-Do List is active")
    @PatchMapping(value = "/{id}/active/{active}")
    public ResponseEntity<ToDoListDto> setActive(
            @PathVariable long id,
            @PathVariable boolean active) {
        logger.info(String.format("Setting Active of ToDoList (id: %d) to %b", id, active));
        ToDoListDto result = service.setToDoListActive(id, active);
        return ResponseEntity.ok(result);

    }

    @ApiOperation(value = "Add a new Task to To-Do List", notes = "Must provide Task to be added in Request Body")
    @PatchMapping(value = "/{id}/task/add")
    @Validated({OnPersist.class})
    public ResponseEntity<ToDoListDto> addTaskToList(
            @PathVariable long id,
            @Valid @RequestBody TaskDto task) {
        logger.info(String.format("Processing PATCH Request to add new Task (name: %s) to ToDoList (id: %d)", task.getName(), id));
        ToDoListDto result = service.addTaskToDoList(id, task);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Remove a Task from To-Do List")
    @PatchMapping(value = "/{listId}/task/remove/{taskId}")
    public ResponseEntity<ToDoListDto> removeTaskFromList(
            @PathVariable long listId,
            @PathVariable long taskId) {
        logger.info(String.format("Removing Task with id %d from ToDoList with id %d", taskId, listId));
        ToDoListDto result = service.removeTaskToDoList(listId, taskId);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Update To-Do List", notes = "Any fields provided will be updated, except list_id and date_time_created")
    @PutMapping(value ="/{id}")
    public ResponseEntity<ToDoListDto> updateToDoList(
            @PathVariable long id,
            @Valid @RequestBody ToDoListDto toDoList) {
        logger.info("Processing PUT Request to update List id: " + id);
        ToDoListDto result = service.updateToDoList(id, toDoList);
        return ResponseEntity.ok(result);
    }
}
