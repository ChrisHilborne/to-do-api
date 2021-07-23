package com.chilborne.todoapi.persistance.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class UserDto {

    @NotNull(message = "username is compulsory")
    private String username;

    @JsonIgnore
    private String password;

    @Email(message = "email not valid")
    private String email;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<ToDoListDto> getToDoLists() {
        return toDoLists;
    }

    public void setToDoLists(List<ToDoListDto> toDoLists) {
        this.toDoLists = toDoLists;
    }
}
