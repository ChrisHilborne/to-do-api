package com.chilborne.todoapi.persistance.dto;

import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.validation.OnPersist;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ToDoListDto {

    @Null(groups = OnPersist.class, message = "list_id is generated on list creation")
    private long listId;

    @NotBlank(groups = OnPersist.class, message = "name is compulsory")
    private String name;

    @Size(min = 3, max = 255, message = "description must be between 3 and 255 characters long")
    private String description;

    @Null(groups = OnPersist.class, message = "date_time_made is automatically generated on list creation")
    private LocalDateTime dateTimeMade;

    private List<Task> tasks = new LinkedList<>();

    @Null(groups = OnPersist.class, message = "ToDoList is activated on list creation")
    private boolean active = true;

    public ToDoListDto() {
    }

    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
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

    public LocalDateTime getDateTimeMade() {
        return dateTimeMade != null ? dateTimeMade.withNano(0) : null;
    }

    public void setDateTimeMade(LocalDateTime dateTimeMade) {
        this.dateTimeMade = dateTimeMade;
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

        ToDoListDto that = (ToDoListDto) o;

        if (listId != that.listId) return false;
        if (active != that.active) return false;
        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(description, that.description)) return false;
        if (!Objects.equals(dateTimeMade, that.dateTimeMade)) return false;
        return Objects.equals(tasks, that.tasks);
    }

    @Override
    public int hashCode() {
        int result = (int) (listId ^ (listId >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (dateTimeMade != null ? dateTimeMade.hashCode() : 0);
        result = 31 * result + (tasks != null ? tasks.hashCode() : 0);
        result = 31 * result + (active ? 1 : 0);
        return result;
    }
}
