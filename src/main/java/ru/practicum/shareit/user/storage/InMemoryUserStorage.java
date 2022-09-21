package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.NoEmailException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.validation.UserValidation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Long, User> users = new HashMap<>();
    private Long id = 1L;

    private final UserValidation userValidation;

    public InMemoryUserStorage(UserValidation userValidation) {
        this.userValidation = userValidation;
    }

    private Long generateId() {
        return id++;
    }

    @Override
    public User add(User user) throws ValidationException, DuplicateEmailException, NoEmailException,
            InvalidEmailException {
        userValidation.userValidation(users, user);
        Long id = generateId();
        user.setId(id);
        users.put(id, user);
        log.info("Пользователь успешно добавлен");
        return user;
    }

    @Override
    public User update(User user, Long id) throws ValidationException, DuplicateEmailException {
        userValidation.idValidation(users, id);
        if (user.getEmail() != null) {
            userValidation.duplicateEmailValidation(users, user);
        }
        User updatedUser = new User();
        updatedUser.setId(id);
        if (user.getName() == null) {
            updatedUser.setName(users.get(id).getName());
        } else {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() == null) {
            updatedUser.setEmail(users.get(id).getEmail());
        } else {
            updatedUser.setEmail(user.getEmail());
        }
        users.remove(id);
        users.put(id, updatedUser);
        log.info("Пользователь успешно обновлён");
        return updatedUser;
    }

    @Override
    public List<User> findAll() {
        log.info("Список пользователей получен");
        return new ArrayList<>(users.values());
    }

    @Override
    public User findById(Long id) throws ValidationException {
        userValidation.idValidation(users, id);
        log.info("Пользователь найден");
        return users.get(id);
    }

    @Override
    public void delete(Long id) throws ValidationException {
        userValidation.idValidation(users, id);
        users.remove(id);
    }

    @Override
    public HashMap<Long, User> getUsers() {
        return users;
    }
}
