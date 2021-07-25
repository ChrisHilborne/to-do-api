package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.UsernameAlreadyExistsException;
import com.chilborne.todoapi.persistance.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.validation.constraints.Email;

public interface UserService extends UserDetailsService {

    UserDto getUserByUsername(String username);

    UserDto createUser(UserDto dto);

    UserDto changeUsername(String oldUsername, String newUsername);

    UserDto changeEmail(String username, @Email String email);

    void deleteUser(String username);

    void changePassword(String username, String newPwd);

    boolean isUsernameUnique(String username) throws UsernameAlreadyExistsException;
}
