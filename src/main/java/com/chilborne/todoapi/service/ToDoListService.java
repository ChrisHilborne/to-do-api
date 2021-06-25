package com.chilborne.todoapi.service;

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
        logger.info(
                String.format("Saving ToDoList (name: %s)", list.getName())
        );
        return repository.save(list);
    }

    public ToDoList getToDoListById(Long id) throws RuntimeException {
        logger.info(String.format("Fetching ToDoList with id: %d", id));
        return repository.findById(id).orElseThrow(
                () -> new RuntimeException(String.format("ToDoList with id %d not found", id))
        );
    }

    public List<ToDoList> getAllToDoList() {
        logger.info("Fetching all ToDoLists");
        return repository.findAll();
    }

    public void deleteToDoList(Long id) {
        logger.info(String.format("Deleting ToDoList (id: %d)", id));
        repository.deleteById(id);
    }

    public ToDoList setName(Long id, String name) {
        logger.info(
            String.format("Setting name of ToDoList (id: %d) to: %s", id, name)
        );
        ToDoList list = getToDoListById(id);
        list.setName(name);
        return repository.save(list);
    }

    public ToDoList setDescription(Long id, String description) {
        logger.info(
                String.format("Adding Description (hashcode: %s) to ToDoList (id: %d)", description.hashCode(), id)
        );
        ToDoList list = getToDoListById(id);
        list.setDescription(description);
        return repository.save(list);
    }

    public ToDoList setActive(Long id, boolean active) throws RuntimeException {
        logger.info(
                String.format("Setting ToDoList (id: %d) Active to: %b", id, active)
        );
        ToDoList list = getToDoListById(id);
        list.setActive(active);
        return repository.save(list);
    }

    public ToDoList addTask(Long listId, Task task) throws RuntimeException {
        logger.info(
                String.format("Adding Task (name: %s) to ToDoList (id: %d)", task.getName(), listId)
        );
        ToDoList list = getToDoListById(listId);
        task.setList(list);
        list.addTask(task);
        return repository.save(list);
    }

    public ToDoList removeTask(Long listId, Task task) {
        logger.info(
                String.format("Removing Task (id: %d) from ToDoList (id; %d)", task.getTaskId(), listId)
        );
        ToDoList list = getToDoListById(listId);
        list.removeTask(task);
        return repository.save(list);
    }
}
