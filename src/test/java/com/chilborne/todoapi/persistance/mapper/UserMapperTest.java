package com.chilborne.todoapi.persistance.mapper;

import com.chilborne.todoapi.persistance.dto.ToDoListDto;
import com.chilborne.todoapi.persistance.dto.UserDto;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.persistance.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    UserMapper mapper = UserMapper.INSTANCE;

    User user;
    UserDto dto;

    static final String NAME = "USER";
    static final String PASSWORD = "SECRET";
    static final String EMAIL = "email@somewhere.com";

    @BeforeEach
    void init() {
        user = new User();
        user.setUsername(NAME);
        user.setPassword(PASSWORD);
        user.setEmail(EMAIL);
        user.setToDoLists(List.of(new ToDoList()));

        dto = new UserDto();
        dto.setUsername(NAME);
        dto.setPassword(PASSWORD);
        dto.setEmail(EMAIL);
        dto.setToDoLists(List.of(new ToDoListDto()));
    }

    @Test
    void convertUser() {
        //when
        dto = mapper.convertUser(user);

        //verify
        assertTrue(mapper.compare(user, dto));
    }

    @Test
    void convertUserDto() {
        //when
        user = mapper.convertUserDto(dto);

        //verify
        assertTrue(mapper.compare(user, dto));
    }

    @Test
    void compare() {
        assertTrue(mapper.compare(user, dto));
    }
}