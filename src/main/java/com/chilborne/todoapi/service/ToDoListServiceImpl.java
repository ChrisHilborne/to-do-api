package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.TaskNotFoundException;
import com.chilborne.todoapi.exception.ToDoListNotFoundException;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.persistance.repository.ToDoListRepository;
import com.chilborne.todoapi.web.dto.SingleValueDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToDoListServiceImpl implements ToDoListService{

    private final ToDoListRepository repository;

    private final Logger logger = LoggerFactory.getLogger(ToDoListServiceImpl.class);

    public ToDoListServiceImpl(ToDoListRepository repository) {
        this.repository = repository;
    }

    @Override
    public ToDoList save(ToDoList list) {
        logger.info("Saving ToDoList (name: " + list.getName() + ")");
        return repository.save(list);
    }

    @Override
    public ToDoList getById(long id) throws ToDoListNotFoundException {
        logger.info("Fetching ToDoList with id: " + id);
        return repository.findById(id).orElseThrow(
                () -> new ToDoListNotFoundException("ToDoList with id " + id + " not found")
        );
    }

    @Override
    public List<ToDoList> getAll() {
        logger.info("Fetching all ToDoLists");
        return repository.findAll();
    }

    @Override
    public void delete(long id) {
        logger.info("Deleting ToDoList (id: " + id + ")");
        repository.deleteById(id);
    }


    @Override
    public ToDoList setName(long id, SingleValueDTO<String> name) throws ToDoListNotFoundException {
        logger.info(
            String.format("Setting name of ToDoList (id: %d) to: %s", id, name.getValue())
        );
        ToDoList list = getById(id);
        list.setName(name.getValue());
        return repository.save(list);
    }

    @Override
    public ToDoList setDescription(long id, SingleValueDTO<String> description) throws ToDoListNotFoundException {
        logger.info(
                String.format("Adding Description (hashcode: %s) to ToDoList (id: %d)",
                        description.getValue(), id)
        );
        ToDoList list = getById(id);
        list.setDescription(description.getValue());
        return repository.save(list);
    }

    @Override
    public ToDoList setActive(long id, SingleValueDTO<Boolean> active) throws ToDoListNotFoundException {
        logger.info(
                String.format("Setting ToDoList (id: %d) Active to: %b", id, active)
        );
        ToDoList list = getById(id);
        list.setActive(active.getValue());
        return repository.save(list);
    }

    @Override
    public ToDoList addTask(long listId, Task task) throws ToDoListNotFoundException {
        logger.info(
                String.format("Adding Task (name: %s) to ToDoList (id: %d)", task.getName(), listId)
        );
        ToDoList list = getById(listId);
        task.setList(list);
        list.addTask(task);
        return repository.save(list);
    }

    @Override
    public ToDoList removeTask(long listId, long taskId) throws TaskNotFoundException {
        logger.info(
                String.format("Removing Task (id: %d) from ToDoList (id; %d)", taskId, listId)
        );
        ToDoList list = getById(listId);
        if (list.removeTask(taskId)) {
            return repository.save(list);
        } else
            throw new TaskNotFoundException(
                    String.format("List with id %d does not contain task with id %d", listId, taskId));
    }
}
