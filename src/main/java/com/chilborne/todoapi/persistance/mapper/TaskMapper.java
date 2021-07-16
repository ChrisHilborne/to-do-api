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
                task.getToDoList() != null ?
                task.getToDoList().getId() :
                0);
    }

    @Mapping(source = "id", target = "taskId")
    @Mapping(source = "timeCreated", target = "dateTimeMade")
    @Mapping(source = "timeCompleted", target = "dateTimeFinished")
    public abstract TaskDto convertTask(Task task);

    @Mapping(source = "dateTimeMade", target = "timeCreated")
    @Mapping(source = "dateTimeFinished", target = "timeCompleted")
    public abstract Task convertTaskDto(TaskDto taskDto);

    /**
     * utility method for testing equality between Task and TaskDto
     *
     * @param task
     * @param dto
     * @return boolean
     */
    public boolean compare(Task task, TaskDto dto) {
        if (task.isActive() != task.isActive()) return false;
        if (!Objects.equals(task.getName(), dto.getName())) return false;
        if (!Objects.equals(task.getDescription(), dto.getDescription())) return false;
        if (dto.getUrl() != null && !dto.getUrl().endsWith(String.valueOf(task.getId()))) return false;
        if ((task.getToDoList() != null) && (task.getToDoList().getId() != dto.getListId())) return false;
        //timeCreated is instantiated when bean is saved to DB for the first time - some unit tests don't use the DB
        if ((task.getTimeCompleted() != null && dto.getDateTimeMade() != null) && !Objects.equals(task.getTimeCreated(), dto.getDateTimeMade())) return false;
        return Objects.equals(task.getTimeCompleted(), dto.getDateTimeFinished());
    }


}
