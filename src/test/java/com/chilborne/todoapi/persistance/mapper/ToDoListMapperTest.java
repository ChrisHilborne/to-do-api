package com.chilborne.todoapi.persistance.mapper;

import com.chilborne.todoapi.persistance.dto.ToDoListDto;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
public class ToDoListMapperTest {

    ToDoListMapper mapper = ToDoListMapper.INSTANCE;

    LocalDateTime now = LocalDateTime.now();

    static final String NAME = "name";
    static final String DESC = "desc";

    @Test
    public void convertToDoListToDto() {
        //given
        ToDoList list = new ToDoList(NAME, DESC);
        list.setTimeCreated(now);
        Task task = new Task(NAME);
        list.addTask(task);

        //when
        ToDoListDto dto = mapper.convertToDoList(list);

        //verify
        assertTrue(mapper.compare(list, dto));
    }

    @Test
    public void convertDtoToList() {
        //given
        ToDoListDto dto = new ToDoListDto();
        dto.setActive(false);
        dto.setDateTimeMade(now);
        dto.setName(NAME);
        dto.setDescription(DESC);
        dto.setTasks(List.of(new Task("NAME")));

        //when
        ToDoList list = mapper.convertListDto(dto);

        //verify
        assertTrue(mapper.compare(list, dto));
    }

    @Test
    public void CompareShouldReturnTrueWhenToDoListAndDtoAreEqual() {
        //given
        ToDoList list = new ToDoList("NAME", "DESC");
        list.setActive(false);
        list.setTimeCreated(now);

        ToDoListDto dto = new ToDoListDto();
        dto.setName("NAME");
        dto.setDescription("DESC");
        dto.setActive(false);
        dto.setDateTimeMade(now);

        //when
        boolean areEqual = mapper.compare(list, dto);

        //verify
        assertTrue(areEqual);
    }

    @Test
    void compareShouldReturnFalseWhenToDoListAndDtoAreNotTheSame() {
        //given
        ToDoList list = new ToDoList("NAME", "DESC");
        list.setActive(false);
        list.setTimeCreated(now);

        ToDoListDto dto = new ToDoListDto();

        //when
        boolean areEqual = mapper.compare(list, dto);

        //verify
        assertFalse(areEqual);
    }
}
