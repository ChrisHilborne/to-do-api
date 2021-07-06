package com.chilborne.todoapi.persistance.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    Task testTask;

    @BeforeEach
    void init() {
        testTask = new Task("test");
    }

    @Test
    void completeShouldReturnTrueAndPopulateTimeCompletedWhenNotAlreadyCompleted() {
        //given

        //when
        boolean completed = testTask.complete();

        //verify
        assertTrue(completed);
        assertFalse(testTask.isActive());
        assertNotNull(testTask.getTimeCompleted());

    }

    @Test
    void completeShouldReturnFalseWhenAlreadyCompleted() {
        //given
        LocalDateTime now = LocalDateTime.now();
        testTask.setActive(false);
        testTask.setTimeCompleted(now);

        //when
        boolean completed = testTask.complete();

        //verify
        assertFalse(completed);
        assertFalse(testTask.isActive());
        assertEquals(now.withNano(0), testTask.getTimeCompleted());
        
    }
}