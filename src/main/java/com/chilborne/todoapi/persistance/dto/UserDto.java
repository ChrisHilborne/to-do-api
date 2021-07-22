package com.chilborne.todoapi.persistance.dto;

import com.chilborne.todoapi.persistance.model.ToDoList;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDto {

    @NotNull(message = "username is compulsory")
    private String username;

    @JsonIgnore
    private String password;

    private Email email;

    @ApiModelProperty(name = "to_do_lists")
    private List<ToDoListDto> toDoLists = new ArrayList<>();

    public UserDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public List<ToDoListDto> getToDoLists() {
        return toDoLists;
    }

    public void setToDoLists(List<ToDoListDto> toDoLists) {
        this.toDoLists = toDoLists;
    }
}
