package com.chilborne.todoapi.web.controller;

import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.persistance.repository.ToDoListRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class ToDoListControllerIT {

    @Autowired
    MockMvc mvc;

    @Autowired
    ToDoListRepository repository;

    ToDoList list;
    long listId;

    Task task;
    long taskId;

    @BeforeEach
    void initData() {
        list = new ToDoList("test");
        task = new Task(list, "task");
        list.addTask(task);
        repository.save(list);
        listId = list.getId();
        taskId = task.getId();
    }

    @AfterEach
    void tearDownData() {
        repository.deleteAll();
    }

    @Test
    void getToDoListByIdShouldReturnListWhenItExists() throws Exception {
        //when
        mvc.perform(
                get("/list/{id}", listId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
        //verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.id").value(listId));
    }

    @Test
    void getToDoListByIdShouldReturn404WIthErrorMessageWhenListDoesNotExist() throws Exception {
        //when
        mvc.perform(
                get("/list/{id}", 50)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
        //verify
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void newToDoListShouldReturnCreatedList() throws Exception {
        //given
        String newTaskJson =
        """
        { 
            "name" : "new task",
            "description" : "some adjectives" 
        }
        """;

        //when
        mvc.perform(
                post("/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newTaskJson)
                        .accept(MediaType.APPLICATION_JSON)
        )
                //verify
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("new task"))
                .andExpect(jsonPath("$.description").value("some adjectives"));
    }

    @Test
    void postNewToDoListShouldReturnBadRequestIfInputsAreNotValid() throws Exception {
        //given
        String toDoListJson = """
                {
                      "name": "",
                      "description": "is",
                      "active" : "false"
                }""";

        //when
        mvc.perform(
                post("/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toDoListJson)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    void putActiveToDoListShouldReturnUpdatedToDoList() throws Exception {
        //when
        mvc.perform(
                patch("/list/{id}/active/{active}", listId, false)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                //verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void putActiveToDoListShouldReturn404WithErrorMessageWhenToDoListDoesNotExist() throws Exception {
        //when
        mvc.perform(
                patch("/list/{id}/active/true", 50)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                //verify
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }


    @Test
    void addTaskShouldReturnUpdatedToDoList() throws Exception {
        //given
        String taskJson = """
                {
                    "name" : "task 2",
                    "description" : "task description"
                }
                """;

        //when
        mvc.perform(
                patch("/list/{id}/task/add", listId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks[1].name").value("task 2"))
                .andExpect(jsonPath("$.tasks[1].description").value("task description"));
    }

    @Test
    void addTaskShouldReturn404WithErrorMessageWhenListDoesNotExist() throws Exception {
        //given
        String task = """
                {
                    "name" : "task 2",
                    "description" : "task description"
                }
                """;

        //when
        mvc.perform(
                patch("/list/{id}/task/add", 50)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(task)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void addTaskShouldReturn400WithErrorMessageWhenInputsAreNotValid() throws Exception {
        //given
        String task = """
                {
                    "name" : "",
                    "description" : "do"
                }
                """;

        //when
        mvc.perform(
                patch("/list/{id}/task/add", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(task)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }


    @Test
    void removeTaskShouldRemoveUpdatedToDoList() throws Exception {
        //when
        mvc.perform(
                patch("/list/{listId}/task/remove/{taskId}", listId, taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks").isEmpty());
    }

    @Test
    void removeTaskShouldReturn404WithErrorMessageWhenToDoListDoesNotExist() throws Exception {
        //given
        Task taskToBeRemoved = new Task("task to be removed");
        list.addTask(taskToBeRemoved);
        repository.save(list);

        //when
        mvc.perform(
                patch("/list/{listId}/task/remove/{taskId}", 50, taskToBeRemoved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void removeTaskShouldReturn404AndErrorMessageWhenTaskDoesNotExist() throws Exception {
        //when
        mvc.perform(
                patch("/list/{listId}/task/remove/{taskId}", listId, 50)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void updatedToDoListShouldReturnUpdatedToDoList() throws Exception {
        //given
        String testJson = """
                {
                    "name" : "updated name",
                    "description" : "updated description"
                }
                """;

        //when
        mvc.perform(
                put("/list/{id}", listId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated name"))
                .andExpect(jsonPath("$.description").value("updated description"))
                .andExpect(jsonPath("$.id").value(listId));
    }

    @Test
    void updatedToDoListShouldReturn404WithErrorMessageWhenTaskDoesNotExist() throws Exception {
        //given
        String testJson = """
                {
                    "name" : "updated name",
                    "description" : "updated description"
                }
                """;

        //when
        mvc.perform(
                put("/list/{id}", 50)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testJson)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void updatedToDoListShouldReturn400WithErrorMessageWhenInputsAreNotValid() throws Exception {
        //given
        String testJson = """
                {
                    "name" : "",
                    "description" : "up"
                }
                """;

        //when
        mvc.perform(
                put("/list/{id}", listId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testJson)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

}
