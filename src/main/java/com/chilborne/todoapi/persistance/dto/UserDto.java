package com.chilborne.todoapi.persistance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ApiModel(value = "user")
public class UserDto {

    @NotBlank(message = "username is compulsory")
    private String username;

    @NotBlank(message = "password is compulsory")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "email is compulsory")
    @Email(message = "email not valid")
    private String email;

    @ApiModelProperty(name = "to_do_lists")
    private List<ToDoListDto> toDoLists = new ArrayList<>();

    public UserDto() {
    }

    public UserDto(
            @NotNull(message = "username is compulsory") String username,
            String password,
            @Email(message = "email not valid") String email)
    {
        this.username = username;
        this.password = password;
        this.email = email;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDto userDto = (UserDto) o;

        if (!username.equals(userDto.username)) return false;
        if (!password.equals(userDto.password)) return false;
        if (!Objects.equals(email, userDto.email)) return false;
        return Objects.equals(toDoLists, userDto.toDoLists);
    }

    @Override
    public int hashCode() {
        int result = username.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (toDoLists != null ? toDoLists.hashCode() : 0);
        return result;
    }
}
