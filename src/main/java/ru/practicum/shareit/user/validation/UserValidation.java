package ru.practicum.shareit.user.validation;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.UserValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.Map;

@Component
public class UserValidation {

    public void emailValidation(User user) {
        if (!user.getEmail().contains("@")) {
            throw new UserValidationException("Неверный формат email");
        }
    }

    public void duplicateEmailValidation(Map<Long, User> users, User user) {
        for (User oneUser : users.values()) {
            if (oneUser.getEmail().equals(user.getEmail())) {
                throw new DuplicateEmailException("Пользователь с таким email уже существует");
            }
        }
    }

    public void noEmailValidation(User user) {
        if (user.getEmail() == null) {
            throw new UserValidationException("Электронная почта отсутствует");
        }
    }

    public void nameValidation(User user) {
        if (user.getName().isBlank()) {
            throw new UserValidationException("Отсутствует имя пользователя");
        }
    }

    public void idValidation(Map<Long, User> users, Long id) {
        if (!users.containsKey(id)) {
            throw new UserValidationException("Пользователь с таким id не существует");
        }
    }

    public void userValidation(Map<Long, User> users, User user) {
        noEmailValidation(user);
        emailValidation(user);
        duplicateEmailValidation(users, user);
        nameValidation(user);
    }
}
