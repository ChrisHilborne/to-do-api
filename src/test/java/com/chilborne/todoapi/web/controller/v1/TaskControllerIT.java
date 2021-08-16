package com.chilborne.todoapi.web.controller.v1;

import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.persistance.model.User;
import com.chilborne.todoapi.persistance.repository.TaskRepository;
import com.chilborne.todoapi.persistance.repository.ToDoListRepository;
import com.chilborne.todoapi.persistance.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class TaskControllerIT {

    private static final String USERNAME = "user name";
    private static final String PASSWORD = "secr3t";
    private User testUser;
    private ToDoList testList;
    private Task testTask;
    private long ID;

    @Autowired MockMvc mvc;
    @Autowired TaskRepository taskRepository;
    @Autowired ToDoListRepository toDoListRepository;
    @Autowired UserRepository userRepository;

    @BeforeEach
    void initialiseTaskData() {
        testTask = new Task("test task");
        testList = new ToDoList("test list");
        testUser = new User(USERNAME, PASSWORD);

        testTask.setToDoList(testList);
        testList.addTask(testTask);
        testList.setUser(testUser);
        testUser.addToDoList(testList);

        userRepository.save(testUser);
        toDoListRepository.save(testList);
        taskRepository.save(testTask);
        ID = testTask.getId();
    }

    @AfterEach
    void tearDownTaskData() {
        userRepository.deleteAll();
        toDoListRepository.deleteAll();
        taskRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = USERNAME)
    void getTaskShouldReturnTaskWhenTheTaskExists() throws Exception {
        //when
        mvc.perform(
                get("/api/v1/task/" + ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept("application/json")
        )
        //verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testTask.getName()))
                .andExpect(jsonPath("$.task_id").value(ID));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void getTaskByIdShouldReturn404WithErrorMessageWhenTaskDoesNotExist() throws Exception {
        //when
        mvc.perform(
                get("/api/v1/task/500")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept("application/json")
        )
        //verify
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());

    }

    @Test
    @WithMockUser(username = USERNAME)
    void completeTaskShouldReturnCompletedTaskWhenTaskExistsAndHasNotBeenCompletedBefore() throws Exception {
        //when
        mvc.perform(
                patch("/api/v1/task/{id}/complete", ID)
                        .contentType(MediaType.APPLICATION_JSON)
        )
        //verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.date_time_finished").isNotEmpty());

    }

    @Test
    @WithMockUser(username = USERNAME)
    void completeTaskShouldReturn208IfTaskExistsButHasAlreadyBeenCompleted() throws Exception {
        //given
        testTask.setActive(false);
        testTask.setTimeCompleted(LocalDateTime.now());
        taskRepository.save(testTask);

        //when
        mvc.perform(
                patch("/api/v1/task/" + ID + "/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                //verify
                .andExpect(status().isAlreadyReported())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    @WithMockUser(username = USERNAME)
    void completeTaskShouldReturn404IfTaskDoesNotExist() throws Exception {
        //when
        mvc.perform(
                patch("/api/v1/task/500/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept("application/json")
        )
                //verify
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());

    }

    @Test
    @WithMockUser(username = USERNAME)
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
                patch("/api/v1/task/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("new name"))
                .andExpect(jsonPath("$.description").value("new description"));
    }

    @Test
    @WithMockUser(username = USERNAME)
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
                patch("/api/v1/task/{id}", 50)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    @WithMockUser(username = USERNAME)
    void updateTaskShouldReturn400IfInputsAreNotValid() throws Exception {
        //given
        String taskJson = """
                {
                    "name" : "",
                    "description" : "do"            
                }
                """;

        //when
        mvc.perform(
                patch("/api/v1/task/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

}
