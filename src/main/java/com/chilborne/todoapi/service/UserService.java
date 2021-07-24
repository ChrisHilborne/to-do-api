package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.UsernameAlreadyExistsException;
import com.chilborne.todoapi.persistance.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.validation.constraints.Email;

public interface UserService extends UserDetailsService {

    UserDto getUserByUsername(String username);

    UserDto createUser(UserDto dto);

    UserDto changeUsername(UserDto dto, String username);

    UserDto changeEmail(UserDto dto, @Email String email);

    void changePassword(UserDto dto, String newPwd);

    void checkUsernameIsUnique(String username) throws UsernameAlreadyExistsException;
}
