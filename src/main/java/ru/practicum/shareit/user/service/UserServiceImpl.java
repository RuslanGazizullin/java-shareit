package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserValidationException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.validation.UserValidation;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserValidation userValidation;

    public UserServiceImpl(UserRepository userRepository, UserValidation userValidation) {
        this.userRepository = userRepository;
        this.userValidation = userValidation;
    }

    public User add(User user) {
        userValidation.userDbValidation(user);
        log.info("Пользователь успешно добавлен");
        return userRepository.save(user);
    }

    public User update(User user, Long id) {
        userValidation.idDbValidation(id);
        User oldUser = userRepository.findById(id).get();
        User updatedUser = new User();
        updatedUser.setId(id);
        if ((user.getName() == null && user.getEmail() == null)) {
            throw new UserValidationException("Отсутствуют данные для обновления");
        }
        if (user.getName() == null) {
            updatedUser.setName(oldUser.getName());
        } else {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() == null) {
            updatedUser.setEmail(oldUser.getEmail());
        } else {
            userValidation.userDbValidation(user);
            updatedUser.setEmail(user.getEmail());
        }
        userRepository.save(updatedUser);
        log.info("Пользователь успешно обновлён");
        return updatedUser;
    }

    public List<User> findAll() {
        log.info("Список пользователей получен");
        return userRepository.findAll();
    }

    public User findById(Long id) {
        userValidation.idDbValidation(id);
        log.info("Пользователь найден");
        return userRepository.findById(id).get();
    }

    public void delete(Long id) {
        userValidation.idDbValidation(id);
        userRepository.deleteById(id);
        log.info("Пользователь успешно удалён");
    }
}
