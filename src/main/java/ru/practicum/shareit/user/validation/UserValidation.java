package ru.practicum.shareit.user.validation;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.NoEmailException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;

@Component
public class UserValidation {

    public void emailValidation(User user) throws InvalidEmailException {
        if (!user.getEmail().contains("@")) {
            throw new InvalidEmailException("Неверный формат email");
        }
    }

    public void duplicateEmailValidation(HashMap<Long, User> users, User user) throws DuplicateEmailException {
        for (User oneUser : users.values()) {
            if (oneUser.getEmail().equals(user.getEmail())) {
                throw new DuplicateEmailException("Пользователь с таким email уже существует");
            }
        }
    }

    public void noEmailValidation(User user) throws NoEmailException {
        if (user.getEmail() == null) {
            throw new NoEmailException("Электронная почта отсутствует");
        }
    }

    public void nameValidation(User user) throws ValidationException {
        if (user.getName().isBlank()) {
            throw new ValidationException("Отсутствует имя пользователя");
        }
    }

    public void idValidation(HashMap<Long, User> users, Long id) throws ValidationException {
        if (!users.containsKey(id)) {
            throw new ValidationException("Пользователь с таким id не существует");
        }
    }

    public void userValidation(HashMap<Long, User> users, User user) throws ValidationException,
            DuplicateEmailException, NoEmailException, InvalidEmailException {
        noEmailValidation(user);
        emailValidation(user);
        duplicateEmailValidation(users, user);
        nameValidation(user);
    }
}
