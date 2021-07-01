package com.chilborne.todoapi.web.controller;

import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.service.TaskServiceImpl;
import com.chilborne.todoapi.web.dto.SingleValueDTO;
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
import org.springframework.test.web.servlet.MockMvc;

import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Captor
    ArgumentCaptor<Long> idCaptor;

    @Captor
    ArgumentCaptor<SingleValueDTO<String>> dtoCaptor;

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
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testTask.getName()))
                .andExpect(jsonPath("$.task_id").value(testTask.getTaskId()))
                .andExpect(jsonPath("$.date_created").value(timeCreated));

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
                put("/task/50/complete")
                .accept("application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value("false"))
                .andExpect(jsonPath("$.time_completed").value(timeCompleted));

        verify(service).completeTask(50L);
        verifyNoMoreInteractions(service);

    }

    @Test
    void setTaskName() throws Exception {
        //given
        String name = "this is a name";
        testTask.setName(name);

        //when
        when(service.setTaskName(anyLong(), any(SingleValueDTO.class))).thenReturn(testTask);

        //verify
        mvc.perform(
                put("/task/50/name")
                        .contentType("application/json")
                        .content("{ \"value\" : \"this is a name\"} ")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name));

        verify(service).setTaskName(idCaptor.capture(), dtoCaptor.capture());
        verifyNoMoreInteractions(service);

        long passedId = idCaptor.getValue();
        assertEquals(50L, passedId);

        SingleValueDTO<String> passedDTO = dtoCaptor.getValue();
        assertEquals(name, passedDTO.getValue());
    }

    @Test
    void setTaskDescription() throws Exception {
        //given
        String description = "descriptions describe things that are describable";
        testTask.setDescription(description);

        //when
        when(service.setTaskDescription(anyLong(), any(SingleValueDTO.class))).thenReturn(testTask);

        //verify
        mvc.perform(
                put("/task/50/description")
                        .contentType("application/json")
                        .content("{ \"value\" : \"" + description + "\"} ")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(description));

        verify(service).setTaskDescription(idCaptor.capture(), dtoCaptor.capture());
        verifyNoMoreInteractions(service);

        long passedId = idCaptor.getValue();
        assertEquals(50L, passedId);

        SingleValueDTO<String> passedDTO = dtoCaptor.getValue();
        assertEquals(description, passedDTO.getValue());
    }
}