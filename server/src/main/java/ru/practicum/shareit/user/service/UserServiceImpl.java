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
    private final UserMapper userMapper;
    private final UserValidation userValidation;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, UserValidation userValidation) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userValidation = userValidation;
    }

    public UserDto add(User user) {
        log.info("Creating user {}", user);
        return userMapper.toUserDto(userRepository.save(user));
    }

    public UserDto update(User user, Long id) {
        userValidation.idValidation(id);
        User oldUser = userRepository.findById(id).get();
        User updatedUser = new User();
        updatedUser.setId(id);
        if ((user.getName() == null && user.getEmail() == null)) {
            throw new ValidationException("No data for update");
        }
        if (user.getName() == null) {
            updatedUser.setName(oldUser.getName());
        } else {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() == null) {
            updatedUser.setEmail(oldUser.getEmail());
        } else {
            updatedUser.setEmail(user.getEmail());
        }
        userRepository.save(updatedUser);
        log.info("Update user №{}", id);
        return userMapper.toUserDto(updatedUser);
    }

    public List<UserDto> findAll() {
        log.info("Get all users");
        return userRepository.findAll().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    public UserDto findById(Long id) {
        userValidation.idValidation(id);
        log.info("Get user №{}", id);
        return userMapper.toUserDto(userRepository.findById(id).get());
    }

    public void delete(Long id) {
        userValidation.idValidation(id);
        userRepository.deleteById(id);
        log.info("Delete user №{}", id);
    }
}
