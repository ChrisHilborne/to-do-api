package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.UsernameAlreadyExistsException;
import com.chilborne.todoapi.persistance.dto.UserDto;
import com.chilborne.todoapi.persistance.mapper.UserMapper;
import com.chilborne.todoapi.persistance.model.User;
import com.chilborne.todoapi.persistance.repository.UserRepository;
import com.chilborne.todoapi.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository repository;

    @Mock
    UserMapper mapper;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl service;

    @Captor
    ArgumentCaptor<User> userCaptor;

    User user;
    UserDto dto;

    static final String USERNAME = "USER";
    static final String PASSWORD = "SECRET";
    static final String ENCODED_PASSWORD = "ESRETC";
    static final String EMAIL = "send@me.mail";

    @BeforeEach
    void init() {
        user = new User(USERNAME, PASSWORD, EMAIL);
        user.setUserId(UUID.randomUUID());

        dto = new UserDto(USERNAME, PASSWORD, EMAIL);
    }

    @Test
    void getUserDtoByUserNameShouldReturnUserDtoWhenUserExists() {
        //given
        given(repository.findByUsername(USERNAME)).willReturn(Optional.ofNullable(user));
        given(mapper.convertUser(user)).willReturn(dto);

        //when
        UserDto result = service.getUserByUsername(USERNAME);

        //verify
        verify(repository).findByUsername(USERNAME);
        verifyNoMoreInteractions(repository);
        verify(mapper).convertUser(user);
        verifyNoMoreInteractions(mapper);

        assertEquals(dto, result);
    }

    @Test
    void getUserDtoByUserNameShouldThrowExceptionWhenUserDoesNotExist() {
        //given
        given(repository.findByUsername(USERNAME)).willReturn(Optional.empty());

        //verify
        assertThrows(UsernameNotFoundException.class,
                () -> service.getUserByUsername(USERNAME));
    }

    @Test
    void createUserShouldEncodePasswordAndReturnCreatedUserDto() {
        //given
        User encodedPassword = new User(USERNAME, ENCODED_PASSWORD, EMAIL);
        UserDto encodedDto = new UserDto(USERNAME, ENCODED_PASSWORD, EMAIL);

        given(repository.existsByUsername(USERNAME)).willReturn(false);
        given(mapper.convertUserDto(dto)).willReturn(user);
        given(passwordEncoder.encode(PASSWORD)).willReturn(ENCODED_PASSWORD);
        given(repository.save(any(User.class))).willReturn(encodedPassword);
        given(mapper.convertUser(encodedPassword)).willReturn(encodedDto);

        //when
        UserDto returned = service.createUser(dto);

        //verify
        verify(mapper).convertUserDto(dto);
        verify(mapper).convertUser(encodedPassword);
        verifyNoMoreInteractions(mapper);

        verify(passwordEncoder).encode(PASSWORD);
        verifyNoMoreInteractions(passwordEncoder);

        verify(repository).existsByUsername(USERNAME);
        verify(repository).save(userCaptor.capture());
        verifyNoMoreInteractions(repository);

        User savedUser = userCaptor.getValue();
        assertEquals(ENCODED_PASSWORD, savedUser.getPassword());

        assertEquals(encodedDto, returned);
    }

    @Test
    void createUserShouldThrowExceptionIsUsernameInNotUnique() {
        //given
        given(repository.existsByUsername(USERNAME)).willReturn(true);

        //verify
        assertThrows(UsernameAlreadyExistsException.class,
                () -> service.createUser(dto));
    }

    @Test
    void changeUsernameShouldReturnUpdatedUserDto() {
        //given
        final String NEW_USERNAME = "NEW USERNAME";
        User updatedUser = new User(NEW_USERNAME, PASSWORD, EMAIL);
        UserDto updatedDto = new UserDto(NEW_USERNAME, PASSWORD, EMAIL);

        given(repository.existsByUsername(NEW_USERNAME)).willReturn(false);
        given(repository.findByUsername(USERNAME)).willReturn(Optional.ofNullable(user));
        given(repository.save(any(User.class))).willReturn(updatedUser);
        given(mapper.convertUser(updatedUser)).willReturn(updatedDto);

        //when
        UserDto returned = service.changeUsername(dto, NEW_USERNAME);


        //verify
        verify(repository).existsByUsername(NEW_USERNAME);
        verify(repository).findByUsername(USERNAME);
        verify(repository).save(userCaptor.capture());
        verifyNoMoreInteractions(repository);

        User savedUser = userCaptor.getValue();
        assertEquals(NEW_USERNAME, savedUser.getUsername());

        verify(mapper).convertUser(updatedUser);
        verifyNoMoreInteractions(mapper);

        assertEquals(updatedDto, returned);
    }

    @Test
    void changeUsernameShouldThrowExceptionWhenNewUsernameIsNotUnique() {
        //given
        given(repository.existsByUsername(USERNAME)).willReturn(true);

        //verify
        assertThrows(UsernameAlreadyExistsException.class,
                () -> service.changeUsername(dto, USERNAME));
    }

    @Test
    void changeEmailShouldReturnUpdatedUserDto() {
        //given
        final String NEW_EMAIL = "new@e.mail";
        User updatedUser = new User(USERNAME, PASSWORD, NEW_EMAIL);
        UserDto updatedDto = new UserDto(USERNAME, PASSWORD, NEW_EMAIL);

        given(repository.findByUsername(USERNAME)).willReturn(Optional.ofNullable(user));
        given(repository.save(any(User.class))).willReturn(updatedUser);
        given(mapper.convertUser(updatedUser)).willReturn(updatedDto);

        //when
        UserDto returned = service.changeEmail(dto, NEW_EMAIL);

        //verify
        verify(repository).findByUsername(USERNAME);
        verify(repository).save(userCaptor.capture());
        verifyNoMoreInteractions(repository);

        verify(mapper).convertUser(updatedUser);
        verifyNoMoreInteractions(mapper);

        User savedUser = userCaptor.getValue();
        assertEquals(NEW_EMAIL, savedUser.getEmail());

        assertEquals(updatedDto, returned);
    }

    @Test
    void changePasswordShouldChangeUserPasswordAfterEncodingIt() {
        //given
        final String newPass = "WEAK PASSWORD";
        final String encodedNewPass = "KAEW ADROSPSW";
        User updatedUser = new User(USERNAME, encodedNewPass, EMAIL);

        given(repository.findByUsername(USERNAME)).willReturn(Optional.ofNullable(user));
        given(repository.save(any(User.class))).willReturn(updatedUser);
        given(passwordEncoder.encode(newPass)).willReturn(encodedNewPass);

        //when
        service.changePassword(dto, newPass);

        //verify
        verify(repository).findByUsername(USERNAME);
        verify(repository).save(userCaptor.capture());
        verifyNoMoreInteractions(repository);

        //check saved User has new password encoded
        User passedUser = userCaptor.getValue();
        assertEquals(encodedNewPass, passedUser.getPassword());

        verify(passwordEncoder).encode(newPass);
        verifyNoMoreInteractions(passwordEncoder); 
    }

    @Test
    void checkUsernameIsUniqueShouldNotThrowExceptionWhenUsernameDoesNotExist() {
        //given
        given(repository.existsByUsername(USERNAME)).willReturn(false);

        //when
        service.checkUsernameIsUnique(USERNAME);

        //verify
        verify(repository).existsByUsername(USERNAME);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void checkUsernameIsUniqueShouldThrowExceptionWhenUsernameExists() {
        //given
        given(repository.existsByUsername(USERNAME)).willReturn(true);

        //verify
        assertThrows(UsernameAlreadyExistsException.class,
                () -> service.checkUsernameIsUnique(USERNAME));
    }

    @Test
    void loadUserByUsernameShouldReturnUserPrincipalWhenUserExists() {
        //given
        given(repository.findByUsername(USERNAME)).willReturn(Optional.ofNullable(user));

        //when
        UserDetails returnedDetails = service.loadUserByUsername(USERNAME);

        //verify
        verify(repository).findByUsername(USERNAME);
        verifyNoMoreInteractions(repository);

        assertTrue(returnedDetails instanceof UserPrincipal);
        UserPrincipal userPrincipal = (UserPrincipal) returnedDetails;

        assertEquals(user, userPrincipal.getUser());
    }

    @Test
    void loadUserByUsernameShouldThrowExceptionWhenUserDoesNotExist() {
        //given
        given(repository.findByUsername(USERNAME)).willReturn(Optional.empty());

        //verify
        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername(USERNAME));
    }
}