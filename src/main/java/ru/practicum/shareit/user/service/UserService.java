package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User add(User user);

    User update(User user, Long id);

    List<User> findAll();

    User findById(Long id);

    void delete(Long id);
}
