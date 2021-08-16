package com.chilborne.todoapi.web.controller.v1;

import com.chilborne.todoapi.persistance.model.User;
import com.chilborne.todoapi.persistance.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class AuthorizationIT {

  private static final String USERNAME = "a_user's_name";
  private static final String PASSWORD = "a_user's_name's_password";
  @Autowired MockMvc mvc;
  @Autowired UserRepository userRepository;
  @Autowired
  PasswordEncoder passwordEncoder;
  private String jSessionId;

  @BeforeEach
  void initialiseData() {
    User user = new User(USERNAME, passwordEncoder.encode(PASSWORD));
    userRepository.save(user);
  }

  @AfterEach
  void tearDownData() {
    userRepository.deleteAll();
  }

  @Test
  void httpBasicAuthenticationWithValidCredentialsShouldReturn200() throws Exception {
    // when
    mvc.perform(get("/api/v1/user/{username}", USERNAME).with(httpBasic(USERNAME, PASSWORD)))
        // verify
        .andExpect(status().isOk());
  }

  @Test
  void httpBasicAuthenticationWithInvalidCredentialsShouldReturnUnauthorized() throws Exception {
    // when
    mvc.perform(get("/api/v1/user/{username}", USERNAME).with(httpBasic("fails", PASSWORD)))
            // verify
            .andExpect(status().isUnauthorized());
  }
}
