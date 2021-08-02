package com.chilborne.todoapi.web.controller.v1;

import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.dto.ToDoListDto;
import com.chilborne.todoapi.persistance.mapper.ToDoListMapper;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.persistance.model.User;
import com.chilborne.todoapi.service.ToDoListServiceImpl;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ToDoListController.class)
@ExtendWith({MockitoExtension.class, SpringExtension.class})
class ToDoListControllerTest {

    private static final String USERNAME = "user";
    private static final String PASSWORD = "password";
    private static final LocalDateTime NOW = LocalDateTime.now();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    @MockBean
    ToDoListServiceImpl service;
    @Captor
    ArgumentCaptor<String> usernameCaptor;
    @Captor
    ArgumentCaptor<ToDoListDto> dtoCaptor;
    @Captor
    ArgumentCaptor<Long> idCaptor;
    @Captor
    ArgumentCaptor<Boolean> booleanCaptor;
    ToDoListMapper toDoListMapper = ToDoListMapper.INSTANCE;
    @Autowired
    private MockMvc mvc;
    private ToDoList testList;
    private ToDoListDto testDto;
    private String nowString;
    private User user;

    @BeforeEach
    void init() {
        nowString = NOW.format(FORMATTER);
        user = new User(USERNAME, PASSWORD);
        testList = new ToDoList("test", "this is a test");
        testList.setUser(user);
        testList.setTimeCreated(NOW);

        user.addToDoList(testList);

        testDto = toDoListMapper.convertToDoList(testList);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void getToDoListShouldWorkWhenListExists() throws Exception {
        //given
        long id = 0L;

        //when
        when(service.getToDoListDtoByIdAndUsername(id, USERNAME)).thenReturn(testDto);

        //verify
        mvc.perform(
                get("/api/v1/list/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.date_time_made").value(nowString)
                );

        verify(service).getToDoListDtoByIdAndUsername(0L, USERNAME);
        verifyNoMoreInteractions(service);
    }


    @Test
    @WithMockUser(username = USERNAME)
    void postToDoListShouldReturnNewlyCreatedToDoList() throws Exception {
        //given
        String testJson = """
                {
                      "name": "test",
                      "description": "this is a test"
                }""";

        //when
        when(service.newToDoList(any(ToDoListDto.class), anyString())).thenReturn(testDto);

        //verify
        mvc.perform(
                post("/api/v1/list")
                        .accept("application/json")
                        .contentType("application/json")
                        .content(testJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.description").value("this is a test")
                );
        verify(service).newToDoList(dtoCaptor.capture(), eq("user"));
        verifyNoMoreInteractions(service);
        assertTrue(toDoListMapper.compare(testList, dtoCaptor.getValue()));
    }

    @Test
    @WithMockUser(username = USERNAME)
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
        when(service.updateToDoList(anyLong(), any(ToDoListDto.class), anyString())).thenReturn(testDto);
        mvc.perform(
                put("/api/v1/list/{id}", testList.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(testJson)
                        .accept(MediaType.APPLICATION_JSON)
        )
        //verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("this name was recently updated"))
                .andExpect(jsonPath("$.description").value("so was this description"))
                .andExpect(jsonPath("$.list_id").value(testList.getId()));
        verify(service).updateToDoList(idCaptor.capture(), dtoCaptor.capture(), usernameCaptor.capture());
        verifyNoMoreInteractions(service);

        long passedId = idCaptor.getValue();
        assertEquals(testList.getId(), passedId);
        String passedUsername = usernameCaptor.getValue();
        assertEquals(USERNAME, passedUsername);
        ToDoListDto passedToDoList = dtoCaptor.getValue();
        assertAll("@passedToDoList has name and description of @testJson",
                () -> assertEquals("this name was recently updated", passedToDoList.getName()),
                () -> assertEquals("so was this description", passedToDoList.getDescription()));

    }

    @Test
    @WithMockUser(username = USERNAME)
    void deleteToDoListShouldReturn201() throws Exception {
        //when
        mvc.perform(
                delete("/api/v1/list/{id}", testList.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNoContent());

        verify(service).deleteToDoList(testList.getId(), USERNAME);
        verifyNoMoreInteractions(service);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void setActiveShouldReturnUpdatedToDoList() throws Exception {
        //given
        testList.setActive(false);
        testDto.setActive(false);

        //when
        when(service.setToDoListActive(anyLong(), anyString(), anyBoolean())).thenReturn(testDto);

        //verify
        mvc.perform(
                patch("/api/v1/list/{id}/active/{active}", testList.getId(), false)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.date_time_made").value(nowString));
        verify(service).setToDoListActive(anyLong(), anyString(), anyBoolean());
        verifyNoMoreInteractions(service);

        verify(service).setToDoListActive(idCaptor.capture(), usernameCaptor.capture(), booleanCaptor.capture());
        long passedId = idCaptor.getValue();
        String passedUsername = usernameCaptor.getValue();
        boolean passedBoolean = booleanCaptor.getValue();
        assertEquals(testList.getId(), passedId);
        assertEquals(USERNAME, passedUsername);
        assertFalse(passedBoolean);

    }

    @Test
    @WithMockUser(username = USERNAME)
    void addTaskShouldReturnListWithNewTask() throws Exception {
        //given
        String taskJson = """
                {
                      "name": "task"
                }""";
        ToDoList testList = new ToDoList("test");
        testList.setTimeCreated(NOW);
        Task testTask = new Task(testList, "task");
        testList.addTask(testTask);

        testDto = toDoListMapper.convertToDoList(testList);

        //when
        when(service.addTaskToDoList(anyLong(), anyString(), any(TaskDto.class))).thenReturn(testDto);

        //verify
        mvc.perform(
                patch("/api/v1/list/" + testList.getId() + "/task/add")
                        .accept("application/json")
                        .contentType("application/json")
                        .content(taskJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.tasks[0].name").value("task"));

        verify(service).addTaskToDoList(anyLong(), anyString(), any(TaskDto.class));
        verifyNoMoreInteractions(service);

    }


    @Test
    @WithMockUser(username = USERNAME)
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
        when(service.removeTaskToDoList(idTestList, USERNAME, idTaskToRemove)).thenReturn(testDto);

        //verify
        mvc.perform(
                patch(String.format("/api/v1/list/%d/task/remove/%d", idTestList, idTaskToRemove))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks[0].name").value("task1"));
        verify(service).removeTaskToDoList(idTestList, USERNAME, idTaskToRemove);
        verifyNoMoreInteractions(service);
    }

}