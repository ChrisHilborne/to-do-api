package com.chilborne.todoapi.persistance.model;

import com.chilborne.todoapi.persistance.validation.OnPersist;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

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

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email")
    private Email email;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name = "to_do_lists")
    private List<ToDoList> toDoLists = new ArrayList<>();

    @Column(name = "authorities")
    private Collection<GrantedAuthority> authorities;

    public User() { }

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

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
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

    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }
}
