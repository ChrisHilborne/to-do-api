package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.UsernameAlreadyExistsException;
import com.chilborne.todoapi.persistance.dto.UserDto;
import com.chilborne.todoapi.persistance.mapper.UserMapper;
import com.chilborne.todoapi.persistance.model.User;
import com.chilborne.todoapi.persistance.repository.UserRepository;
import com.chilborne.todoapi.security.UserPrincipal;
import com.chilborne.todoapi.security.access.UserAccessManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Email;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {

  private final UserRepository userRepository;
  private final UserAccessManager accessManager;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper mapper;

  private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

  public UserServiceImpl(
      UserRepository userRepository,
      UserAccessManager accessManager,
      PasswordEncoder passwordEncoder,
      UserMapper mapper) {
    this.userRepository = userRepository;
    this.accessManager = accessManager;
    this.passwordEncoder = passwordEncoder;
    this.mapper = mapper;
  }

  @Override
  public UserDto getUserByUsername(String username) {
    logger.info("Fetching User with username {}", username);
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () ->
                    new UsernameNotFoundException(
                        "User with username" + username + "does not exist"));
    checkUserAccess(user);
    return mapper.convertUser(user);
  }

  @Override
  public User getUserIfAuthorized(String username) {
    logger.info("Fetching User with username {}", username);
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () ->
                    new UsernameNotFoundException(
                        "User with username" + username + "does not exist"));
    checkUserAccess(user);
    return user;
  }

  private UserDto saveUser(User user) {
    logger.info("Saving User {}", user.getUsername());
    return mapper.convertUser(userRepository.save(user));
  }

  @Override
  public UserDto createUser(UserDto userDto) {
    if (!isUsernameUnique(userDto.getUsername())) {
      throw new UsernameAlreadyExistsException(userDto.getUsername());
    }
    logger.info("Creating new user: {}", userDto.getUsername());
    User entity = mapper.convertUserDto(userDto);
    entity.setPassword(passwordEncoder.encode(userDto.getPassword()));
    return saveUser(entity);
  }

  @Override
  public UserDto changeUsername(String oldUsername, String newUsername) {
    if (!isUsernameUnique(newUsername)) {
      throw new UsernameAlreadyExistsException(newUsername);
    }
    logger.info("Changing username for User:{} to {}", oldUsername, newUsername);
    User toUpdate = getUserIfAuthorized(oldUsername);
    toUpdate.setUsername(newUsername);
    return saveUser(toUpdate);
  }

  @Override
  public UserDto changeEmail(String username, @Email String email) {
    logger.info("Changing User:{} email to {}", username, email);
    User toUpdate = getUserIfAuthorized(username);
    toUpdate.setEmail(email);
    return saveUser(toUpdate);
  }

  @Override
  public void deleteUser(String username) {
    if (!userRepository.existsByUsername(username)) {
      throw new UsernameNotFoundException(username);
    }
    accessManager.checkAccess(username);
    logger.info("Deleting User:{}", username);
    userRepository.deleteByUsername(username);
  }

  @Override
  public void changePassword(String username, String newPwd) {
    logger.info("Changing password for User: {}", username);
    User toUpdate = getUserIfAuthorized(username);
    toUpdate.setPassword(passwordEncoder.encode(newPwd));
    saveUser(toUpdate);
  }

  @Override
  public boolean isUsernameUnique(String username) throws UsernameAlreadyExistsException {
    return !userRepository.existsByUsername(username);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return new UserPrincipal(
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(username)));
  }

  @Override
  public void checkUserAccess(User user) {
    accessManager.checkAccess(user);
  }

}
