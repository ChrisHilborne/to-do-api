package com.chilborne.todoapi.web.controller.v1;

import com.chilborne.todoapi.persistance.dto.UserDto;
import com.chilborne.todoapi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Validated
@RestController
@RequestMapping(path = "api/v1/user")
public class UserController {

  private final UserService service;
  private final Logger logger = LoggerFactory.getLogger(UserController.class);

  public UserController(UserService service) {
    this.service = service;
  }

  @GetMapping(path = "/{username}", produces = "application/json")
  public ResponseEntity<UserDto> getUser(@PathVariable String username) {
    logger.info("Fetching User: {}", username);
    UserDto user = service.getUserByUsername(username);
    return ResponseEntity.ok(user);
  }

  @PostMapping(path = "/register", produces = "application/json", consumes = "application/json")
  public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto user) {
    logger.info("Creating new User: {}", user.getUsername());
    UserDto newUser = service.createUser(user);
    return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
  }

  @PatchMapping(path = "/{username}/username", produces = "application/json")
  public ResponseEntity<UserDto> changeUsername(
      @PathVariable String username, @RequestBody @NotBlank(message = "username cannot be blank") String newUsername) {
    logger.info("Changing User: {} username to {}", username, newUsername);
    UserDto updatedUser = service.changeUsername(username, newUsername);
    return ResponseEntity.ok(updatedUser);
  }

  @PatchMapping(path = "/{username}/password", produces = "application/json", consumes = "application/json")
  public ResponseEntity changePassword(
      @PathVariable String username,
      @RequestBody @NotBlank(message = "password cannot be blank") String password) {
    logger.info("Changing password for User: {}", username);
    service.changePassword(username, password);
    return ResponseEntity.ok().build();
  }

  @Validated
  @PatchMapping(path = "/{username}/email", produces = "application/json")
  public ResponseEntity<UserDto> changeEmail(
      @PathVariable String username,
      @RequestBody @Email(message = "Email provided is not valid") String email) {
    logger.info("Changing email for User: {} to {}", username, email);
    UserDto updatedUser = service.changeEmail(username, email);
    return ResponseEntity.ok(updatedUser);
  }

  @DeleteMapping(path = "/{username}")
  public ResponseEntity deleteUser(@PathVariable String username) {
    logger.info("Deleting User: {}", username);
    service.deleteUser(username);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
