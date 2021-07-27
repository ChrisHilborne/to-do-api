package com.chilborne.todoapi.web.controller.v1;

import com.chilborne.todoapi.persistance.dto.UserDto;
import com.chilborne.todoapi.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    UserService service;

    @Captor
    ArgumentCaptor<UserDto> userCaptor;

    UserDto user;
    static final String USERNAME = "username";
    static final String PASSWORD = "passw0rd";
    static final String EMAIL = "please@mail.me";

    @BeforeEach
    void setUp() {
        user = new UserDto(USERNAME, PASSWORD, EMAIL);
    }

    @Test
    @WithMockUser
    void getUserShouldReturnUserDtoWithoutPassword() throws Exception {
        //given
        given(service.getUserByUsername(USERNAME)).willReturn(user);

        //when
        mvc.perform(
                get("/api/v1/user/{username}", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                //verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(service).getUserByUsername(USERNAME);
        verifyNoMoreInteractions(service);
    }

    @Test
    void createUserShouldReturnNewUserDtoWith201Status() throws Exception {
        //given
        String userJson = """
                {
                    "username" : "%s",
                    "password" : "%s",
                    "email" : "%s"
                }
                """.formatted(USERNAME, PASSWORD, EMAIL);

        given(service.createUser(any(UserDto.class))).willReturn(user);

        //when
        mvc.perform(
                post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(userJson)
        )
                //verify
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.email").value(EMAIL));
        verify(service).createUser(userCaptor.capture());
        verifyNoMoreInteractions(service);

        assertEquals(user, userCaptor.getValue());

    }

    @Test
    @WithMockUser
    void changeUsernameShouldReturnUpdatedUserWith200() throws Exception {
        //given
        String newName = "new username";
        user.setUsername(newName);

        given(service.changeUsername(USERNAME, newName)).willReturn(user);

        //when
        mvc.perform(
                patch("/api/v1/user/{username}/username", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(newName)
        )
                //verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(newName))
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(service).changeUsername(USERNAME, newName);
        verifyNoMoreInteractions(service);
    }

    @Test
    @WithMockUser
    void changePasswordShouldReturn200() throws Exception {
        //given
        String newPassword = "new passw0rd";
        user.setPassword(newPassword);

        //when
        mvc.perform(
                patch("/api/v1/user/{username}/password", USERNAME)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newPassword)
        )
                //verify
                .andExpect(status().isOk());

        verify(service).changePassword(USERNAME, newPassword);
        verifyNoMoreInteractions(service);
    }

    @Test
    @WithMockUser
    void changeEmailShouldReturnUpdatedUserWith200() throws Exception {
        //given
        String newEmail = "electronic@mail.me";
        user.setEmail(newEmail);

        given(service.changeEmail(USERNAME, newEmail)).willReturn(user);

        //when
        mvc.perform(
                patch("/api/v1/user/{username}/email", USERNAME)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(newEmail)
        )
                //verify
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(USERNAME))
                .andExpect(jsonPath("$.email").value(newEmail))
                .andExpect(jsonPath("$.password").doesNotExist());
        verify(service).changeEmail(USERNAME, newEmail);
        verifyNoMoreInteractions(service);
    }

    @Test
    @WithMockUser
    void deleteUserShouldReturn203() throws Exception {
        //when
        mvc.perform(
                delete("/api/v1/user/{username}", USERNAME)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        )
                //verify
                .andExpect(status().isNoContent());
        verify(service).deleteUser(USERNAME);
        verifyNoMoreInteractions(service);
    }
}