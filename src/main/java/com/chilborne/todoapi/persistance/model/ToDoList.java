package com.chilborne.todoapi.persistance.model;

import com.chilborne.todoapi.persistance.validation.OnPersist;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Entity
@DynamicUpdate
@Table(name = "lists")
public class ToDoList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "list_id", updatable = false, unique = true, nullable = false)
    private long id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "name is compulsory")
    private String name;

    @Column(name = "desc")
    @Size(min = 3, max = 255, message = "description must be between 3 and 255 characters long")
    private String description;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    @CreationTimestamp
    @Column(name = "date_time_made", updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime timeCreated;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "toDoList", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Task> tasks = new LinkedList<>();

    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "username")
    private User user;

    @Column(name = "active", columnDefinition = "BOOLEAN DEFAULT TRUE", nullable = false)
    private boolean active = true;

    public ToDoList() {}

    public ToDoList(String name) {
        this.name = name;
    }

    public ToDoList(String name, String description) {
        this(name);
        this.description = description;
    }

    public ToDoList(String name, List<Task> tasks) {
        this(name);
        this.tasks.addAll(tasks);
    }

    public ToDoList(String name, String description, List<Task> tasks) {
        this(name, tasks);
        this.description = description;
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public boolean removeTask(long taskId) {
        return this.tasks.removeIf(
                task -> task.getId() == taskId);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        if (id > 0) {
            this.id = id;
        }
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
        //some tests do not save bean to DB - therefore timeCreated is never instantiated
        return timeCreated != null ? timeCreated.withNano(0) : null;
    }

    public void setTimeCreated(LocalDateTime timeCreated) {
        this.timeCreated = timeCreated;
    }

    public List<Task> getTasks() {
        return tasks != null ? List.copyOf(tasks) : null;
    }

    public void setTasks(List<Task> tasks) {
        // Hibernate tracks collection to persist
        // therefore we must use the same collection instance throughout the life of a ToDoList
        if (this.tasks != tasks) {
            this.tasks.clear();
            this.tasks.addAll(tasks);
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
        if (!Objects.equals(description, toDoList.description))
            return false;
        // timeCreated is populated when first persisted to DB
        // some test cases will therefore not have this field populated
        if (this.timeCreated != null && toDoList.timeCreated != null) {
            return Objects.equals(timeCreated, toDoList.timeCreated);
        } else return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (timeCreated != null ? timeCreated.hashCode() : 0);
        result = 31 * result + (active ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ToDoList {" +
                "name='" + name + '\'' +
                ", dateCreated=" + timeCreated +
                ", tasks=" + tasks +
                ", active=" + active +
                " }";
    }
}
