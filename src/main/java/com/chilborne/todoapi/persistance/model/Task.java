package com.chilborne.todoapi.persistance.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id", unique = true, nullable = false)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id")
    @JsonIgnore
    private ToDoList list;

    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "desc", columnDefinition = "CLOB")
    private String description;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    @Column(name = "date_time_made", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime timeCreated = LocalDateTime.now();

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    @Column(name = "date_time_finished", columnDefinition = "TIMESTAMP")
    private LocalDateTime timeCompleted;

    @Column(name = "active", columnDefinition = "boolean default true" ,nullable = false)
    private boolean active = true;

    protected Task() {}

    public Task(String name) {
        this.name = name;
    }

    public Task(ToDoList list, String name) {
        this.list = list;
        this.name = name;
    }

    public Task(ToDoList list, String name, String description) {
        this(list, name);
        this.description = description;
    }

    public long getTaskId() {
        return id;
    }

    public void setTaskId(long taskId) {
        this.id = taskId;
    }

    public ToDoList getList() {
        return list;
    }

    public void setList(ToDoList list) {
        this.list = list;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(LocalDateTime timeCreated) {
        this.timeCreated = timeCreated;
    }

    public LocalDateTime getTimeCompleted() {
        return timeCompleted;
    }

    public void setTimeCompleted(LocalDateTime timeCompleted) {
        this.timeCompleted = timeCompleted;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
