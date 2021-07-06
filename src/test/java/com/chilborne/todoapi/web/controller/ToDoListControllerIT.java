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

    ToDoList testList;

    long testListId;

    @BeforeEach
    void initData() {
        testList = new ToDoList("test");
        repository.save(testList);
        testListId = testList.getId();
    }

    @AfterEach
    void tearDownData() {
        repository.deleteAll();
    }

    @Test
    void getToDoListByIdShouldReturnListWhenItExists() throws Exception {
        //when
        mvc.perform(
                get("/list/{id}", testListId)
                        .accept(MediaType.APPLICATION_JSON)
        )
        //verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.id").value(testListId));
    }

    @Test
    void getToDoListByIdShouldReturn404WIthErrorMessageWhenListDoesNotExist() throws Exception {
        //when
        mvc.perform(
                get("/list/{id}", 50)
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
                .andExpect(status().isOk())
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
        //given
        String booleanDTO = """
                {
                    "value" : "false"
                }""";

        //when
        mvc.perform(
                put("/list/{id}/active", testListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(booleanDTO)
                        .accept(MediaType.APPLICATION_JSON)
        )
                //verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void putActiveToDoListShouldReturn404WithErrorMessageWhenToDoListDoesNotExist() throws Exception {
        //given
        String activeDTO = """
                {
                    "value" : "false"
                }""";

        //when
        mvc.perform(
                put("/list/{id}/active", 50)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(activeDTO)
                        .accept(MediaType.APPLICATION_JSON)
        )
                //verify
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void setToDoListNameShouldReturnUpdatedToDoList() throws Exception {
        //given
        String nameDTO = """
                {
                    "value" : "this is a name"
                }
                """;

        //when
        mvc.perform(
                put("/list/{id}/name", testListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nameDTO)
                        .accept(MediaType.APPLICATION_JSON)
        )
        //verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("this is a name"));
    }

    @Test
    void setToDoListNameShouldReturnBadRequestWhenNameIsBlank() throws Exception {
        //given
        String nameDTO = """
                {
                    "value" : ""
                }
                """;

        //when
        mvc.perform(
                put("/list/{id}/name", testListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nameDTO)
                        .accept(MediaType.APPLICATION_JSON)
        )
                //verify
                .andExpect(status().isBadRequest());
    }

    @Test
    void setToDoListNameShouldReturn404WithErrorMessageWhenListDoesNotExist() throws Exception {
        //given
        String nameDTO = """
                {
                    "value" : "this is a name"
                }
                """;

        //when
        mvc.perform(
                put("/list/{id}/name", 50)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nameDTO)
                        .accept(MediaType.APPLICATION_JSON)
        )
                //verify
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void setToDoListDescriptionShouldReturnUpdatedToDoList() throws Exception {
        //given
        String descriptionDTO = """
                {
                    "value" : "this is a description"
                }
                """;

        //when
        mvc.perform(
                put("/list/{id}/description", testListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(descriptionDTO)
                        .accept(MediaType.APPLICATION_JSON)
        )
                //verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("this is a description"));
    }

    @Test
    void setToDoListDescriptionShouldReturn404WithErrorMessageWhenListDoesNotExist() throws Exception {
        //given
        String descriptionDTO = """
                {
                    "value" : "this is a description"
                }
                """;

        //when
        mvc.perform(
                put("/list/{id}/description", 50)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(descriptionDTO)
                        .accept(MediaType.APPLICATION_JSON)
        )
                //verify
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void setToDoListDescriptionShouldReturn400WithErrorMessageWhenDescriptionDoesNotMeetRequirements() throws Exception {
        //given
        String descriptionDTO = """
                {
                    "value" : "na"
                }
                """;
        //when
        mvc.perform(
                put("/list/{id}/description", testList.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(descriptionDTO)
                        .accept(MediaType.APPLICATION_JSON)
        )
        //verify
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void addTaskShouldReturnUpdatedToDoList() throws Exception {
        //given
        String task = """
                {
                    "name" : "task 2",
                    "description" : "task description"
                }
                """;

        //when
        mvc.perform(
                put("/list/{id}/task/add", testListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(task)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks[0].name").value("task 2"))
                .andExpect(jsonPath("$.tasks[0].description").value("task description"));
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
                put("/list/{id}/task/add", 50)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(task)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }


    @Test
    void removeTaskShouldRemoveUpdatedToDoList() throws Exception {
        //given
        Task taskToBeRemoved = new Task("task to be removed");
        testList.addTask(taskToBeRemoved);
        repository.save(testList);
        ToDoList savedList = repository.findById(testListId).get();

        //when
        mvc.perform(
                put("/list/{listId}/task/remove/{taskId}", testListId, taskToBeRemoved.getTaskId())
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks").isEmpty());
    }

    @Test
    void removeTaskShouldReturn404WithErrorMessageWhenToDoListDoesNotExist() throws Exception {
        //given
        Task taskToBeRemoved = new Task("task to be removed");
        testList.addTask(taskToBeRemoved);
        repository.save(testList);

        //when
        mvc.perform(
                put("/list/{listId}/task/remove/{taskId}", 50, taskToBeRemoved.getTaskId())
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    void removeTaskShouldReturn404AndErrorMessageWhenTaskDoesNotExist() throws Exception {
        //when
        mvc.perform(
                put("/list/{listId}/task/remove/{taskId}", testListId, 50)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

}
