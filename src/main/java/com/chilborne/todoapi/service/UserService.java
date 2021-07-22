package com.chilborne.todoapi.service;

import com.chilborne.todoapi.persistance.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    UserDto getUserByUserName(String username);

    UserDto createUser(UserDto user);

    UserDto updateUser(UserDto user);

    void changePassword(String oldPwd, String newPwd);

    boolean userExists(String username);


}
