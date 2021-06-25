package com.chilborne.todoapi.web.controller;

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
    void getToDoListShouldReturn404WhenListDoesNotExist() throws Exception {
        //given
        long id = 1l;
        String errorMessage = "This list does not exit";
        when(service.getToDoListById(id)).thenThrow(new RuntimeException(errorMessage));

        //verify
        mvc.perform(
                get("/list/" + id)
                .accept("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(errorMessage)
                );
        verify(service).getToDoListById(1L);
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
    void setActiveWhenListDoesNotExistShouldReturn404WithExceptionMessage() throws Exception {
        //given
        given(service.setActive(1L, true)).willThrow(new RuntimeException("This List Does Not Exist"));

        //verify
        mvc.perform(
                put("/list/1/active/true")
                        .accept("application/json")
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("This List Does Not Exist")
                );
        verify(service).setActive(1L, true);
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
    void addTaskShouldReturn404WhenListDoesNotExist() throws Exception {
        //given
        String taskJson = """
        {
              "name": "task"
        }""";

        //when
        when(service.addTask(anyLong(), any(Task.class)))
                .thenThrow(new RuntimeException("List Does Not Exist"));

        //verify
        mvc.perform(put("/list/1/list/add")
                .accept("application/json")
                .contentType("application/json")
                .content(taskJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("List Does Not Exist"));
    }

}