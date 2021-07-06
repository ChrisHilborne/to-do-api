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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ToDoListServiceImpl implements ToDoListService {

    private final ToDoListRepository repository;
    private final Logger logger = LoggerFactory.getLogger(ToDoListServiceImpl.class);

    public ToDoListServiceImpl(ToDoListRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ToDoList saveToDoList(ToDoList list) {
        logger.info("Saving ToDoList (name: " + list.getName() + ")");
        return repository.save(list);
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public ToDoList getToDoListById(long id) throws ToDoListNotFoundException {
        logger.info("Fetching ToDoList with id: " + id);
        return repository.findById(id).orElseThrow(
                () -> new ToDoListNotFoundException(id)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ToDoList> getAllToDoList() {
        logger.info("Fetching all ToDoLists");
        return repository.findAll();
    }

    @Override
    @Transactional
    public void deleteToDoList(long id) {
        logger.info("Deleting ToDoList (id: " + id + ")");
        repository.deleteById(id);
    }

    @Override
    public ToDoList updateToDoList(long id, ToDoList toDoList) {
        if (!repository.existsById(id)) throw new ToDoListNotFoundException(id);
        toDoList.setId(id);
        return saveToDoList(toDoList);
    }


    @Override
    public ToDoList setToDoListActive(long id, boolean active) throws ToDoListNotFoundException {
        logger.info(
                String.format("Setting ToDoList (id: %d) Active to: %b", id, active)
        );
        ToDoList toUpdate = getToDoListById(id);
        toUpdate.setActive(active);
        return saveToDoList(toUpdate);
    }

    @Override
    public ToDoList addTaskToDoList(long listId, Task task) throws ToDoListNotFoundException {
        logger.info(
                String.format("Adding Task (name: %s) to ToDoList (id: %d)", task.getName(), listId)
        );
        ToDoList toUpdate = getToDoListById(listId);
        task.setToDoList(toUpdate);
        toUpdate.addTask(task);
        return saveToDoList(toUpdate);
    }

    @Override
    public ToDoList removeTaskToDoList(long listId, long taskId) throws TaskNotFoundException {
        logger.info(
                String.format("Removing Task (id: %d) from ToDoList (id; %d)", taskId, listId)
        );
        ToDoList toUpdate = getToDoListById(listId);
        if (toUpdate.removeTask(taskId)) {
            return saveToDoList(toUpdate);
        } else
            throw new TaskNotFoundException(
                    String.format("List with id %d does not contain task with id %d", listId, taskId));
    }
}
