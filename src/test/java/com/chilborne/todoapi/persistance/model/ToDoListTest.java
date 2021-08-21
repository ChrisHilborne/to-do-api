package com.chilborne.todoapi.persistance.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class ToDoListTest {

    ToDoList list;
    Task first;
    Task second;

    @BeforeEach
    void init() {
        list = new ToDoList("To Do");
        first = new Task(list, "First");
        first.setId(1L);
        second = new Task(list, "Second");
        second.setId(2L);
    }

    @Test
    void addTask() {
        //given
        list.setTasks(List.of(first));

        //when
        list.addTask(second);

        //verify
        assertEquals(List.of(first, second), list.getTasks());
    }

    @Test
    void removeTaskShouldRemoveSingleTaskAndReturnTrue() {
        //given
        list.setTasks(List.of(first, second));

        //when
        boolean removed = list.removeTask(second.getId());

        //verify
        assertEquals(List.of(first), list.getTasks());
        assertTrue(removed);
    }

    @Test
    void removeTaskShouldNotRemoveAnyTaskAndReturnFalseWhenTaskIsNotPresent() {
        //given
        list.setTasks(List.of(first, second));

        //when
        boolean removed = list.removeTask(5L);

        //verify
        assertEquals(List.of(first, second), list.getTasks());
        assertFalse(removed);
    }

    @Test
    void getTasksShouldReturnAnUnmodifiableList() {
        //given
        list.addTask(first);
        list.addTask(second);

        //when
        List<Task> tasks = list.getTasks();

        //verify
        Exception e = assertThrows(UnsupportedOperationException.class,
                () -> tasks.add(first));
    }


}