package com.chilborne.todoapi.security.access;

import com.chilborne.todoapi.persistance.model.Task;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class TaskAccessManager implements AccessManager<Task> {

  private final UsernameAccessManager usernameAccessManager;

  public TaskAccessManager(UsernameAccessManager usernameAccessManager) {
    this.usernameAccessManager = usernameAccessManager;
  }

  @Override
  public void checkAccess(Task task) throws AccessDeniedException {
    String taskOwnerUsername = task.getToDoList().getUser().getUsername();
    usernameAccessManager.checkAccess(taskOwnerUsername);
  }
}
