package com.chilborne.todoapi.web.controller.v1;

import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.dto.ToDoListDto;
import com.chilborne.todoapi.persistance.validation.OnPersist;
import com.chilborne.todoapi.service.ToDoListService;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.service.ToDoListServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "To Do List Controller")
@RestController
@RequestMapping(path = "api/v1/list", consumes = "application/json", produces = "application/json")
public class ToDoListController {

    public static final String TO_DO_LIST_ROOT_URL = "http://localhost:8080/vi/list";

    private final ToDoListService service;

    private final Logger logger = LoggerFactory.getLogger(ToDoListController.class);

    public ToDoListController(ToDoListServiceImpl service) {
        this.service = service;
    }

    @ApiOperation(value = "Find to_do_list by Id")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "ToDoListNotFoundException -> to_do_list with id:{id} not found")
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<ToDoListDto> getToDoListById(
            @PathVariable long id) {
        logger.info("Processing GET Request for ToDoList (id: " + id +")");
        ToDoListDto result = service.getToDoListDtoById(id);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "New to_do_list", code = 401)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "InvalidDataException: { {to_do_list_property} : {constraint_message} }")
    })
    @PostMapping(value = "")
    @Validated({OnPersist.class})
    public ResponseEntity<ToDoListDto> newToDoList(
            @Valid @RequestBody ToDoListDto list) {
        logger.info("Processing POST request for new ToDoList");
        ToDoListDto result = service.saveToDoList(list);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @ApiOperation(value = "Update to_do_list", notes = "Any fields provided will be updated, except list_id and date_time_created")
    @PutMapping(value ="/{id}")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "ToDoListNotFoundException -> to_do_list with id:{id} not found"),
            @ApiResponse(code = 400, message = "InvalidDataException: { {to_do_list_property} : {constraint_message} }")
    })
    public ResponseEntity<ToDoListDto> updateToDoList(
            @PathVariable long id,
            @Valid @RequestBody ToDoListDto toDoList) {
        logger.info("Processing PUT Request to update List id:" + id);
        ToDoListDto result = service.updateToDoList(id, toDoList);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Delete to_do_list")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "ToDoListNotFoundException -> to_do_list with id:{id} not found")
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteToDoList(
            @PathVariable long id
    ) {
        logger.info("Processing DELETE Request for List id:" + id);
        service.deleteToDoList(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @ApiOperation(value = "Change whether to_do_list is active")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "ToDoListNotFoundException -> to_do_list with id:{id} not found")
    })
    @PatchMapping(value = "/{id}/active/{active}")
    public ResponseEntity<ToDoListDto> setActive(
            @PathVariable long id,
            @PathVariable boolean active) {
        logger.info(String.format("Setting Active of ToDoList (id: %d) to %b", id, active));
        ToDoListDto result = service.setToDoListActive(id, active);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Add a new Task to to_do_list", notes = "Must provide Task to be added in Request Body")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "ToDoListNotFoundException -> to_do_list with id:{id} not found"),
            @ApiResponse(code = 400, message = "InvalidDataException: { {task_property} : {constraint_message} }")
    })
    @PatchMapping(value = "/{id}/task/add")
    @Validated({OnPersist.class})
    public ResponseEntity<ToDoListDto> addTaskToList(
            @PathVariable long id,
            @Valid @RequestBody TaskDto task) {
        logger.info(String.format("Processing PATCH Request to add new Task (name: %s) to ToDoList (id: %d)", task.getName(), id));
        ToDoListDto result = service.addTaskToDoList(id, task);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Remove a Task from to_do_list")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "ToDoListNotFoundException -> to_do_list with id:{id} not found"),
            @ApiResponse(code = 404, message = "TaskNotFoundException -> to_do_list with id:{list_id} does not contain task with id:{task_id}")
    })
    @PatchMapping(value = "/{listId}/task/remove/{taskId}")
    public ResponseEntity<ToDoListDto> removeTaskFromList(
            @PathVariable long listId,
            @PathVariable long taskId) {
        logger.info(String.format("Removing Task with id %d from ToDoList with id %d", taskId, listId));
        ToDoListDto result = service.removeTaskToDoList(listId, taskId);
        return ResponseEntity.ok(result);
    }

}
