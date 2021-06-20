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
        second = new Task(list, "Second");
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
    void removeTask() {
        //given
        list.setTasks(List.of(first, second));

        //when
        list.removeTask(second);

        //verify
        assertEquals(List.of(first), list.getTasks());

    }
}