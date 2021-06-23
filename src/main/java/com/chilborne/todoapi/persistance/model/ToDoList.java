package com.chilborne.todoapi.persistance.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
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

    @Lob
    @Column(name = "desc", columnDefinition = "CLOB")
    private String description;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    @Column(name = "date_time_made", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime dateTimeCreated = LocalDateTime.now();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "list")
    private List<Task> tasks = new LinkedList<>();

    @Column(name = "ACTIVE", columnDefinition = "BOOLEAN DEFAULT TRUE", nullable = false)
    private boolean active = true;

    protected ToDoList() {}

    public ToDoList(String name) {
        this.name = name;
    }

    public ToDoList(String name, List<Task> tasks) {
        this(name);
        this.tasks = new LinkedList<>(tasks);
    }

    public ToDoList(String name, String description) {
        this(name);
        this.description = description;
    }

    public ToDoList(String name, String description, List<Task> tasks) {
        this(name, description);
        this.tasks = new LinkedList<>(tasks);
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }

    public long getId() {
        return id;
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

    public LocalDateTime getDateTimeCreated() {
        return LocalDateTime.of(dateTimeCreated.toLocalDate(), dateTimeCreated.toLocalTime());
    }

    public void setDateTimeCreated(LocalDateTime dateTimeCreated) {
        this.dateTimeCreated = dateTimeCreated;
    }

    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

    public void setTasks(Collection<Task> tasks) {
        this.tasks = new LinkedList<>(tasks);
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
