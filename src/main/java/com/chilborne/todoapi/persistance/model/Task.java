package com.chilborne.todoapi.persistance.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
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

    @Lob
    @Column(name = "desc", columnDefinition = "CLOB")
    private String description;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    @Column(name = "date_time_made", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime timeCreated = LocalDateTime.now().withNano(0);

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    @Column(name = "date_time_finished", columnDefinition = "TIMESTAMP")
    private LocalDateTime timeCompleted;

    @Column(name = "active", columnDefinition = "boolean default true" ,nullable = false)
    private boolean active = true;

    protected Task() {}

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
        if (!this.active) return false;
        this.active = false;
        timeCompleted = LocalDateTime.now().withNano(0);
        return true;
    }

    public long getTaskId() {
        return id;
    }

    public void setTaskId(long taskId) {
        this.id = taskId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (id != task.id) return false;
        if (active != task.active) return false;
        if (!Objects.equals(toDoList, task.toDoList)) return false;
        if (!Objects.equals(name, task.name)) return false;
        if (!Objects.equals(description, task.description)) return false;
        if (!Objects.equals(timeCreated, task.timeCreated)) return false;
        return Objects.equals(timeCompleted, task.timeCompleted);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (toDoList != null ? toDoList.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (timeCreated != null ? timeCreated.hashCode() : 0);
        result = 31 * result + (timeCompleted != null ? timeCompleted.hashCode() : 0);
        result = 31 * result + (active ? 1 : 0);
        return result;
    }
}
