package com.chilborne.todoapi.web.controller.v1;

import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.mapper.TaskMapper;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.persistance.model.User;
import com.chilborne.todoapi.service.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertAll;
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

    @Captor
    ArgumentCaptor<Long> idCaptor;

    @Captor
    ArgumentCaptor<TaskDto> taskCaptor;

    TaskMapper mapper = TaskMapper.INSTANCE;
    private Task testTask;
    private ToDoList testList;
    private User testUser;

    private static final String USERNAME = "username1";
    private static final String PASSWORD = "secret";


    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @BeforeEach
    void init() {
        testTask = new Task(testList, "test task");
        testTask.setTimeCreated(LocalDateTime.now());

        testList = new ToDoList("ToDoList");
        testList.addTask(testTask);
        testList.setUser(testUser);

        testUser = new User(USERNAME, PASSWORD);
        testUser.addToDoList(testList);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void getTask() throws Exception {
        //given
        String timeCreated = testTask.getTimeCreated().format(formatter);

        //when
        when(service.getTaskDtoById(50L)).thenReturn(mapper.convertTask(testTask));

        //verify
        mvc.perform(
                get("/api/v1/task/50")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(testTask.getName()))
                .andExpect(jsonPath("$.task_id").value(testTask.getId()))
                .andExpect(jsonPath("$.date_time_made").value(timeCreated));

        verify(service).getTaskDtoById(50L);
        verifyNoMoreInteractions(service);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void completeTask() throws Exception {
        //given
        testTask.setActive(false);
        testTask.setTimeCompleted(LocalDateTime.now());
        String timeCompleted = testTask.getTimeCompleted().format(formatter);

        //when
        when(service.completeTask(50)).thenReturn(mapper.convertTask(testTask));

        //verify
        mvc.perform(
                patch("/api/v1/task/50/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept("application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value("false"))
                .andExpect(jsonPath("$.date_time_finished").value(timeCompleted));

        verify(service).completeTask(50L);
        verifyNoMoreInteractions(service);

    }

    @Test
    @WithMockUser(username = USERNAME)
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
        when(service.updateTaskNameAndDescription(anyLong(), any(TaskDto.class))).thenReturn(mapper.convertTask(testTask));

        mvc.perform(
                patch("/api/v1/task/{id}", testTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson)
        )
        //verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("new name"))
                .andExpect(jsonPath("$.description").value("new description"));

        verify(service).updateTaskNameAndDescription(idCaptor.capture(), taskCaptor.capture());
        verifyNoMoreInteractions(service);

        long passedId = idCaptor.getValue();
        assertEquals(testTask.getId(), passedId);

        TaskDto passedTask = taskCaptor.getValue();
        assertAll("@passedTask properties match @testJson",
                () -> assertEquals("new name", passedTask.getName()),
                () -> assertEquals("new description", passedTask.getDescription()));
    }

}