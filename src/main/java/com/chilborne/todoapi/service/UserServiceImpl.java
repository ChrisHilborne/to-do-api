package com.chilborne.todoapi.service;

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

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;


    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(
            UserRepository repository,
            PasswordEncoder passwordEncoder,
            UserMapper mapper) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    @Override
    public UserDto getUserDtoByUserName(String username) {
        logger.info("Fetching User with username {}", username);
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username" + username + "does not exist"));
        return mapper.convertUser(user);
    }

    private User getUser(String username) {
        logger.info("Fetching User with username {}", username);
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username" + username + "does not exist"));
    }

    private UserDto saveUser(User user) {
        logger.info("Saving User {}", user.getUsername());
        return mapper.convertUser(
                repository.save(user)
        );
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        logger.info("Creating new user: {}", userDto.getUsername());
        User entity = mapper.convertUserDto(userDto);
        entity.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return saveUser(entity);
    }

    @Override
    public UserDto updateUser(UserDto user) {
        logger.info("Updating user: {} with the following properties {}", user.getUsername(), user.toString());
        User toUpdate = mapper.convertUserDto(user);
        if (!user.getPassword().equals(toUpdate.getPassword())) {
            throw new UnsupportedOperationException("To update password use the /{userid}/password endpoint");
        }
        toUpdate.setUserId(getUser(user.getUsername()).getUserId());
        return saveUser(toUpdate);
    }

    @Override
    public void changePassword(String username, CharSequence newPwd) {
        logger.info("Changing password for User: {}", username);
        User entity = getUser(username);
        entity.setPassword(passwordEncoder.encode(newPwd));
        saveUser(entity);
    }

    @Override
    public boolean userExists(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserPrincipal(repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + "does not exist")));
    }
}
