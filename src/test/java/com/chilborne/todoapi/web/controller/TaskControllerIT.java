package com.chilborne.todoapi.web.controller;

import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.repository.TaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class TaskControllerIT {

    @Autowired
    MockMvc mvc;

    @Autowired
    TaskRepository repository;

    Task testTask;

    long testTaskId;

    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @BeforeEach
    void initialiseTaskData() {
        testTask = new Task("test task");
        repository.save(testTask);
        testTaskId = testTask.getTaskId();
    }

    @AfterEach
    void tearDownTaskData() {
        repository.deleteAll();
    }

    @Test
    void getTaskShouldReturnTaskWhenTheTaskExists() throws Exception {
        //when
        mvc.perform(
                get("/task/" + testTaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept("application/json")
        )
        //verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testTask.getName()))
                .andExpect(jsonPath("$.task_id").value(testTaskId))
                .andExpect(jsonPath("$.time_created").value(testTask.getTimeCreated().format(FORMATTER)));
    }

    @Test
    void getTaskByIdShouldReturn404WithErrorMessageWhenTaskDoesNotExist() throws Exception {
        //when
        mvc.perform(
                get("/task/500")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept("application/json")
        )
        //verify
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());

    }

    @Test
    void completeTaskShouldReturnCompletedTaskWhenTaskExistsAndHasNotBeenCompletedBefore() throws Exception {
        //when
        mvc.perform(
                patch("/task/" + testTaskId + "/complete")
                        .contentType(MediaType.APPLICATION_JSON)
        )
        //verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.time_completed").isNotEmpty());

        //check DB has been updated
        testTask.complete();
        Task savedTask = repository.findById(testTaskId).get();
        assertEquals(savedTask, testTask);
    }

    @Test
    void completeTaskShouldReturn208IfTaskExistsButHasAlreadyBeenCompleted() throws Exception {
        //given
        testTask.complete();
        repository.save(testTask);

        //when
        mvc.perform(
                patch("/task/" + testTaskId + "/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                //verify
                .andExpect(status().isAlreadyReported())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void completeTaskShouldReturn404IfTaskDoesNotExist() throws Exception {
        //when
        mvc.perform(
                patch("/task/500/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept("application/json")
        )
                //verify
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());

    }

    @Test
    void updateTaskShouldReturnUpdatedTaskWhenTaskExists() throws Exception {
        //given
        String taskJson = """
                {
                    "name" : "new name",
                    "description" : "new description"            
                }
                """;

        //when
        mvc.perform(
                put("/task/{id}", testTaskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("new name"))
                .andExpect(jsonPath("$.description").value("new description"));
    }

    @Test
    void updateTaskShouldReturn404IfTaskDoesNotExist() throws Exception {
        //given
        String taskJson = """
                {
                    "name" : "new name",
                    "description" : "new description"            
                }
                """;

        //when
        mvc.perform(
                put("/task/{id}", 50)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

}
