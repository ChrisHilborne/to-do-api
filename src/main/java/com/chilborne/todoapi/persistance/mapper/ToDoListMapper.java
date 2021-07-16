package com.chilborne.todoapi.persistance.mapper;

import com.chilborne.todoapi.persistance.dto.ToDoListDto;
import com.chilborne.todoapi.persistance.model.ToDoList;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Objects;

import static com.chilborne.todoapi.web.controller.v1.ToDoListController.TO_DO_LIST_ROOT_URL;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ToDoListMapper {

    public static ToDoListMapper INSTANCE = Mappers.getMapper(ToDoListMapper.class);

    @BeforeMapping
    protected void addUrlToListDot(@MappingTarget ToDoListDto dto, ToDoList list) {
        dto.setUrl(TO_DO_LIST_ROOT_URL + "/" + list.getId());
    }

    @Mapping(source = "id", target = "listId")
    @Mapping(source = "timeCreated", target = "dateTimeMade")
    public abstract ToDoListDto convertToDoList(@NotNull ToDoList list);

    @Mapping(source = "listId", target = "id")
    @Mapping(source = "dateTimeMade", target = "timeCreated")
    public abstract ToDoList convertListDto(@NotNull ToDoListDto dto);

    /**
     * Utility method for testing equality between ToDoList and ToDoListDto
     *
     * @param list
     * @param dto
     * @return boolean
     */
    public boolean compare(ToDoList list, ToDoListDto dto) {
        if (dto.getUrl() != null && dto.getUrl().endsWith(String.valueOf(list.getId()))) return false;
        if (list.isActive() != dto.isActive()) return false;
        if (!list.getName().equals(dto.getName())) return false;
        if ((list.getTimeCreated() != null && dto.getDateTimeMade() != null)
                && !list.getTimeCreated().equals(dto.getDateTimeMade())) return false;
        return list.getTasks().size() == dto.getTasks().size();
        //TODO --> move compare methods to dto
    }
}
