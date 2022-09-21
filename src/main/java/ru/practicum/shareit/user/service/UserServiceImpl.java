package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.NoEmailException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User add(User user) throws ValidationException, DuplicateEmailException, NoEmailException,
            InvalidEmailException {
        return userStorage.add(user);
    }

    public User update(User user, Long id) throws ValidationException, NoEmailException, DuplicateEmailException,
            InvalidEmailException {
        return userStorage.update(user, id);
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(Long id) throws ValidationException {
        return userStorage.findById(id);
    }

    public void delete(Long id) throws ValidationException {
        userStorage.delete(id);
    }
}
