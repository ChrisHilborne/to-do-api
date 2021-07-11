package com.chilborne.todoapi.persistance.mapper;

import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.dto.TaskDto;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @Mapping(source = "id", target = "taskId")
    @Mapping(source = "timeCreated", target = "dateTimeMade")
    @Mapping(source = "timeCompleted", target = "dateTimeFinished")
    TaskDto convertTask(@NotNull Task task);

    @Mapping(source = "taskId", target = "id")
    @Mapping(source = "dateTimeMade", target = "timeCreated")
    @Mapping(source = "dateTimeFinished", target = "timeCompleted")
    Task convertTaskDto(@NotNull TaskDto taskDto);


}
