package com.chilborne.todoapi.persistance.mapper;

import com.chilborne.todoapi.persistance.dto.ToDoListDto;
import com.chilborne.todoapi.persistance.model.ToDoList;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;


import static com.chilborne.todoapi.web.controller.v1.ToDoListController.TO_DO_LIST_ROOT_URL;

@Mapper(componentModel = "spring", uses = TaskMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ToDoListMapper {

    public static ToDoListMapper INSTANCE = Mappers.getMapper(ToDoListMapper.class);

    @BeforeMapping
    protected void addUrlToListDto(@MappingTarget ToDoListDto dto, ToDoList list) {
        dto.setUsername(list.getUser() != null ? list.getUser().getUsername() : null);
        dto.setUrl(TO_DO_LIST_ROOT_URL + "/" + list.getId());
    }

    @Mapping(source = "id", target = "listId")
    @Mapping(source = "timeCreated", target = "dateTimeMade")
    public abstract ToDoListDto convertToDoList(@NotNull ToDoList list);

    @Mapping(source = "listId", target = "id")
    @Mapping(source = "dateTimeMade", target = "timeCreated")
    public abstract ToDoList convertListDto(@NotNull ToDoListDto dto);


}
