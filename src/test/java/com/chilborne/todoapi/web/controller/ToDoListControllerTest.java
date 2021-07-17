package com.chilborne.todoapi.web.controller;

import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.dto.ToDoListDto;
import com.chilborne.todoapi.persistance.mapper.TaskMapper;
import com.chilborne.todoapi.persistance.mapper.ToDoListMapper;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.service.ToDoListServiceImpl;
import com.chilborne.todoapi.web.controller.v1.ToDoListController;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
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

    @Captor
    ArgumentCaptor<ToDoList> toDoListCaptor;

    @Captor
    ArgumentCaptor<ToDoListDto> dtoCaptor;

    @Captor
    ArgumentCaptor<Long> idCaptor;

    @Captor
    ArgumentCaptor<Boolean> booleanCaptor;

    ToDoList testList;
    ToDoListDto testDto;

    ToDoListMapper toDoListMapper = ToDoListMapper.INSTANCE;
    TaskMapper taskMapper = TaskMapper.INSTANCE;

    LocalDateTime now = LocalDateTime.now();
    String nowString;

    @BeforeEach
    void init() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        nowString = now.format(formatter);
        testList = new ToDoList("test", "this is a test");
        testList.setTimeCreated(now);

        testDto = toDoListMapper.convertToDoList(testList);
    }

    @Test
    void getToDoListShouldWorkWhenListExists() throws Exception {
        //given
        long id = 0L;

        //when
        when(service.getToDoListDtoById(id)).thenReturn(testDto);

        //verify
        mvc.perform(
                get("/v1/list/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.date_time_made").value(nowString)
                );

        verify(service).getToDoListDtoById(0L);
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
        when(service.saveToDoList(any(ToDoListDto.class))).thenReturn(testDto);

        //verify
        mvc.perform(
                post("/v1/list")
                        .accept("application/json")
                        .contentType("application/json")
                        .content(testJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.description").value("this is a test")
                );
        verify(service).saveToDoList(dtoCaptor.capture());
        verifyNoMoreInteractions(service);
        assertTrue(toDoListMapper.compare(testList, dtoCaptor.getValue()));
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
        testDto = toDoListMapper.convertToDoList(testList);
        //when
        when(service.updateToDoList(anyLong(), any(ToDoListDto.class))).thenReturn(testDto);
        mvc.perform(
                put("/v1/list/{id}", testList.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testJson)
                        .accept(MediaType.APPLICATION_JSON)
        )
        //verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("this name was recently updated"))
                .andExpect(jsonPath("$.description").value("so was this description"))
                .andExpect(jsonPath("$.list_id").value(testList.getId()));
        verify(service).updateToDoList(idCaptor.capture(), dtoCaptor.capture());
        verifyNoMoreInteractions(service);

        long passedId = idCaptor.getValue();
        assertEquals(testList.getId(), passedId);
        ToDoListDto passedToDoList = dtoCaptor.getValue();
        assertAll("@passedToDoList has name and description of @testJson",
                () -> assertEquals("this name was recently updated", passedToDoList.getName()),
                () -> assertEquals("so was this description", passedToDoList.getDescription()));

    }

    @Test
    void deleteToDoListShouldReturn201() throws Exception {
        //when
        mvc.perform(
                delete("/v1/list/{id}", testList.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNoContent());

        verify(service).deleteToDoList(testList.getId());
        verifyNoMoreInteractions(service);
    }

    @Test
    void setActiveShouldReturnUpdatedToDoList() throws Exception {
        //given
        testList.setActive(false);
        testDto.setActive(false);

        //when
        when(service.setToDoListActive(anyLong(), anyBoolean())).thenReturn(testDto);

        //verify
        mvc.perform(
                patch("/v1/list/{id}/active/{active}", testList.getId(), false)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.date_time_made").value(nowString));
        verify(service).setToDoListActive(anyLong(), anyBoolean());
        verifyNoMoreInteractions(service);

        verify(service).setToDoListActive(idCaptor.capture(), booleanCaptor.capture());
        long passedId = idCaptor.getValue();
        boolean passedBoolean = booleanCaptor.getValue();
        assertEquals(testList.getId(), passedId);
        assertFalse(passedBoolean);

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

        testDto = toDoListMapper.convertToDoList(testList);

        //when
        when(service.addTaskToDoList(anyLong(), any(TaskDto.class))).thenReturn(testDto);

        //verify
        mvc.perform(
                patch("/v1/list/" + testList.getId() + "/task/add")
                        .accept("application/json")
                        .contentType("application/json")
                        .content(taskJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.tasks[0].name").value("task"));

        verify(service).addTaskToDoList(anyLong(), any(TaskDto.class));
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
        long idTaskToRemove = taskToRemove.getId();

        testDto = toDoListMapper.convertToDoList(testList);

        //when
        when(service.removeTaskToDoList(idTestList, idTaskToRemove)).thenReturn(testDto);

        //verify
        mvc.perform(
                patch(String.format("/v1/list/%d/task/remove/%d", idTestList, idTaskToRemove))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks[0].name").value("task1"));
        verify(service).removeTaskToDoList(idTestList, idTaskToRemove);
        verifyNoMoreInteractions(service);
    }

}