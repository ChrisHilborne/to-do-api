package com.chilborne.todoapi.service;

import com.chilborne.todoapi.persistance.dto.UserDto;
import com.chilborne.todoapi.persistance.mapper.UserMapper;
import com.chilborne.todoapi.persistance.model.User;
import com.chilborne.todoapi.persistance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository repository;

    @Mock
    UserMapper mockMapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl service;

    User user;
    UserDto dto;

    static final String USERNAME = "USER";
    static final String PASSWORD = "SECRET";
    static final String ENCODED_PASSWORD = "ESRETC";
    static final String EMAIL = "send@me.mail";

    @BeforeEach
    void init() {
        user = new User();
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        user.setEmail(EMAIL);

        dto = new UserDto();
        dto.setUsername(USERNAME);
        dto.setPassword(PASSWORD);
        dto.setEmail(EMAIL);
    }

    @Test
    void getUserDtoByUserName() {
        //given
        given(repository.findByUsername(USERNAME)).willReturn(Optional.ofNullable(user));
        given(mockMapper.convertUser(user)).willReturn(dto);

        //when
        UserDto result = service.getUserDtoByUserName(USERNAME);

        //verify
        verify(repository).findByUsername(USERNAME);
        verifyNoMoreInteractions(repository);
        verify(mockMapper).convertUser(user);
        verifyNoMoreInteractions(mockMapper);

        assertEquals(dto, result);
    }

    @Test
    void createUser() {
        //given
        given(mockMapper.convertUserDto(dto)).willReturn(user);
        given(passwordEncoder.encode(PASSWORD)).willReturn(ENCODED_PASSWORD);
        given(repository.save(any(User.class))).willReturn(user);

    }

    @Test
    void updateUser() {
    }

    @Test
    void changePassword() {
    }

    @Test
    void userExists() {
    }

    @Test
    void loadUserByUsername() {
    }
}