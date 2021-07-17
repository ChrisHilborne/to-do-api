package com.chilborne.todoapi.web.controller.v1;

import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.service.TaskService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "Task Controller")
@RestController
@RequestMapping(value = "v1/task", consumes = "application/json", produces = "application/json")
public class TaskController {

    public static final String TASK_ROOT_URL = "http://localhost:8080/v1/task";


    private final TaskService service;
    private final Logger logger = LoggerFactory.getLogger(TaskController.class);

    public TaskController(TaskService service) {
        this.service = service;
    }

    @ApiOperation(value = "Find Task by Id")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "TaskNotFoundException -> Task with id:{task_id} not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(
            @PathVariable long id) {
        logger.info("Processing GET Request for Task id: " + id);
        TaskDto result = service.getTaskById(id);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Mark Task as Complete", notes = "Once completed a Task cannot be reactivated, date_time_finished will be generated automatically")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "TaskNotFoundException -> Task with id:{task_id} not found"),
            @ApiResponse(code = 208, message = "TaskAlreadyCompletedException -> This task was already completed at {date_time_finished}")
    })
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskDto> completeTask(
            @PathVariable long id) {
        logger.info("Processing PATCH Request to Complete Task id: " + id);
        TaskDto result = service.completeTask(id);
        return ResponseEntity.ok(result);
    }

    @ApiOperation(value = "Update Task")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "TaskNotFoundException -> Task with id:{task_id} not found"),
            @ApiResponse(code = 400, message = "InvalidDataException : { {task_property} : {constraint message} }")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable long id,
            @Valid @RequestBody TaskDto task) {
        logger.info("Processing PUT Request to update Task id: " + id);
        TaskDto result = service.updateTask(id, task);
        return ResponseEntity.ok(result);
    }


}
