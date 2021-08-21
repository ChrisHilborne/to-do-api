
package com.chilborne.todoapi.persistance.mapper;

import com.chilborne.todoapi.persistance.dto.ToDoListDto;
import com.chilborne.todoapi.persistance.dto.UserDto;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.persistance.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = ToDoListMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {

    public static UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    public abstract UserDto convertUser(User user);

    public abstract User convertUserDto(UserDto dto);




}
