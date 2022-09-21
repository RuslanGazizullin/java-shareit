package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.NoEmailException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {

    User add(User user) throws ValidationException, DuplicateEmailException, NoEmailException, InvalidEmailException;

    User update(User user, Long id) throws ValidationException, NoEmailException, DuplicateEmailException, InvalidEmailException;

    List<User> findAll();

    User findById(Long id) throws ValidationException;

    void delete(Long id) throws ValidationException;

    Map<Long, User> getUsers();
}
