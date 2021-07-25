package com.chilborne.todoapi.persistance.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.*;

@Entity
@DynamicUpdate
@Table(name = "users", indexes = {
        @Index(name = "username", columnList = "username")
})
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id", unique = true, insertable = false, nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false, columnDefinition = "VARCHAR")
    private String password;

    @Column(name = "email")
    private String email;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Column(name = "to_do_lists")
    private List<ToDoList> toDoLists = new ArrayList<>();

    public User() { }

    public User(
            String username,
            String password,
            String email)
    {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public void addToDoList(ToDoList toDoList) {
        toDoLists.add(toDoList);
    }

    public boolean removeToDoList(long toDoListId) {
        return toDoLists.removeIf(
                toDoList -> toDoList.getId() == toDoListId);
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<ToDoList> getToDoLists() {
        return toDoLists != null ? List.copyOf(toDoLists) : null;
    }

    public void setToDoLists(List<ToDoList> toDoLists) {
        if (this.toDoLists != toDoLists) {
            this.toDoLists.clear();
            this.toDoLists.addAll(toDoLists);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!Objects.equals(userId, user.userId)) return false;
        if (!username.equals(user.username)) return false;
        if (!password.equals(user.password)) return false;
        if (!Objects.equals(email, user.email)) return false;
        return Objects.equals(toDoLists, user.toDoLists);
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + username.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (toDoLists != null ? toDoLists.hashCode() : 0);
        return result;
    }
}
