package ru.practicum.shareit.user.validation;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Component
public class UserValidation {

    private final UserRepository userRepository;

    public UserValidation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void emailValidation(User user) {
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Неверный формат email");
        }
    }

    public void noEmailValidation(User user) {
        if (user.getEmail() == null) {
            throw new ValidationException("Электронная почта отсутствует");
        }
    }

    public void idValidation(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Пользователь с таким id не существует");
        }
    }

    public void userValidation(User user) {
        noEmailValidation(user);
        emailValidation(user);
    }
}
