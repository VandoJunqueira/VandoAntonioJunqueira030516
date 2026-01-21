package com.example.musicapi.model;

public class UserBuilder {

    private Long id;
    private String username;
    private String password;
    private User.Role role;

    public UserBuilder() {
    }

    public UserBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public UserBuilder username(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder password(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder role(User.Role role) {
        this.role = role;
        return this;
    }

    public User build() {
        return new User(id, username, password, role);
    }
}
