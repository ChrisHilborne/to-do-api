package com.chilborne.todoapi.web.controller.v1;

import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.service.TaskService;
import com.chilborne.todoapi.service.ToDoListService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Task Controller")
@RestController
@RequestMapping(value = "api/v1/task")
public class TaskController {

  public static final String TASK_ROOT_URL = "http://localhost:8080/api/v1/task";
  private final TaskService taskService;
  private final Logger logger = LoggerFactory.getLogger(TaskController.class);

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @Operation(summary = "Find Task by Id", responses =
    @ApiResponse(responseCode = "404", description = "TaskNotFoundException -> Task with id:{task_id} not found"))
  @GetMapping(path ="/{id}", produces = "application/json")
  public ResponseEntity<TaskDto> getTaskById(@PathVariable long id) {
    logger.info("Processing GET Request for Task id: " + id);
    TaskDto result = taskService.getTaskDtoById(id);
    return ResponseEntity.ok(result);
  }

  @Operation(summary= "Mark Task as Complete",
    description = "Once completed a Task cannot be reactivated, date_time_finished will be generated automatically",
    responses = {
      @ApiResponse(
        responseCode = "404",
        description = "TaskNotFoundException -> Task with id:{task_id} not found"),
      @ApiResponse(
        responseCode = "208",
        description =
          "TaskAlreadyCompletedException -> This task was already completed at {date_time_finished}")
    })
  @PatchMapping(path = "/{id}/complete", produces = "application/json")
  public ResponseEntity<TaskDto> completeTask(@PathVariable long id) {
    logger.info("Processing PATCH Request to Complete Task id: " + id);
    TaskDto result = taskService.completeTask(id);
    return ResponseEntity.ok(result);
  }

  @PostMapping(path = "/", consumes = "application/json", produces = "application/json")
  public ResponseEntity<TaskDto> newTask(
    @RequestBody @Valid TaskDto taskDto) {
    logger.info("Creating new Task:{} in ToDoList id:{}", taskDto.toString(), taskDto.getListId());
    TaskDto result = taskService.newTask(taskDto);
    return ResponseEntity
      .status(HttpStatus.CREATED)
      .body(result);
  }

  @Operation(summary = "Update Task Name and/or Description",
    responses = {
        @ApiResponse(
            responseCode = "404",
            description = "TaskNotFoundException -> Task with id:{task_id} not found"),
        @ApiResponse(
            responseCode = "400",
            description = "InvalidDataException : { {task_property} : {constraint message} }")
      })
  @PatchMapping(path = "/{id}", produces = "application/json", consumes = "application/json")
  public ResponseEntity<TaskDto> updateTaskNameAndDescription(
      @PathVariable long id, @Valid @RequestBody TaskDto task) {
    logger.info("Processing PUT Request to update Task id: " + id);
    TaskDto result = taskService.updateTaskNameAndDescription(id, task);
    return ResponseEntity.ok(result);
  }
}
