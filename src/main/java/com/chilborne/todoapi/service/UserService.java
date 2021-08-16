package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.UsernameAlreadyExistsException;
import com.chilborne.todoapi.persistance.dto.UserDto;
import com.chilborne.todoapi.persistance.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Email;

public interface UserService {

  @Transactional(readOnly = true)
  UserDto getUserByUsername(String username) throws UsernameNotFoundException, AccessDeniedException;

  User getUserIfAuthorized(String username);

  @Transactional
  UserDto createUser(UserDto dto) throws UsernameAlreadyExistsException;

  @Transactional
  UserDto changeUsername(String oldUsername, String newUsername) throws UsernameAlreadyExistsException, AccessDeniedException;

  @Transactional
  UserDto changeEmail(String username, @Email String email) throws AccessDeniedException;

  @Transactional
  void deleteUser(String username) throws UsernameNotFoundException, AccessDeniedException;

  @Transactional
  void changePassword(String username, String newPwd) throws AccessDeniedException;

  @Transactional(readOnly = true)
  boolean isUsernameUnique(String username);

  void checkUserAccess(User user);
}
