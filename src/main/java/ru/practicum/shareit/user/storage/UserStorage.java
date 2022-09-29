package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {

    User add(User user);

    User update(User user, Long id);

    List<User> findAll();

    User findById(Long id);

    void delete(Long id);

    Map<Long, User> getUsers();
}
