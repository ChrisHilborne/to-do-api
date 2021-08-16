package com.chilborne.todoapi.security.access;

import com.chilborne.todoapi.persistance.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserAccessManager implements AccessManager<User> {

  private final UsernameAccessManager usernameAccessManager;

  public UserAccessManager(UsernameAccessManager usernameAccessManager) {
    this.usernameAccessManager = usernameAccessManager;
  }

  @Override
  public void checkAccess(User user) {
    String username = user.getUsername();
    usernameAccessManager.checkAccess(username);
  }

  public void checkAccess(String username) {
    usernameAccessManager.checkAccess(username);
  }

}
