package com.chilborne.todoapi.persistance.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@DynamicUpdate
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id", unique = true, nullable = false)
    private long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "list_id")
    private ToDoList toDoList;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "desc")
    private String description;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    @CreationTimestamp
    @Column(name = "date_time_created", insertable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime timeCreated;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    @Column(name = "date_time_finished", columnDefinition = "TIMESTAMP")
    private LocalDateTime timeCompleted;

    @Column(name = "active", columnDefinition = "BOOLEAN DEFAULT TRUE" , nullable = false)
    private boolean active;

    public Task() {}

    public Task(String name) {
        this.name = name;
    }

    public Task(ToDoList toDoList, String name) {
        this(name);
        this.toDoList = toDoList;
    }

    public Task(ToDoList toDoList, String name, String description) {
        this(toDoList, name);
        this.description = description;
    }

    public boolean complete() {
        //must check both @active and @timeComplete because @active is only populated when persisted in DB
        if (!this.active && timeCompleted != null) return false;
        this.active = false;
        timeCompleted = LocalDateTime.now().withNano(0);
        return true;
    }

    public long getId() {
        return id;
    }

    public void setId(long Id) {
        this.id = Id;
    }

    public ToDoList getToDoList() {
        return toDoList;
    }

    public void setToDoList(ToDoList toDoList) {
        this.toDoList = toDoList;
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
        //some tests do not save POJO to DB - therefore timeCreated is never instantiated
        return timeCreated != null ? timeCreated.withNano(0) : null;
    }

    public void setTimeCreated(LocalDateTime timeCreated) {
        this.timeCreated = timeCreated;
    }

    public LocalDateTime getTimeCompleted() {
        return timeCompleted != null ? timeCompleted.withNano(0) : null;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (active != task.active) return false;
        if (!name.equals(task.name)) return false;
        if (description != null ? !description.equals(task.description) : task.description != null) return false;
        if (timeCreated != null ? !timeCreated.equals(task.timeCreated) : task.timeCreated != null) return false;
        return timeCompleted != null ? timeCompleted.equals(task.timeCompleted) : task.timeCompleted == null;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (timeCreated != null ? timeCreated.hashCode() : 0);
        result = 31 * result + (timeCompleted != null ? timeCompleted.hashCode() : 0);
        result = 31 * result + (active ? 1 : 0);
        return result;
    }
}
