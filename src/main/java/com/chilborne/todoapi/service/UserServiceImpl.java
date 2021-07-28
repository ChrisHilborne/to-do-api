package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.UsernameAlreadyExistsException;
import com.chilborne.todoapi.persistance.dto.UserDto;
import com.chilborne.todoapi.persistance.mapper.UserMapper;
import com.chilborne.todoapi.persistance.model.User;
import com.chilborne.todoapi.persistance.repository.UserRepository;
import com.chilborne.todoapi.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Email;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper mapper;

  private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

  public UserServiceImpl(
      UserRepository repository, PasswordEncoder passwordEncoder, UserMapper mapper) {
    this.repository = repository;
    this.passwordEncoder = passwordEncoder;
    this.mapper = mapper;
  }

  @Override
  public UserDto getUserByUsername(String username) {
    logger.info("Fetching User with username {}", username);
    User user =
        repository
            .findByUsername(username)
            .orElseThrow(
                () ->
                    new UsernameNotFoundException(
                        "User with username" + username + "does not exist"));
    return mapper.convertUser(user);
  }

  private User getUser(String username) {
    logger.info("Fetching User with username {}", username);
    return repository
        .findByUsername(username)
        .orElseThrow(
            () ->
                new UsernameNotFoundException("User with username" + username + "does not exist"));
  }

  private UserDto saveUser(User user) {
    logger.info("Saving User {}", user.getUsername());
    return mapper.convertUser(repository.save(user));
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
    User toUpdate = getUser(oldUsername);
    toUpdate.setUsername(newUsername);
    return saveUser(toUpdate);
  }

  @Override
  public UserDto changeEmail(String username, @Email String email) {
    logger.info("Changing User:{} email to {}", username, email);
    User toUpdate = getUser(username);
    toUpdate.setEmail(email);
    return saveUser(toUpdate);
  }

  @Override
  public void deleteUser(String username) {
    if (!repository.existsByUsername(username)) {
      throw new UsernameNotFoundException(username);
    }
    logger.info("Deleting User:{}", username);
    repository.deleteByUsername(username);
  }

  @Override
  public void changePassword(String username, String newPwd) {
    logger.info("Changing password for User: {}", username);
    User toUpdate = getUser(username);
    toUpdate.setPassword(passwordEncoder.encode(newPwd));
    saveUser(toUpdate);
  }

  @Override
  public boolean isUsernameUnique(String username) throws UsernameAlreadyExistsException {
    return !repository.existsByUsername(username);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return new UserPrincipal(getUser(username));
  }

  /**
   * Method to fetch User Entity from DB using UserDto username and return fetched User Entity Id
   *
   * @param dto UserDto
   * @return UUID userId
   */
  private UUID getUserId(UserDto dto) {
    return getUser(dto.getUsername()).getUserId();
  }
}
