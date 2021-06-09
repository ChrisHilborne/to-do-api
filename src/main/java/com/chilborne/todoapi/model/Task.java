package com.chilborne.todoapi.model;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class Task {

    private long id;
    private ToDoList list;
    private String name;
    private String description;
    private LocalDateTime timeCreated;
    private boolean active = true;

    protected Task() {}
}
