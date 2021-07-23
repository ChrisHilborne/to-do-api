package com.chilborne.todoapi.persistance.mapper;

import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static com.chilborne.todoapi.web.controller.v1.TaskController.TASK_ROOT_URL;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class TaskMapperTest {

    TaskMapper mapper = TaskMapper.INSTANCE;

    LocalDateTime now = LocalDateTime.now();

    @Test
    void convertTaskToDtoShouldWork() {
        //given
        Task task = new Task("This is a task");
        task.setDescription("That will be transformed");
        task.setTimeCreated(now);

        //when
        TaskDto taskDto = mapper.convertTask(task);

        //verify
        assertTrue(mapper.compare(task, taskDto));
    }

    @Test
    void convertDtoToTaskShouldWork() {
        //given
        TaskDto dto = new TaskDto();
        dto.setTaskId(50L);
        dto.setName("this object transfers data");
        dto.setActive(false);
        dto.setDateTimeMade(now);
        dto.setUrl(TASK_ROOT_URL + "/" + 50L);

        //when
        Task task = mapper.convertTaskDto(dto);

        //verify
        assertTrue(mapper.compare(task, dto));
    }

    @Test
    void compareTaskToDtoShouldReturnTrueWhenTheyAreEqual() {
        //given
        Task task = new Task("task");
        task.setTimeCreated(now);
        task.setActive(false);

        TaskDto dto = new TaskDto();
        dto.setName("task");
        dto.setDateTimeMade(now);
        dto.setActive(false);
        dto.setUrl(TASK_ROOT_URL + "/" + task.getId());

        //when
        boolean areEqual = mapper.compare(task, dto);

        //verify
        assertTrue(areEqual);
    }

    @Test
    void compareTaskToDtoShouldReturnFalseWhenTheyAreEqual() {
        //given
        Task task = new Task("task");
        task.setTimeCreated(now);
        task.setActive(false);

        TaskDto dto = new TaskDto();

        //when
        boolean areEqual = mapper.compare(task, dto);

        //verify
        assertFalse(areEqual);
    }
}