package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.TaskNotFoundException;
import com.chilborne.todoapi.exception.ToDoListNotFoundException;
import com.chilborne.todoapi.persistance.dto.TaskDto;
import com.chilborne.todoapi.persistance.dto.ToDoListDto;
import com.chilborne.todoapi.persistance.mapper.TaskMapper;
import com.chilborne.todoapi.persistance.mapper.ToDoListMapper;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.persistance.repository.ToDoListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ToDoListServiceImpl implements ToDoListService {

    private final ToDoListRepository repository;
    private final ToDoListMapper toDoListMapper;
    private final TaskMapper taskMapper;
    private final Logger logger = LoggerFactory.getLogger(ToDoListServiceImpl.class);

    public ToDoListServiceImpl(ToDoListRepository repository,
                               ToDoListMapper toDoListMapper,
                               TaskMapper taskMapper) {
        this.repository = repository;
        this.toDoListMapper = toDoListMapper;
        this.taskMapper = taskMapper;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ToDoListDto saveToDoList(ToDoList list) {
        logger.info("Saving ToDoList (name: " + list.getName() + ")");
        ToDoList saved = repository.save(list);
        return toDoListMapper.convertToDoList(saved);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ToDoListDto saveToDoList(ToDoListDto listDto) {
        logger.info("Saving ToDoList (name: " + listDto.getName() + ")");
        ToDoList toSave = toDoListMapper.convertListDto(listDto);
        ToDoList saved = repository.save(toSave);
        return toDoListMapper.convertToDoList(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ToDoListDto getToDoListDtoById(long id) throws ToDoListNotFoundException {
        logger.info("Fetching ToDoList with id: " + id);
        ToDoList result = repository.findById(id).orElseThrow(
                () -> new ToDoListNotFoundException(id)
        );
        return toDoListMapper.convertToDoList(result);
    }

    /**
     * Private method to return ToDoList object with null check to methods in ToDoListService.
     * Allows us to avoid multiple uses of ToDoListMapper from ToDoList -> Dto, Dto -> ToDoList and then ToDoList -> Dto again.
     * @param id
     * @return
     * @throws ToDoListNotFoundException
     */
    private ToDoList getToDoList(long id) throws ToDoListNotFoundException {
        logger.info("Getting ToDoList with id: " + id);
        return repository.findById(id).orElseThrow(
                () -> new ToDoListNotFoundException(id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ToDoListDto > getAllToDoList() {
        logger.info("Fetching all ToDoLists");
        return repository.findAll()
                .stream()
                .map(toDoListMapper::convertToDoList)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteToDoList(long id) {
        logger.info("Deleting ToDoList (id: " + id + ")");
        repository.deleteById(id);
    }

    @Override
    public ToDoListDto updateToDoList(long id, ToDoListDto listDto) {
        if (!repository.existsById(id)) throw new ToDoListNotFoundException(id);
        ToDoList toUpdate = toDoListMapper.convertListDto(listDto);
        toUpdate.setId(id);
        return saveToDoList(toUpdate);
    }


    @Override
    public ToDoListDto setToDoListActive(long id, boolean active) throws ToDoListNotFoundException {
        logger.info(
                String.format("Setting ToDoList (id: %d) Active to: %b", id, active)
        );
        ToDoList toUpdate = getToDoList(id);
        toUpdate.setActive(active);
        return saveToDoList(toUpdate);
    }

    @Override
    public ToDoListDto addTaskToDoList(long listId, TaskDto taskDto) throws ToDoListNotFoundException {
        logger.info(
                String.format("Adding Task (name: %s) to ToDoList (id: %d)", taskDto.getName(), listId)
        );
        ToDoList toUpdate = getToDoList(listId);
        Task newTask = taskMapper.convertTaskDto(taskDto);
        newTask.setToDoList(toUpdate);
        toUpdate.addTask(newTask);
        return saveToDoList(toUpdate);
    }

    @Override
    public ToDoListDto removeTaskToDoList(long listId, long taskId) throws TaskNotFoundException {
        logger.info(
                String.format("Removing Task (id: %d) from ToDoList (id; %d)", taskId, listId)
        );
        ToDoList toUpdate = getToDoList(listId);
        if (toUpdate.removeTask(taskId)) {
            return saveToDoList(toUpdate);
        } else
            throw new TaskNotFoundException(
                    String.format("List with id %d does not contain task with id %d", listId, taskId));
    }
}
