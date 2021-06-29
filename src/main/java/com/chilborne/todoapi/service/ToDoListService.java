package com.chilborne.todoapi.service;

import com.chilborne.todoapi.exception.TaskNotFoundException;
import com.chilborne.todoapi.exception.ToDoListNotFoundException;
import com.chilborne.todoapi.web.dto.SingleValueDTO;
import com.chilborne.todoapi.persistance.model.Task;
import com.chilborne.todoapi.persistance.model.ToDoList;
import com.chilborne.todoapi.persistance.repository.ToDoListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToDoListService {

    private final ToDoListRepository repository;

    private final Logger logger = LoggerFactory.getLogger(ToDoListService.class);

    public ToDoListService(ToDoListRepository repository) {
        this.repository = repository;
    }

    public ToDoList saveToDoList(ToDoList list) {
        logger.info("Saving ToDoList (name: " + list.getName() + ")");
        return repository.save(list);
    }

    public ToDoList getToDoListById(Long id) throws ToDoListNotFoundException {
        logger.info("Fetching ToDoList with id: " + " id");
        return repository.findById(id).orElseThrow(
                () -> new ToDoListNotFoundException("ToDoList with id " + id + " not found")
        );
    }

    public List<ToDoList> getAllToDoList() {
        logger.info("Fetching all ToDoLists");
        return repository.findAll();
    }

    public void deleteToDoList(Long id) {
        logger.info("Deleting ToDoList (id: " + id + ")");
        repository.deleteById(id);
    }

    public ToDoList setName(Long id, String name) throws ToDoListNotFoundException {
        logger.info(
            String.format("Setting name of ToDoList (id: %d) to: %s", id, name)
        );
        ToDoList list = getToDoListById(id);
        list.setName(name);
        return repository.save(list);
    }

    public ToDoList setDescription(Long id, SingleValueDTO<String> description) throws ToDoListNotFoundException {
        logger.info(
                String.format("Adding Description (hashcode: %s) to ToDoList (id: %d)",
                        description.hashCode(), id)
        );
        ToDoList list = getToDoListById(id);
        list.setDescription(description.getValue());
        return repository.save(list);
    }

    public ToDoList setActive(Long id, SingleValueDTO<Boolean> active) throws ToDoListNotFoundException {
        logger.info(
                String.format("Setting ToDoList (id: %d) Active to: %b", id, active)
        );
        ToDoList list = getToDoListById(id);
        list.setActive(active.getValue());
        return repository.save(list);
    }

    public ToDoList addTask(Long listId, Task task) throws ToDoListNotFoundException {
        logger.info(
                String.format("Adding Task (name: %s) to ToDoList (id: %d)", task.getName(), listId)
        );
        ToDoList list = getToDoListById(listId);
        task.setList(list);
        list.addTask(task);
        return repository.save(list);
    }

    public ToDoList removeTask(Long listId, Long taskId) throws TaskNotFoundException {
        logger.info(
                String.format("Removing Task (id: %d) from ToDoList (id; %d)", taskId, listId)
        );
        ToDoList list = getToDoListById(listId);
        if (list.removeTask(taskId)) {
            return repository.save(list);
        } else
            throw new TaskNotFoundException(
                    String.format("List with id %d does not contain task with id %d", listId, taskId));
    }
}
