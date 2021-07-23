
package com.chilborne.todoapi.persistance.mapper;

import com.chilborne.todoapi.persistance.dto.ToDoListDto;
import com.chilborne.todoapi.persistance.dto.UserDto;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.persistance.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {

    public static UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    public UserDto convertUser(User user) {
        UserDto dto = new UserDto();
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setEmail(user.getEmail());
        dto.setToDoLists(user.getToDoLists()
                .stream()
                .map(this::convertToDoList)
                .collect(Collectors.toList()));
        return dto;
    }

    public User convertUserDto(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setToDoLists(dto.getToDoLists()
                .stream()
                .map(this::convertToDoListDto)
                .collect(Collectors.toList()));
        return user;
    }

    /**
     * Utility method to test equality between mapped objects.
     *
     * @param user User
     * @param dto UserDto
     * @return boolean
     */
    public boolean compare(User user, UserDto dto) {
        if (!user.getUsername().equals(dto.getUsername())) return false;
        if (!user.getPassword().equals(dto.getPassword())) return false;
        if (!user.getEmail().equals(dto.getEmail())) return false;
        return user.getToDoLists().size() == dto.getToDoLists().size();
    }

    public abstract ToDoListDto convertToDoList(ToDoList toDoList);

    public abstract ToDoList convertToDoListDto(ToDoListDto toDoListDto);


}
