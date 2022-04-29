package com.chilborne.todoapi.security.access;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UsernameAccessManager implements AccessManager<String> {

  public void checkAccess(String username) {

    try {
      UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

      if (userDetails.getUsername().equals(username)) {
        throw new AccessDeniedException("User: " + username + " does not have access");
      }
    } catch (ClassCastException e) {
      throw new AccessDeniedException("User does not have access");
    }
  }
}
