package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class User {
    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    private Long id;
    private String name;
    @Email
    private String email;

    public User() {
    }
}
