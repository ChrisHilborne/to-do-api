package com.chilborne.todoapi.persistance.mapper;

import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.dto.TaskDto;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @Mapping(source = "id", target = "taskId")
    @Mapping(source = "timeCreated", target = "dateTimeMade")
    @Mapping(source = "timeCompleted", target = "dateTimeFinished")
    TaskDto convertTask(Task task);

    @Mapping(source = "dateTimeMade", target = "timeCreated")
    @Mapping(source = "dateTimeFinished", target = "timeCompleted")
    Task convertTaskDto(TaskDto taskDto);

    /**
     * utility method for testing equality between Task and TaskDto
     *
     * @param task
     * @param dto
     * @return boolean
     */
    default boolean compare(Task task, TaskDto dto) {
        if (task.isActive() != task.isActive()) return false;
        if (!Objects.equals(task.getName(), dto.getName())) return false;
        if (!Objects.equals(task.getDescription(), dto.getDescription())) return false;
        //timeCreated is instantiated when bean is saved to DB for the first time - some unit tests don't use the DB
        if ((task.getTimeCompleted() != null && dto.getDateTimeMade() != null) && !Objects.equals(task.getTimeCreated(), dto.getDateTimeMade())) return false;
        return Objects.equals(task.getTimeCompleted(), dto.getDateTimeFinished());
    }


}
