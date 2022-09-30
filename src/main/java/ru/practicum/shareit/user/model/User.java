package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column
    private Long id;
    @Column
    private String name;
    @Email
    @Column
    private String email;

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public User() {
    }
}
