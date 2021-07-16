package com.chilborne.todoapi.persistance.mapper;

import com.chilborne.todoapi.persistance.dto.ToDoListDto;
import com.chilborne.todoapi.persistance.model.ToDoList;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ToDoListMapper {

    ToDoListMapper INSTANCE = Mappers.getMapper(ToDoListMapper.class);

    @Mapping(source = "id", target = "listId")
    @Mapping(source = "timeCreated", target = "dateTimeMade")
    ToDoListDto convertToDoList(@NotNull ToDoList list);

    @Mapping(source = "listId", target = "id")
    @Mapping(source = "dateTimeMade", target = "timeCreated")
    ToDoList convertListDto(@NotNull ToDoListDto dto);

    /**
     * Utility method for testing equality between ToDoList and ToDoListDto
     *
     * @param list
     * @param dto
     * @return boolean
     */
    default boolean compare(ToDoList list, ToDoListDto dto) {
        if (list.isActive() != dto.isActive()) return false;
        if (!list.getName().equals(dto.getName())) return false;
        if ((list.getTimeCreated() != null && dto.getDateTimeMade() != null)
                && !list.getTimeCreated().equals(dto.getDateTimeMade())) return false;
        return list.getTasks().size() == dto.getTasks().size();
        //TODO --> move compare methods to dto
    }
}
