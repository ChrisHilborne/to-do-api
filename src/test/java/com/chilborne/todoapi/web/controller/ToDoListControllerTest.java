package com.chilborne.todoapi.web.controller;

import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.service.ToDoListServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
@WebMvcTest(ToDoListController.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class})
class ToDoListControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ToDoListServiceImpl service;

    @InjectMocks
    ToDoListController controller;

    @Captor
    ArgumentCaptor<ToDoList> toDoListCaptor;

    @Captor
    ArgumentCaptor<Long> idCaptor;

    @Captor
    ArgumentCaptor<Boolean> booleanCaptor;

    ToDoList testList;

    LocalDateTime now = LocalDateTime.now();
    String nowString;

    @BeforeEach
    void init() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        nowString = now.format(formatter);
        testList = new ToDoList("test", "this is a test");
        testList.setTimeCreated(now);
    }

    @Test
    void getToDoListShouldWorkWhenListExists() throws Exception {
        //given
        long id = 0L;

        //when
        when(service.getToDoListById(id)).thenReturn(testList);

        //verify
        mvc.perform(
                get("/list/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.time_created").value(nowString)
                );

        verify(service).getToDoListById(0L);
        verifyNoMoreInteractions(service);
    }


    @Test
    void postToDoListShouldReturnNewlyCreatedToDoList() throws Exception {
        //given
        String testJson = """
                {
                      "name": "test",
                      "description": "this is a test"
                }""";

        //when
        when(service.saveToDoList(any(ToDoList.class))).thenReturn(testList);

        //verify
        mvc.perform(
                post("/list")
                        .accept("application/json")
                        .contentType("application/json")
                        .content(testJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.description").value("this is a test")
                );
        verify(service).saveToDoList(toDoListCaptor.capture());
        verifyNoMoreInteractions(service);
        assertEquals(testList.getName(), toDoListCaptor.getValue().getName());
        assertEquals(testList.getDescription(), toDoListCaptor.getValue().getDescription());
    }

    @Test
    void upDateToDoListShouldReturnUpdatedList() throws Exception {
        //given
        String testJson = """
                {
                    "name" : "this name was recently updated",
                    "description" : "so was this description"
                }
                """;
        testList.setName("this name was recently updated");
        testList.setDescription("so was this description");
        //when
        when(service.updateToDoList(anyLong(), any(ToDoList.class))).thenReturn(testList);
        mvc.perform(
                put("/list/{id}", testList.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testJson)
                        .accept(MediaType.APPLICATION_JSON)
        )
        //verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("this name was recently updated"))
                .andExpect(jsonPath("$.description").value("so was this description"))
                .andExpect(jsonPath("$.id").value(testList.getId()));
        verify(service).updateToDoList(idCaptor.capture(), toDoListCaptor.capture());
        verifyNoMoreInteractions(service);

        long passedId = idCaptor.getValue();
        assertEquals(testList.getId(), passedId);
        ToDoList passedToDoList = toDoListCaptor.getValue();
        assertAll("@passedToDoList has name and description of @testJson",
                () -> assertEquals("this name was recently updated", passedToDoList.getName()),
                () -> assertEquals("so was this description", passedToDoList.getDescription()));

    }

    @Test
    void setActiveShouldReturnUpdatedToDoList() throws Exception {
        //given
        testList.setActive(false);

        //when
        when(service.setToDoListActive(anyLong(), anyBoolean())).thenReturn(testList);

        //verify
        mvc.perform(
                patch("/list/{id}/active/{active}", testList.getId(), false)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept("application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.time_created").value(nowString));
        verify(service).setToDoListActive(anyLong(), anyBoolean());
        verifyNoMoreInteractions(service);

        verify(service).setToDoListActive(idCaptor.capture(), booleanCaptor.capture());
        long passedId = idCaptor.getValue();
        boolean passedBoolean = booleanCaptor.getValue();
        assertEquals(testList.getId(), passedId);
        assertEquals(false, passedBoolean);

    }

    @Test
    void addTaskShouldReturnListWithNewTask() throws Exception {
        //given
        String taskJson = """
                {
                      "name": "task"
                }""";
        ToDoList testList = new ToDoList("test");
        testList.setTimeCreated(now);
        Task testTask = new Task(testList, "task");
        testList.addTask(testTask);

        //when
        when(service.addTaskToDoList(anyLong(), any(Task.class))).thenReturn(testList);

        //verify
        mvc.perform(
                patch("/list/" + testList.getId() + "/task/add")
                        .accept("application/json")
                        .contentType("application/json")
                        .content(taskJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.tasks[0].name").value("task"));

        verify(service).addTaskToDoList(anyLong(), any(Task.class));
        verifyNoMoreInteractions(service);

    }


    @Test
    void removeTaskShouldReturnUpdatedToDoList() throws Exception {
        //given
        ToDoList testList = new ToDoList("testList");
        testList.setTimeCreated(LocalDateTime.now());
        Task initialTask = new Task(testList, "task1");
        testList.addTask(initialTask);
        Task taskToRemove = new Task(testList, "task2");
        long idTestList = testList.getId();
        long idTaskToRemove = taskToRemove.getTaskId();

        //when
        when(service.removeTaskToDoList(idTestList, idTaskToRemove)).thenReturn(testList);

        //verify
        mvc.perform(
                patch(String.format("/list/%d/task/remove/%d", idTestList, idTaskToRemove))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks[0].name").value("task1"));
        verify(service).removeTaskToDoList(idTestList, idTaskToRemove);
        verifyNoMoreInteractions(service);
    }

}