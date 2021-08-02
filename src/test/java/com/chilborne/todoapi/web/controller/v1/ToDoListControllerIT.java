package com.chilborne.todoapi.web.controller.v1;

import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.persistance.model.User;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class ToDoListControllerIT {

    static final String USERNAME = "name";
    static final String PASSWORD = "secret";
    static final String EMAIL = "please@mail.me";
    @Autowired
    MockMvc mvc;
    @Autowired
    ToDoListRepository listRepository;
    @Autowired
    UserRepository userRepository;
    ToDoList list;
    long listId;
    Task task;
    long taskId;
    User user;

    @BeforeEach
    void initData() {
        list = new ToDoList("test");
        task = new Task(list, "task");
        list.addTask(task);


        user = new User(USERNAME, PASSWORD, EMAIL);
        user.addToDoList(list);
        userRepository.save(user);

        list.setUser(user);
        listRepository.save(list);
        listId = list.getId();
        taskId = task.getId();
    }

    @AfterEach
    void tearDownData() {
        userRepository.deleteAll();
        listRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = USERNAME, password = PASSWORD)
    void getToDoListByIdShouldReturnListWhenItExistsAndBelongsToAuthenticatedUser() throws Exception {
        //when
        mvc.perform(
                get("/api/v1/list/{id}", listId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
        //verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.list_id").value(listId));
    }

    @Test
    @WithMockUser(username = "fails", password = PASSWORD)
    void getToDoListByIdShouldReturnNotFoundStatusWhenDoesNotBelongToAuthenticatedUser() throws Exception {
        //when
        mvc.perform(
                get("/api/v1/list/{id}", listId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                //verify
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getToDoListByIdShouldReturn404WIthErrorMessageWhenListDoesNotExist() throws Exception {
        //when
        mvc.perform(
                get("/api/v1/list/{id}", 50)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
        //verify
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    @WithMockUser(username = USERNAME)
    void newToDoListShouldReturnCreatedListBelongingToAuthenticatedUser() throws Exception {
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
                post("/api/v1/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newTaskJson)
                        .accept(MediaType.APPLICATION_JSON)
        )
                //verify
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("new task"))
                .andExpect(jsonPath("$.description").value("some adjectives"))
                .andExpect(jsonPath("$.username").value(USERNAME));
    }

    @Test
    @WithMockUser
    void newToDoListShouldReturnBadRequestIfInputsAreNotValid() throws Exception {
        //given
        String toDoListJson = """
                {
                      "name": "",
                      "description": "is",
                      "active" : "false"
                }""";

        //when
        mvc.perform(
                post("/api/v1/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toDoListJson)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = USERNAME)
    void deleteToDoListShouldDeleteObjectFromDB() throws Exception {
        //when
        mvc.perform(
                delete("/api/v1/list/{id}", listId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        )
                //verify
                .andExpect(status().isNoContent());

        assertTrue(listRepository.findById(listId).isEmpty());
    }

    @Test
    @WithMockUser(username = USERNAME, password = PASSWORD)
    void putActiveToDoListShouldReturnUpdatedToDoListWhenListBelongsToAuthenticatedUser() throws Exception {
        //when
        mvc.perform(
                patch("/api/v1/list/{id}/active/{active}", listId, false)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                //verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    @WithMockUser(username = "fails", password = PASSWORD)
    void putActiveToDoListShouldReturnNotFoundStatusWhenListDoesNotBelongToAuthenticatedUser() throws Exception {
        //when
        mvc.perform(
                patch("/api/v1/list/{id}/active/{active}", listId, false)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                //verify
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void putActiveToDoListShouldReturn404WithErrorMessageWhenToDoListDoesNotExist() throws Exception {
        //when
        mvc.perform(
                patch("/api/v1/list/{id}/active/true", 50)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                //verify
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }


    @Test
    @WithMockUser(username = USERNAME)
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
                patch("/api/v1/list/{id}/task/add", listId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks[1].name").value("task 2"))
                .andExpect(jsonPath("$.tasks[1].description").value("task description"));
    }

    @Test
    @WithMockUser
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
                patch("/api/v1/list/{id}/task/add", 50)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(task)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    @WithMockUser
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
                patch("/api/v1/list/{id}/task/add", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(task)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }


    @Test
    @WithMockUser(username = USERNAME)
    void removeTaskShouldRemoveUpdatedToDoList() throws Exception {
        //when
        mvc.perform(
                patch("/api/v1/list/{listId}/task/remove/{taskId}", listId, taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks").isEmpty());
    }

    @Test
    @WithMockUser
    void removeTaskShouldReturn404WithErrorMessageWhenToDoListDoesNotExist() throws Exception {
        //given
        Task taskToBeRemoved = new Task("task to be removed");
        list.addTask(taskToBeRemoved);
        listRepository.save(list);

        //when
        mvc.perform(
                patch("/api/v1/list/{listId}/task/remove/{taskId}", 50, taskToBeRemoved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    @WithMockUser
    void removeTaskShouldReturn404AndErrorMessageWhenTaskDoesNotExist() throws Exception {
        //when
        mvc.perform(
                patch("/api/v1/list/{listId}/task/remove/{taskId}", listId, 50)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    @WithMockUser(username = USERNAME)
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
                put("/api/v1/list/{id}", listId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testJson)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated name"))
                .andExpect(jsonPath("$.description").value("updated description"))
                .andExpect(jsonPath("$.list_id").value(listId));
    }

    @Test
    @WithMockUser(username = USERNAME)
    void updatedToDoListShouldReturn404WithErrorMessageWhenToDoListDoesNotExist() throws Exception {
        //given
        String testJson = """
                {
                    "name" : "updated name",
                    "description" : "updated description"
                }
                """;

        //when
        mvc.perform(
                put("/api/v1/list/{id}", 50)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testJson)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test
    @WithMockUser
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
                put("/api/v1/list/{id}", listId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testJson)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty());
    }

}
