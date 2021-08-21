package com.chilborne.todoapi.persistance.mapper;

import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.dto.TaskDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Objects;

import static com.chilborne.todoapi.web.controller.v1.TaskController.TASK_ROOT_URL;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TaskMapper {

    public static TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @BeforeMapping
    void addUrlAndListIdToTaskDto(@MappingTarget TaskDto dto, Task task) {
        dto.setUrl(TASK_ROOT_URL + "/" + task.getId());
        dto.setListId(
                task.getToDoList() != null ? task.getToDoList().getId() : 0);
    }

    @Mapping(source = "id", target = "taskId")
    @Mapping(source = "timeCreated", target = "dateTimeMade")
    @Mapping(source = "timeCompleted", target = "dateTimeFinished")
    public abstract TaskDto convertTask(Task task);

    @Mapping(source = "taskId", target = "id")
    @Mapping(source = "dateTimeMade", target = "timeCreated")
    @Mapping(source = "dateTimeFinished", target = "timeCompleted")
    public abstract Task convertTaskDto(TaskDto taskDto);


}
