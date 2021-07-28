package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.UsernameAlreadyExistsException;
import com.chilborne.todoapi.persistance.dto.UserDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.validation.constraints.Email;

public interface UserService extends UserDetailsService {

  @PreAuthorize("#username == authentication.principal.username")
  UserDto getUserByUsername(String username);

  UserDto createUser(UserDto dto);

  @PreAuthorize("#oldUsername == authentication.principal.username")
  UserDto changeUsername(String oldUsername, String newUsername);

  @PreAuthorize("#username == authentication.principal.username")
  UserDto changeEmail(String username, @Email String email);

  @PreAuthorize("#username == authentication.principal.username")
  void deleteUser(String username);

  @PreAuthorize("#username == authentication.principal.username")
  void changePassword(String username, String newPwd);

  boolean isUsernameUnique(String username) throws UsernameAlreadyExistsException;
}
