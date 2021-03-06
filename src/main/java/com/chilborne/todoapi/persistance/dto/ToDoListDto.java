package com.chilborne.todoapi.persistance.dto;

import com.chilborne.todoapi.persistance.validation.OnPersist;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Schema(title = "To-Do List")
public class ToDoListDto {

    @Null(groups = OnPersist.class, message = "to_do_list id is autogenerated on list creation")
    private long listId;

    @NotBlank(groups = OnPersist.class, message = "name is compulsory")
    private String name;

    @Size(min = 3, max = 255, message = "description must be between 3 and 255 characters long")
    private String description;

    @Null(message = "user is auto-set upon list creation")
    private String username;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    @Null(groups = OnPersist.class, message = "date_time_made is automatically generated on list creation")
    private LocalDateTime dateTimeMade;

    @Null(groups = OnPersist.class, message = "You must add tasks separately using /api/v1/list/{list_id}/task/add")
    private List<TaskDto> tasks = new ArrayList<>();

    @Null(groups = OnPersist.class, message = "To-Do List is activated on creation")
    private boolean active = true;

    @Null(groups = OnPersist.class, message = "url is autogenerated on list creation")
    private String url;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getDateTimeMade() {
        return dateTimeMade != null ? dateTimeMade.withNano(0) : null;
    }

    public void setDateTimeMade(LocalDateTime dateTimeMade) {
        this.dateTimeMade = dateTimeMade;
    }

    public List<TaskDto> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDto> tasks) {
        this.tasks = tasks;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ToDoListDto listDto = (ToDoListDto) o;

        if (listId != listDto.listId) return false;
        if (active != listDto.active) return false;
        if (!name.equals(listDto.name)) return false;
        if (!description.equals(listDto.description)) return false;
        if (username != null ? !username.equals(listDto.username) : listDto.username != null) return false;
        if (dateTimeMade != null ? !dateTimeMade.equals(listDto.dateTimeMade) : listDto.dateTimeMade != null)
            return false;
        if (tasks != null ? !tasks.equals(listDto.tasks) : listDto.tasks != null) return false;
        return url != null ? url.equals(listDto.url) : listDto.url == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (listId ^ (listId >>> 32));
        result = 31 * result + name.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (dateTimeMade != null ? dateTimeMade.hashCode() : 0);
        result = 31 * result + (tasks != null ? tasks.hashCode() : 0);
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ToDoListDto{" +
          "listId=" + listId +
          ", name='" + name + '\'' +
          ", description='" + description + '\'' +
          ", username='" + username + '\'' +
          ", dateTimeMade=" + dateTimeMade +
          ", tasks=" + tasks +
          ", active=" + active +
          ", url='" + url + '\'' +
          '}';
    }
}
