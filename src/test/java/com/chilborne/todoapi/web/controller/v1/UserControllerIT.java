package com.chilborne.todoapi.web.controller.v1;

import com.chilborne.todoapi.persistance.dto.UserDto;
import com.chilborne.todoapi.persistance.model.User;
import com.chilborne.todoapi.persistance.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@AutoConfigureMockMvc
class UserControllerIT {

  static final String USERNAME = "name";
  static final String PASSWORD = "secret";
  static final String EMAIL = "please@email.me";
  static final String NEW_USERNAME = "new user";
  static final String NEW_PASSWORD = "new password";
  static final String NEW_EMAIL = "new@email.com";
  @Autowired MockMvc mvc;
  @Autowired UserRepository userRepository;
  @Autowired PasswordEncoder encoder;
  private User user;
  private UserDto dto;

  @BeforeEach
  void init() {
    user = new User(USERNAME, encoder.encode(PASSWORD), EMAIL);
    dto = new UserDto(USERNAME, PASSWORD, EMAIL);

    userRepository.save(user);
  }

  @AfterEach
  void tearDown() {
    userRepository.deleteAll();
  }

  @Test
  @WithMockUser(
      username = USERNAME,
      password = PASSWORD,
      roles = {"USER"})
  void getUserShouldReturnUserDtoWith200StatusWhenUserWithCorrectUsernameAndRoleMakesRequest()
      throws Exception {
    //when
    mvc.perform(
            get("/api/v1/user/{username}", USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        //verify
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value(USERNAME))
        .andExpect(jsonPath("$.password").doesNotExist())
        .andExpect(jsonPath("$.email").value(EMAIL));
  }

  @Test
  @WithMockUser(
      username = "fails",
      password = PASSWORD,
      roles = {"USER"})
  void getUserShouldReturn403WhenUserWithDifferentUsernameMakesRequest() throws Exception {
    //when
    mvc.perform(
            get("/api/v1/user/{username}", USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        //verify
        .andExpect(status().isForbidden());
  }

  @Test
  void createUserShouldReturnNewlyCreatedUserDtoWith201Status() throws Exception {
    //given
    String newUserJson = """
            {
              "username" : "%s",
              "password" : "%s",
              "email" : "%s"
            }
            """.formatted(NEW_USERNAME, NEW_PASSWORD, NEW_EMAIL);

    //when
    mvc.perform(
            post("/api/v1/user/register")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(newUserJson)
    )
            //verify
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value(NEW_USERNAME))
            .andExpect(jsonPath("$.password").doesNotExist())
            .andExpect(jsonPath("$.email").value(NEW_EMAIL));

    assertTrue(userRepository.existsByUsername(NEW_USERNAME));
    User newUser = userRepository.findByUsername(NEW_USERNAME).get();
    assertAll("saved User matches passed data - but password is encoded",
            () -> assertEquals(NEW_USERNAME, newUser.getUsername()),
            () -> assertEquals(NEW_EMAIL, newUser.getEmail()),
            () -> assertNotEquals(NEW_PASSWORD, newUser.getPassword()));
  }

  @Test
  void createUserShouldReturnBadRequestWithErrorMessageWhenUsernameAlreadyExists() throws Exception {
    //given
    String newUserJson = """
            {
              "username" : "%s",
              "password" : "%s",
              "email" : "%s"
            }
            """.formatted(USERNAME, NEW_PASSWORD, NEW_EMAIL);

    //when
    mvc.perform(
            post("/api/v1/user/register")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(newUserJson)
    )
            //verify
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").isNotEmpty());

  }

  @Test
  void createUserShouldReturn401WithErrorMessageWhenUserIsNotValid() throws Exception {
    //given
    String invalidUserJson = """
            {
              "username" : "",
              "password" : "",
              "email" : "not.an.email"
            }
            """;

    //when
    mvc.perform(
            post("/api/v1/user/register")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidUserJson)
    )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").isNotEmpty())
            .andExpect(jsonPath("$.error.username").isNotEmpty())
            .andExpect(jsonPath("$.error.password").isNotEmpty())
            .andExpect(jsonPath("$.error.email").value("email not valid"));
  }

  @Test
  @WithMockUser(username = USERNAME, password = PASSWORD)
  void changeUsernameShouldReturnUpdatedUserWith200StatusWhenNewUsernameIsUnique() throws Exception {
    //when
    mvc.perform(
            patch("/api/v1/user/{username}/username", USERNAME)
            .contentType(MediaType.APPLICATION_JSON)
            .content(NEW_USERNAME)
            .accept(MediaType.APPLICATION_JSON)
    )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value(NEW_USERNAME))
            .andExpect(jsonPath("$.password").doesNotExist())
            .andExpect(jsonPath("$.email").value(EMAIL));
  }

  @Test
  @WithMockUser(username = USERNAME, password = PASSWORD)
  void changeUsernameShouldReturnErrorMessageWIth401StatusWhenUsernameAlreadyExists() throws Exception {
    //when
    mvc.perform(
            patch("/api/v1/user/{username}/username", USERNAME)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(USERNAME)
                    .accept(MediaType.APPLICATION_JSON)
    )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").isNotEmpty());
  }

  @Test
  @WithMockUser(username = "fails", password = PASSWORD)
  void changeUsernameShouldReturnForbiddenWhenUserDoesNotHaveAccess() throws Exception {
    //when
    mvc.perform(
            patch("/api/v1/user/{username}/username", USERNAME)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(USERNAME)
                    .accept(MediaType.APPLICATION_JSON)
    )
            .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = USERNAME, password = PASSWORD)
  void changePasswordShouldReturn200WhenUserHasAccess() throws Exception {
    //when
    mvc.perform(
            patch("/api/v1/user/{username}/password", USERNAME)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(NEW_PASSWORD)
                    .accept(MediaType.APPLICATION_JSON)
    )
            //verify
            .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "fails", password = PASSWORD)
  void changePasswordShouldReturnForbiddenWhenUserDoesNotHaveAccess() throws Exception {
    //when
    mvc.perform(
            patch("/api/v1/user/{username}/password", USERNAME)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(NEW_PASSWORD)
                    .accept(MediaType.APPLICATION_JSON)
    )
            //verify
            .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = USERNAME, password = PASSWORD)
  void changeEmailShouldReturnUpdatedUSerWith200StatusWhenUserHasAccess() throws Exception {
    //when
    mvc.perform(
            patch("/api/v1/user/{username}/email", USERNAME)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(NEW_EMAIL)
                    .accept(MediaType.APPLICATION_JSON)
    )
            //verify
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value(USERNAME))
            .andExpect(jsonPath("$.password").doesNotExist())
            .andExpect(jsonPath("$.email").value(NEW_EMAIL));

  }

  @Test
  @WithMockUser(username = "fails", password = PASSWORD)
  void changeEmailShouldReturnForbiddenWhenUserDoesNotHaveAccess() throws Exception {
    //when
    mvc.perform(
            patch("/api/v1/user/{username}/email", USERNAME)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(NEW_EMAIL)
                    .accept(MediaType.APPLICATION_JSON)
    )
            //verify
            .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = USERNAME, password = PASSWORD)
  void changeEmailShouldReturnErrorMessageWith402StatusWhenEmailIsIncorrectlyFormatted() throws Exception {
    //given
    String badEmail = "thisisnotanemail";

    //when
    mvc.perform(
            patch("/api/v1/user/{username}/email", USERNAME)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(badEmail)
                    .accept(MediaType.APPLICATION_JSON)
    )
            //verify
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").isNotEmpty());
  }

  @Test
  void deleteUser() {}
}
