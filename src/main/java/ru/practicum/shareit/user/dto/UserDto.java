package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class UserDto {
    final Long id;
    final String name;
    @Email
    final String email;
}
