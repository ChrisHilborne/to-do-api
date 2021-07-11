package com.chilborne.todoapi.persistance.mapper;

import com.chilborne.todoapi.persistance.dto.ToDoListDto;
import com.chilborne.todoapi.persistance.model.ToDoList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ToDoListMapper {

    ToDoListMapper INSTANCE = Mappers.getMapper(ToDoListMapper.class);

    @Mapping(source = "id", target = "listId")
    @Mapping(source = "timeCreated", target = "dateTimeMade")
    ToDoListDto convertToDoListToDto(ToDoList list);

    @Mapping(source = "listId", target = "id")
    @Mapping(source = "dateTimeMade", target = "timeCreated")
    ToDoList convertDtoToToDoList(ToDoListDto dto);
}
