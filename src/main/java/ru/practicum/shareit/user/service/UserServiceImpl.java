package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.validation.UserValidation;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserValidation userValidation;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserValidation userValidation, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userValidation = userValidation;
        this.userMapper = userMapper;
    }

    public UserDto add(User user) {
        userValidation.userValidation(user);
        log.info("Пользователь успешно добавлен");
        return userMapper.toUserDto(userRepository.save(user));
    }

    public UserDto update(User user, Long id) {
        userValidation.idValidation(id);
        User oldUser = userRepository.findById(id).get();
        User updatedUser = new User();
        updatedUser.setId(id);
        if ((user.getName() == null && user.getEmail() == null)) {
            throw new ValidationException("Отсутствуют данные для обновления");
        }
        if (user.getName() == null) {
            updatedUser.setName(oldUser.getName());
        } else {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() == null) {
            updatedUser.setEmail(oldUser.getEmail());
        } else {
            userValidation.userValidation(user);
            updatedUser.setEmail(user.getEmail());
        }
        userRepository.save(updatedUser);
        log.info("Пользователь успешно обновлён");
        return userMapper.toUserDto(updatedUser);
    }

    public List<UserDto> findAll() {
        log.info("Список пользователей получен");
        return userRepository.findAll().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    public UserDto findById(Long id) {
        userValidation.idValidation(id);
        log.info("Пользователь найден");
        return userMapper.toUserDto(userRepository.findById(id).get());
    }

    public void delete(Long id) {
        userValidation.idValidation(id);
        userRepository.deleteById(id);
        log.info("Пользователь успешно удалён");
    }
}
