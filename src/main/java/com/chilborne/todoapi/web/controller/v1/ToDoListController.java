package com.chilborne.todoapi.web.controller.v1;

import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.dto.ToDoListDto;
import com.chilborne.todoapi.persistance.validation.OnPersist;
import com.chilborne.todoapi.service.ToDoListService;
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
import java.security.Principal;
import java.util.List;

@Api(
    value = "To Do List Controller",
    position = 0,
    basePath = "api/v1/list",
    description = "Create, Read, Update and Delete to_do_lists belonging to authenticated user")
@RestController
@RequestMapping(path = "api/v1/list")
public class ToDoListController {

  public static final String TO_DO_LIST_ROOT_URL = "http://localhost:8080/v1/list";
  private final ToDoListService service;
  private final Logger logger = LoggerFactory.getLogger(ToDoListController.class);

  public ToDoListController(ToDoListServiceImpl service) {
    this.service = service;
  }

  @ApiOperation(value = "Find to_do_list by Id", position = 2, produces = "application/jsom")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 404,
            message =
                "ToDoListNotFoundException -> to_do_list with id:{id} belonging to User:{username} not found")
      })
  @GetMapping(path = "/{id}", produces = "application/json")
  public ResponseEntity<ToDoListDto> getToDoListById(@PathVariable long id, Principal principal) {
    logger.info("Processing GET Request for ToDoList (id: " + id + ")");
    ToDoListDto result = service.getToDoListDtoById(id, principal.getName());
    return ResponseEntity.ok(result);
  }

  @ApiOperation(
      value = "Find all to_do_lists belonging to authenticated user",
      position = 3,
      produces = "application/jsom")
  @GetMapping(path = "/all", produces = "application/json")
  public ResponseEntity<List<ToDoListDto>> getAllToDoLists(Principal principal) {
    logger.debug(
        "Processing GET Request for all ToDoLists beloning to User:{}", principal.getName());
    List<ToDoListDto> result = service.getAllToDoList(principal.getName());
    return ResponseEntity.ok(result);
  }

  @ApiOperation(
      value = "Create to_do_list",
      code = 401,
      position = 1,
      consumes = "application/jsom",
      produces = "application/jsom")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 400,
            message = "InvalidDataException: { {to_do_list_property} : {constraint_message} }")
      })
  @PostMapping(path = "", produces = "application/json", consumes = "application/json")
  @Validated({OnPersist.class})
  public ResponseEntity<ToDoListDto> newToDoList(
      @RequestBody @Valid ToDoListDto list, Principal principal) {
    logger.info("Processing POST request for new ToDoList");
    ToDoListDto result = service.newToDoList(list, principal.getName());
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  @ApiOperation(
      value = "Update to_do_list name and/or description",
      position = 4,
      produces = "application/json",
      consumes = "application/json")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 404,
            message = "ToDoListNotFoundException -> to_do_list with id:{id} not found"),
        @ApiResponse(
            code = 400,
            message = "InvalidDataException: { {to_do_list_property} : {constraint_message} }")
      })
  @PutMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
  public ResponseEntity<ToDoListDto> updateToDoListNameAndDescription(
      @PathVariable long id,
      @RequestBody @Valid ToDoListDto toDoList,
      Principal principal) {
    logger.info("Processing PUT Request to update ToDoList:{} to {}", id, toDoList.toString());
    ToDoListDto result =
        service.updateToDoListNameAndDescription(id, toDoList, principal.getName());
    return ResponseEntity.ok(result);
  }

  @ApiOperation(value = "Delete to_do_list", position = 5)
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 404,
            message =
                "ToDoListNotFoundException -> to_do_list with id:{id} belonging to User:{username} not found")
      })
  @DeleteMapping(value = "/{id}")
  public ResponseEntity deleteToDoList(@PathVariable long id, Principal principal) {
    logger.info("Processing DELETE Request for List id:" + id);
    service.deleteToDoList(id, principal.getName());
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @ApiOperation(value = "Change whether to_do_list is active", position = 6)
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 404,
            message =
                "ToDoListNotFoundException -> to_do_list with id:{id} belonging to User:{username} not found")
      })
  @PatchMapping(path = "/{id}/active/{active}", produces = "application/json")
  public ResponseEntity<ToDoListDto> setActive(
      @PathVariable long id, @PathVariable boolean active, Principal principal) {
    logger.info(String.format("Setting Active of ToDoList (id: %d) to %b", id, active));
    ToDoListDto result = service.setToDoListActive(id, principal.getName(), active);
    return ResponseEntity.ok(result);
  }

  @ApiOperation(
      value = "Add a new task to to_do_list",
      position = 7,
      notes = "Must provide task to be added in Request Body")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 404,
            message = "ToDoListNotFoundException -> to_do_list with id:{id} not found"),
        @ApiResponse(
            code = 400,
            message = "InvalidDataException: { {task_property} : {constraint_message} }")
      })
  @PatchMapping(
      path = "/{id}/task/add",
      produces = "application/json",
      consumes = "application/json")
  @Validated({OnPersist.class})
  public ResponseEntity<ToDoListDto> addTaskToList(
      @PathVariable long id,
      @RequestBody @Valid TaskDto task,
      Principal principal) {
    logger.info(
        String.format(
            "Processing PATCH Request to add new Task (name: %s) to ToDoList (id: %d)",
            task.getName(), id));
    ToDoListDto result = service.addTaskToDoList(id, principal.getName(), task);
    return ResponseEntity.ok(result);
  }

  @ApiOperation(value = "Delete task from to_do_list", position = 8)
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 404,
            message = "ToDoListNotFoundException -> to_do_list with id:{id} not found"),
        @ApiResponse(
            code = 404,
            message =
                "TaskNotFoundException -> to_do_list with id:{list_id} does not contain task with id:{task_id}")
      })
  @PatchMapping(path = "/{listId}/task/remove/{taskId}", produces = "application/json")
  public ResponseEntity<ToDoListDto> removeTaskFromList(
      @PathVariable long listId, @PathVariable long taskId, Principal principal) {
    logger.info(String.format("Removing Task with id %d from ToDoList with id %d", taskId, listId));
    ToDoListDto result = service.removeTaskFromToDoList(listId, principal.getName(), taskId);
    return ResponseEntity.ok(result);
  }
}
