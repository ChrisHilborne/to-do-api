package com.chilborne.todoapi.web.controller;

import com.chilborne.todoapi.web.dto.SingleValueDTO;
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
    ArgumentCaptor<SingleValueDTO<String>> stringDTOCaptor;

    @Captor
    ArgumentCaptor<SingleValueDTO<Boolean>> booleanDTOCaptor;

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

        testList.setTimeCreated(now);

        //when
        when(service.getToDoListById(id)).thenReturn(testList);

        //verify
        mvc.perform(
                get("/list/" + id)
                        .accept("application/json")
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
        ToDoList testList = new ToDoList("test", "this is a test");
        testList.setTimeCreated(now);

        //when
        when(service.saveToDoList(any(ToDoList.class))).thenReturn(testList);

        //verify
        mvc.perform(
                post("/list")
                        .accept("application/json")
                        .contentType("application/json")
                        .content(testJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.description").value("this is a test")
                );
        verify(service).saveToDoList(toDoListCaptor.capture());
        verifyNoMoreInteractions(service);
        assertEquals(testList.getName(), toDoListCaptor.getValue().getName());
        assertEquals(testList.getDescription(), toDoListCaptor.getValue().getDescription());
    }

    @Test
    void setActiveShouldReturnUpdatedToDoList() throws Exception {
        //given
        String activeJson = "{ \"value\" : \"false\" }";
        ToDoList activeList = new ToDoList("test");
        activeList.setTimeCreated(now);
        activeList.setActive(true);
        long activeListId = activeList.getId();

        ToDoList inactiveList = new ToDoList("test");
        inactiveList.setTimeCreated(now);
        inactiveList.setActive(false);


        //when
        when(service.setToDoListActive(anyLong(), any(SingleValueDTO.class))).thenReturn(inactiveList);

        //verify
        mvc.perform(
                put("/list/" + activeListId + "/active/")
                        .accept("application/json")
                        .contentType("application/json")
                        .content(activeJson)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.time_created").value(nowString));
        verify(service).setToDoListActive(anyLong(), any(SingleValueDTO.class));
        verifyNoMoreInteractions(service);

        verify(service).setToDoListActive(idCaptor.capture(), booleanDTOCaptor.capture());
        long passedId = idCaptor.getValue();
        SingleValueDTO<Boolean> passedDTO = booleanDTOCaptor.getValue();
        assertEquals(activeListId, passedId);
        assertEquals(false, passedDTO.getValue());

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
                put("/list/" + testList.getId() + "/task/add")
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
        Task initialTask = new Task(testList, "task1");
        testList.addTask(initialTask);
        Task taskToRemove = new Task(testList, "task2");
        long idTestList = testList.getId();
        long idTaskToRemove = taskToRemove.getTaskId();

        //when
        when(service.removeTaskToDoList(idTestList, idTaskToRemove)).thenReturn(testList);

        //verify
        mvc.perform(
                put(String.format("/list/%d/task/remove/%d", idTestList, idTaskToRemove))
                .accept("application/json")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tasks[0].name").value("task1"));
        verify(service).removeTaskToDoList(idTestList, idTaskToRemove);
        verifyNoMoreInteractions(service);
    }


    @Test
    void setDescriptionShouldReturnUpdatedToDoList() throws Exception {
        //given
        String description = "This is a description";
        SingleValueDTO<String> descriptionDTO = new SingleValueDTO<>(description);
        ToDoList testList = new ToDoList("testList", description);


        //when
        when(service.setToDoListDescription(anyLong(), any(SingleValueDTO.class))).thenReturn(testList);

        //verify
        mvc.perform(
                put("/list/{id}/description", testList.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"value\": \"" + descriptionDTO.getValue() + "\" }")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());

        verify(service).setToDoListDescription(anyLong(), any(SingleValueDTO.class));
        verifyNoMoreInteractions(service);
        
        verify(service).setToDoListDescription(idCaptor.capture(), stringDTOCaptor.capture());
        long passedId = idCaptor.getValue();
        SingleValueDTO<String> passedDTO = stringDTOCaptor.getValue();
        assertEquals(testList.getId(), passedId);
        assertEquals(description, passedDTO.getValue());

    }

    @Test
    void setNameShouldReturnUpdatedToDoList() throws Exception {
        //given
        String name = "This is a name";
        SingleValueDTO<String> descriptionDTO = new SingleValueDTO<>(name);
        ToDoList testList = new ToDoList("testTask", name);


        //when
        when(service.setToDoListName(anyLong(), any(SingleValueDTO.class))).thenReturn(testList);

        //verify
        mvc.perform(
                put(String.format("/list/%d/name", testList.getId()))
                        .contentType("application/json")
                        .content("{ \"value\": \"" + descriptionDTO.getValue() + "\" }")
                        .accept("application/json")
        )
                .andExpect(status().isOk());

        verify(service).setToDoListName(anyLong(), any(SingleValueDTO.class));
        verifyNoMoreInteractions(service);

        verify(service).setToDoListName(idCaptor.capture(), stringDTOCaptor.capture());
        long passedId = idCaptor.getValue();
        SingleValueDTO<String> passedDTO = stringDTOCaptor.getValue();
        assertEquals(testList.getId(), passedId);
        assertEquals(name, passedDTO.getValue());
    }

}