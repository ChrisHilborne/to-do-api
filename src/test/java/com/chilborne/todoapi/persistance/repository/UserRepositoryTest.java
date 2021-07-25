package com.chilborne.todoapi.persistance.repository;

import com.chilborne.todoapi.persistance.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    UserRepository repository;

    User user;
    static final String USERNAME = "USER";
    static final String PASSWORD = "PASSWORD";
    static final String EMAIL = "my@mail.com";

    @BeforeEach
    void setup() {
        user = new User(USERNAME, PASSWORD, EMAIL);
        repository.save(user);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void findByUsernameShouldReturnOptionalOfUserWhenUserExists() {
        //when
        Optional<User> returned = repository.findByUsername(USERNAME);

        //verify
        assertTrue(returned.isPresent());
        assertEquals(user, returned.get());
    }

    @Test
    void findByUsernameShouldReturnEmptyOptionalWhenUsernameDoesNotExist() {
        //when
        Optional<User> returned = repository.findByUsername("DOES NOT EXIST");

        //verify
        assertTrue(returned.isEmpty());
    }

    @Test
    void existsByUsernameShouldReturnTrueWhenUsernameExists() {
        //when
        boolean exists = repository.existsByUsername(USERNAME);

        //verify
        assertTrue(exists);
    }

    @Test
    void existsByUsernameShouldReturnFalseWhenUsernameDoesNotExist() {
        //when
        boolean exists = repository.existsByUsername("DOES NOT EXISTS");

        //verify
        assertFalse(exists);
    }

    @Test
    void deleteByUsernameShouldDeleteUser() {
        //when
        repository.deleteByUsername(USERNAME);

        //verify
        Optional<User> check = repository.findByUsername(USERNAME);
        assertTrue(check.isEmpty());
    }
}