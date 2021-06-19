package com.chilborne.todoapi.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "lists")
public class ToDoList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "list_id", updatable = false, unique = true, nullable = false)
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "desc")
    private String description;

    @Column(name = "date_time_made", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime dateTimeCreated = LocalDateTime.now();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "list")
    private List<Task> tasks = new LinkedList<>();

    @Column(name = "ACTIVE", columnDefinition = "boolean default true", nullable = false)
    private boolean active = true;

    protected ToDoList() {}

    public ToDoList(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDateTimeCreated() {
        return dateTimeCreated;
    }

    public void setDateTimeCreated(LocalDateTime dateTimeCreated) {
        this.dateTimeCreated = dateTimeCreated;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
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

        ToDoList toDoList = (ToDoList) o;

        if (active != toDoList.active) return false;
        if (!name.equals(toDoList.name)) return false;
        if (!dateTimeCreated.equals(toDoList.dateTimeCreated)) return false;
        return Objects.equals(tasks, toDoList.tasks);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + dateTimeCreated.hashCode();
        result = 31 * result + (tasks != null ? tasks.hashCode() : 0);
        result = 31 * result + (active ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ToDoList {" +
                "name='" + name + '\'' +
                ", dateCreated=" + dateTimeCreated +
                ", tasks=" + tasks +
                ", active=" + active +
                " }";
    }
}
