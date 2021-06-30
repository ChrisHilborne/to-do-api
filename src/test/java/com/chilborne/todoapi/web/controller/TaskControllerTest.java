package com.chilborne.todoapi.web.controller;

import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.service.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.format.DateTimeFormatter;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    TaskServiceImpl service;

    @InjectMocks
    TaskController controller;

    Task testTask;

    @BeforeEach
    void init() {
        ToDoList testList = new ToDoList("test list");
        testTask = new Task(testList, "test task");
    }

    @Test
    void getTask() throws Exception {
        //given
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd:MM.yy HH:mm:ss");

        //when
        when(service.getTaskById(50L)).thenReturn(testTask);

        //verify
        mvc.perform(
                get("/task/50")
                        .accept("application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testTask.getName()))
                .andExpect(jsonPath("$.task_id").value(testTask.getTaskId()))
                .andExpect(jsonPath("$.date_created").value(testTask.getTimeCreated().format(formatter)));

        verify(service).getTaskById(50L);
        verifyNoMoreInteractions(service);
    }

    @Test
    void completeTask() {
    }

    @Test
    void setTaskName() {
    }

    @Test
    void setTaskDescription() {
    }
}