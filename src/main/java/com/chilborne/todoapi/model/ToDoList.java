package com.chilborne.todoapi.model;

import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
public class ToDoList {

    private long id;
    private String name;
    private String description;
    private User owner;
    private LocalDate dateCreated;
    private List<Task> tasks;
    private boolean active = true;

    protected ToDoList() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
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
        if (!owner.equals(toDoList.owner)) return false;
        if (!dateCreated.equals(toDoList.dateCreated)) return false;
        return Objects.equals(tasks, toDoList.tasks);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + owner.hashCode();
        result = 31 * result + dateCreated.hashCode();
        result = 31 * result + (tasks != null ? tasks.hashCode() : 0);
        result = 31 * result + (active ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ToDoList {" +
                "name='" + name + '\'' +
                ", owner=" + owner +
                ", dateCreated=" + dateCreated +
                ", tasks=" + tasks +
                ", active=" + active +
                " }";
    }
}
