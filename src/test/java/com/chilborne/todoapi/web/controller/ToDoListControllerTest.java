package com.chilborne.todoapi.web.controller;

import com.chilborne.todoapi.exception.TaskNotFoundException;
import com.chilborne.todoapi.exception.ToDoListNotFoundException;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.service.ToDoListService;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ToDoListController.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class})
class ToDoListControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ToDoListService service;

    @InjectMocks
    ToDoListController controller;

    @Captor
    ArgumentCaptor<ToDoList> captor;

    LocalDateTime now = LocalDateTime.now();

    String nowString;

    @BeforeEach
    void init() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        nowString = now.format(formatter);
    }

    @Test
    void getToDoListShouldWorldWhenListExists() throws Exception {
        //given
        long id = 0L;
        ToDoList testList = new ToDoList("test");

        testList.setDateTimeCreated(now);

        //when
        when(service.getToDoListById(id)).thenReturn(testList);

        //verify
        mvc.perform(
                get("/list/" + id)
                        .accept("application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.date_time_created").value(nowString)
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
        ToDoList testList = new ToDoList("test", "this is a test");
        testList.setDateTimeCreated(now);

        //when
        when(service.saveToDoList(any(ToDoList.class))).thenReturn(testList);

        //verify
        mvc.perform(
                post("/list/new")
                        .accept("application/json")
                        .contentType("application/json")
                        .content(testJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.description").value("this is a test")
                );
        verify(service).saveToDoList(captor.capture());
        verifyNoMoreInteractions(service);
        assertEquals(testList.getName(), captor.getValue().getName());
        assertEquals(testList.getDescription(), captor.getValue().getDescription());
    }

    @Test
    void setActiveShouldReturnUpdatedToDoList() throws Exception {
        //given
        ToDoList activeList = new ToDoList("test");
        activeList.setDateTimeCreated(now);
        activeList.setActive(true);
        long activeId = activeList.getId();

        ToDoList inactiveList = new ToDoList("test");
        inactiveList.setDateTimeCreated(now);
        inactiveList.setActive(false);


        //when
        when(service.setActive(activeId, false)).thenReturn(inactiveList);

        //verify
        mvc.perform(
                put("/list/" + activeId + "/active/false")
                        .accept("application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.date_time_created").value(nowString));
        verify(service).setActive(activeId, false);
        verifyNoMoreInteractions(service);

    }

    @Test
    void addTaskShouldReturnListWithNewTask() throws Exception {
        //given
        String taskJson = """
                {
                      "name": "task"
                }""";
        ToDoList testList = new ToDoList("test");
        testList.setDateTimeCreated(now);
        Task testTask = new Task(testList, "task");
        testList.addTask(testTask);

        //when
        when(service.addTask(anyLong(), any(Task.class))).thenReturn(testList);

        //verify
        mvc.perform(
                put("/list/" + testList.getId() + "/task/add")
                        .accept("application/json")
                        .contentType("application/json")
                        .content(taskJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.tasks[0].name").value("task"));

        verify(service).addTask(anyLong(), any(Task.class));
        verifyNoMoreInteractions(service);

    }


    @Test
    void removeTaskShouldReturnUpdatedToDoList() throws Exception {
        //given
        ToDoList testList = new ToDoList("testList");
        Task initialTask = new Task(testList, "task1");
        testList.addTask(initialTask);
        Task taskToRemove = new Task(testList, "task2");
        long idTestList = testList.getId();
        long idTaskToRemove = taskToRemove.getTaskId();

        //when
        when(service.removeTask(idTestList, idTaskToRemove)).thenReturn(testList);

        //verify
        mvc.perform(
                put(String.format("/list/%d/task/remove/%d", idTestList, idTaskToRemove))
                .accept("application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks[0].name").value("task1"));
        verify(service).removeTask(idTestList, idTaskToRemove);
        verifyNoMoreInteractions(service);
    }


    @Test
    void setDescriptionShouldReturnUpdatedToDoList() throws Exception {
        //given
        String description = "This is a description";
        ToDoList testList = new ToDoList("testTask", description);

        //when
        when(service.setDescription(testList.getId(), description)).thenReturn(testList);

        //verify
        mvc.perform(
                put(String.format("/list/%d/description", testList.getId()))
                        .content("{ \"description\": \"" + description + "\"")
                        .accept("application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(description));

    }

    @Test
    void setNameShouldReturnUpdatedToDoList() {

    }

}