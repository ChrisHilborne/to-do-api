package com.chilborne.todoapi.persistance.model;

import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.validation.OnPersist;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
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
    @NotBlank(groups = OnPersist.class, message = "name is compulsory")
    private String name;

    @Column(name = "desc")
    @Size(min = 3, max = 225, message = "description must be between 3 and 225 characters long")
    private String description;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    @CreationTimestamp
    @Column(name = "date_time_created", insertable = false, columnDefinition = "TIMESTAMP")
    @Null(groups = OnPersist.class, message = "time_created is automatically generated on task creation")
    private LocalDateTime timeCreated;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    @Column(name = "date_time_finished", columnDefinition = "TIMESTAMP")
    @Null(groups = OnPersist.class, message = "time_completed is automatically generated on task completion")
    private LocalDateTime timeCompleted;

    @Column(name = "active", columnDefinition = "BOOLEAN DEFAULT TRUE" , nullable = false)
    @Null(groups = OnPersist.class, message = "active is automatically set to true when on task creation")
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
        //must check both @active and @timeComplete because @active is only instantiated when persisted in DB
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
        //some tests do not save bean to DB - therefore timeCreated is never instantiated
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

        if (id != task.id) return false;
        if (active != task.active) return false;
        if (!Objects.equals(toDoList, task.toDoList)) return false;
        if (!Objects.equals(name, task.name)) return false;
        if (!Objects.equals(description, task.description)) return false;
        //timeCreated is instantiated when bean is saved to DB for the first time - some unit tests don't use the DB
        if ((timeCreated != null && task.timeCreated != null) && !Objects.equals(timeCreated, task.timeCreated)) return false;
        return Objects.equals(timeCompleted, task.timeCompleted);
    }

    /**
     * This is a utility method to help test returned Dto from TaskService
     * @param taskDto
     * @return boolean
     */
    public boolean equalsDto(TaskDto taskDto) {
        if (id != taskDto.getTaskId()) return false;
        if (active != taskDto.isActive()) return false;
        if (!Objects.equals(name, taskDto.getName())) return false;
        if (!Objects.equals(description, taskDto.getDescription())) return false;
        //timeCreated is instantiated when bean is saved to DB for the first time - some unit tests don't use the DB
        if ((timeCreated != null && taskDto.getDateTimeMade() != null) && !Objects.equals(this.getTimeCreated(), taskDto.getDateTimeMade())) return false;
        return Objects.equals(timeCompleted, taskDto.getDateTimeFinished());
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
