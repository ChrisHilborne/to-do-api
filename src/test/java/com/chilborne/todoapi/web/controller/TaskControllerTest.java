package com.chilborne.todoapi.web.controller;

import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.service.TaskServiceImpl;
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
import org.springframework.test.web.servlet.MockMvc;

import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
@ExtendWith(MockitoExtension.class)
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    TaskServiceImpl service;

    @InjectMocks
    TaskController controller;

    @Captor
    ArgumentCaptor<Long> idCaptor;

    @Captor
    ArgumentCaptor<Task> taskCaptor;

    Task testTask;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @BeforeEach
    void init() {
        ToDoList testList = new ToDoList("test list");
        testTask = new Task(testList, "test task");
    }

    @Test
    void getTask() throws Exception {
        //given
        String timeCreated = testTask.getTimeCreated().format(formatter);

        //when
        when(service.getTaskById(50L)).thenReturn(testTask);

        //verify
        mvc.perform(
                get("/task/50")
                        .accept("application/json")
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(testTask.getName()))
                .andExpect(jsonPath("$.task_id").value(testTask.getTaskId()))
                .andExpect(jsonPath("$.time_created").value(timeCreated));

        verify(service).getTaskById(50L);
        verifyNoMoreInteractions(service);
    }

    @Test
    void completeTask() throws Exception {
        //given
        testTask.complete();
        String timeCompleted = testTask.getTimeCompleted().format(formatter);

        //when
        when(service.completeTask(50)).thenReturn(testTask);

        //verify
        mvc.perform(
                patch("/task/50/complete")
                .accept("application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value("false"))
                .andExpect(jsonPath("$.time_completed").value(timeCompleted));

        verify(service).completeTask(50L);
        verifyNoMoreInteractions(service);

    }

    @Test
    void updateTaskShouldReturnUpdatedTask() throws Exception {
        //given
        String taskJson = """
                {
                    "name" : "new name",
                    "description" : "new description"
                }
                """;
        testTask.setName("new name");
        testTask.setDescription("new description");

        //when
        when(service.updateTask(anyLong(), any(Task.class))).thenReturn(testTask);

        mvc.perform(
                put("/task/{id}", testTask.getTaskId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson)
        )
        //verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("new name"))
                .andExpect(jsonPath("$.description").value("new description"));

        verify(service).updateTask(idCaptor.capture(), taskCaptor.capture());
        verifyNoMoreInteractions(service);

        long passedId = idCaptor.getValue();
        assertEquals(testTask.getTaskId(), passedId);

        Task passedTask = taskCaptor.getValue();
        assertAll("@passedTask properties match @testJson",
                () -> assertEquals("new name", passedTask.getName()),
                () -> assertEquals("new description", passedTask.getDescription()));
    }



}