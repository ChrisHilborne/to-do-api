package com.chilborne.todoapi.web.controller.v1;

import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.dto.ToDoListDto;
import com.chilborne.todoapi.persistance.validation.OnPersist;
import com.chilborne.todoapi.service.ToDoListService;
import com.chilborne.todoapi.service.ToDoListServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Tag(
    name = "To Do List Controller",
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

  @Operation(description = "Find to_do_list by Id",
    responses = @ApiResponse(
            responseCode = "404",
            description = "ToDoListNotFoundException -> to_do_list with id:{id} belonging to User:{username} not found"))
  @GetMapping(path = "/{id}", produces = "application/json")
  public ResponseEntity<ToDoListDto> getToDoListById(@PathVariable long id, Principal principal) {
    logger.info("Processing GET Request for ToDoList (id: " + id + ")");
    ToDoListDto result = service.getToDoListDtoById(id, principal.getName());
    return ResponseEntity.ok(result);
  }

  @Operation(
      summary = "Find all to_do_lists belonging to authenticated user")
  @GetMapping(path = "/all", produces = "application/json")
  public ResponseEntity<List<ToDoListDto>> getAllToDoLists(Principal principal) {
    logger.debug(
        "Processing GET Request for all ToDoLists beloning to User:{}", principal.getName());
    List<ToDoListDto> result = service.getAllToDoList(principal.getName());
    return ResponseEntity.ok(result);
  }

  @Operation( summary = "Create to_do_list")
  @ApiResponse( responseCode = "400", description = "InvalidDataException: { {to_do_list_property} : {constraint_message} }")
  @PostMapping( path = "", produces = "application/json", consumes = "application/json")
  @Validated({OnPersist.class})
  public ResponseEntity<ToDoListDto> newToDoList(
      @RequestBody @Valid ToDoListDto list, Principal principal) {
    logger.info("Processing POST request for new ToDoList");
    ToDoListDto result = service.newToDoList(list, principal.getName());
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }

  @Operation( summary = "Update to_do_list name and/or description",
   responses = {
    @ApiResponse( responseCode = "404", description = "ToDoListNotFoundException -> to_do_list with id:{id} not found"),
    @ApiResponse( responseCode = "400", description = "InvalidDataException: { {to_do_list_property} : {constraint_message} }")})
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

  @Operation(summary = "Delete to_do_list",
    responses = @ApiResponse( responseCode = "404", description = "ToDoListNotFoundException -> to_do_list with id:{id} belonging to User:{username} not found")
  )
  @DeleteMapping(value = "/{id}")
  public ResponseEntity deleteToDoList(@PathVariable long id, Principal principal) {
    logger.info("Processing DELETE Request for List id:" + id);
    service.deleteToDoList(id, principal.getName());
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(summary = "Change whether to_do_list is active",
  responses =
        @ApiResponse( responseCode = "404", description = "ToDoListNotFoundException -> to_do_list with id:{id} belonging to User:{username} not found"))
  @PatchMapping(path = "/{id}/active/{active}", produces = "application/json")
  public ResponseEntity<ToDoListDto> setActive(
      @PathVariable long id, @PathVariable boolean active, Principal principal) {
    logger.info(String.format("Setting Active of ToDoList (id: %d) to %b", id, active));
    ToDoListDto result = service.setToDoListActive(id, principal.getName(), active);
    return ResponseEntity.ok(result);
  }

  @Operation(
      summary = "Add a new task to to_do_list",
      responses = {
        @ApiResponse(
            responseCode = "404",
            description = "ToDoListNotFoundException -> to_do_list with id:{id} not found"),
        @ApiResponse(
            responseCode= "400",
            description = "InvalidDataException: { {task_property} : {constraint_message} }")
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

  @Operation(summary = "Delete task from to_do_list",
      responses =
      {
        @ApiResponse(
            responseCode = "404",
            description = "ToDoListNotFoundException -> to_do_list with id:{id} not found"),
        @ApiResponse(
            responseCode = "404",
            description = "TaskNotFoundException -> to_do_list with id:{list_id} does not contain task with id:{task_id}")
      })
  @PatchMapping(path = "/{listId}/task/remove/{taskId}", produces = "application/json")
  public ResponseEntity<ToDoListDto> removeTaskFromList(
      @PathVariable long listId, @PathVariable long taskId, Principal principal) {
    logger.info(String.format("Removing Task with id %d from ToDoList with id %d", taskId, listId));
    ToDoListDto result = service.removeTaskFromToDoList(listId, principal.getName(), taskId);
    return ResponseEntity.ok(result);
  }
}
