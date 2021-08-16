package com.chilborne.todoapi.web.controller.v1;

import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Api(value = "Task Controller")
@RestController
@RequestMapping(value = "api/v1/task")
public class TaskController {

  public static final String TASK_ROOT_URL = "http://localhost:8080/api/v1/task";
  private final TaskService service;
  private final Logger logger = LoggerFactory.getLogger(TaskController.class);

  public TaskController(TaskService service) {
    this.service = service;
  }

  @ApiOperation(value = "Find Task by Id")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 404,
            message = "TaskNotFoundException -> Task with id:{task_id} not found")
      })
  @GetMapping(path ="/{id}", produces = "application/json")
  public ResponseEntity<TaskDto> getTaskById(@PathVariable long id) {
    logger.info("Processing GET Request for Task id: " + id);
    TaskDto result = service.getTaskDtoById(id);
    return ResponseEntity.ok(result);
  }

  @ApiOperation(
      value = "Mark Task as Complete",
      notes =
          "Once completed a Task cannot be reactivated, date_time_finished will be generated automatically")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 404,
            message = "TaskNotFoundException -> Task with id:{task_id} not found"),
        @ApiResponse(
            code = 208,
            message =
                "TaskAlreadyCompletedException -> This task was already completed at {date_time_finished}")
      })
  @PatchMapping(path = "/{id}/complete", produces = "application/json")
  public ResponseEntity<TaskDto> completeTask(@PathVariable long id) {
    logger.info("Processing PATCH Request to Complete Task id: " + id);
    TaskDto result = service.completeTask(id);
    return ResponseEntity.ok(result);
  }

  @ApiOperation(value = "Update Task Name and/or Description")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 404,
            message = "TaskNotFoundException -> Task with id:{task_id} not found"),
        @ApiResponse(
            code = 400,
            message = "InvalidDataException : { {task_property} : {constraint message} }")
      })
  @PatchMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
  public ResponseEntity<TaskDto> updateTaskNameAndDescription(
      @PathVariable long id, @Valid @RequestBody TaskDto task) {
    logger.info("Processing PUT Request to update Task id: " + id);
    TaskDto result = service.updateTaskNameAndDescription(id, task);
    return ResponseEntity.ok(result);
  }
}
