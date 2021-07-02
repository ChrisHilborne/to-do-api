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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import java.time.format.DateTimeFormatter;

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
    TaskController taskController;

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
                        .accept("application/json")
        )
        //verify
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());

    }

    @Test
    void setTaskNameShouldReturnTaskWithNewName() throws Exception {
        //given
        String name = "new name";

        //when
        mvc.perform(
                put("/task/" + testTaskId + "/name")
                        .contentType("application/json")
                        .content("{ \"value\":\"" + name + "\"}")
        )
        //verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.task_id").value(testTaskId));

        //check DB has been updated
        testTask.setName(name);
        Task savedTask = repository.findById(testTaskId).get();
        assertTrue(testTask.equals(savedTask));
    }

    @Test
    void setTaskNameShouldReturn404WithErrorMessageWhenTaskDoesNotExist() throws Exception {
        //when
        mvc.perform(
                put("/task/500/name")
                        .contentType("application/json")
                        .content("{\"value\":\"new name\"}")
        )
        //verify
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void setTaskDescriptionShouldReturnTaskWithNewDescription() throws Exception {
        //given
        String description = "new description";

        //when
        mvc.perform(
                put("/task/" + testTaskId + "/description")
                        .contentType("application/json")
                        .content("{ \"value\":\"" + description + "\"}")
        )
        //verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.task_id").value(testTaskId));

        //check DB has been updated
        testTask.setDescription(description);
        Task savedTask = repository.findById(testTaskId).get();
        assertTrue(testTask.equals(savedTask));
    }

    @Test
    void setTaskDescriptionShouldReturn404WithErrorMessageWhenTaskDoesNotExist() throws Exception {
        //when
        mvc.perform(
                put("/task/500/description")
                        .contentType("application/json")
                        .content("{\"value\":\"new description\"}")
        )
        //verify
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void completeTaskShouldReturnCompletedTaskWhenTaskExistsAndHasNotBeenCompletedBefore() throws Exception {
        //when
        mvc.perform(
                put("/task/" + testTaskId + "/complete")
        )
        //verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.time_completed").isNotEmpty());

        //check DB has been updated
        testTask.complete();
        Task savedTask = repository.findById(testTaskId).get();
        assertTrue(testTask.equals(savedTask));
    }

    @Test
    void completeTaskShouldReturn208IfTaskExistsButHasAlreadyBeenCompleted() throws Exception {
        //given
        testTask.complete();
        repository.save(testTask);

        //when
        mvc.perform(
                put("/task/" + testTaskId + "/complete")
        )
                //verify
                .andExpect(status().isAlreadyReported())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void completeTaskShouldReturn404IfTaskDoesNotExist() throws Exception {
        //when
        mvc.perform(
                put("/task/500/complete")
                        .accept("application/json")
        )
                //verify
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());

    }

}
