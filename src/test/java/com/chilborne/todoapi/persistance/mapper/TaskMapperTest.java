package com.chilborne.todoapi.persistance.mapper;

import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.model.Task;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

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
        TaskDto taskDto = mapper.convert(task);

        //verify
        assertTrue(task.equalsDto(taskDto));
    }

    @Test
    void convertDtoToTaskShouldWork() {
        //given
        TaskDto dto = new TaskDto();
        dto.setTaskId(50L);
        dto.setName("this object transfers data");
        dto.setActive(false);
        dto.setDateTimeMade(now);

        //when
        Task task = mapper.convert(dto);

        //verify
        assertTrue(task.equalsDto(dto));
    }
}