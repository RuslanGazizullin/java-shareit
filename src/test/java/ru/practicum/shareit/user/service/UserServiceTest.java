package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.validation.UserValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @MockBean
    private UserRepository userRepository;
    private UserService userService;
    private UserMapper userMapper;
    private UserDto resultUser;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        UserValidation userValidation = new UserValidation(userRepository);
        userMapper = new UserMapper();
        userService = new UserServiceImpl(userRepository, userValidation, userMapper);
        user = new User(1L, "name", "email@email.ru");
        userDto = new UserDto(1L, "name", "email@email.ru");
        resultUser = new UserDto();
    }

    @Test
    void testAdd() {
        when(userRepository.save(any()))
                .thenReturn(user);

        resultUser = userService.add(user);

        assertNotNull(resultUser);
        assertEquals(resultUser, userDto);
    }

    @Test
    void testAddNoEmail() {
        user.setEmail(null);
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.add(user)
        );
        assertEquals("Электронная почта отсутствует", exception.getMessage());
    }

    @Test
    void testAddWrongEmail() {
        user.setEmail("email");
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.add(user)
        );
        assertEquals("Неверный формат email", exception.getMessage());
    }

    @Test
    void testUpdate() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(user));
        User updatedUser = new User(1L, "updatedName", "updatedEmail@email.ru");
        when(userRepository.save(any()))
                .thenReturn(user);

        resultUser = userService.update(updatedUser, 1L);

        assertNotNull(resultUser);
        assertEquals(resultUser, userMapper.toUserDto(updatedUser));
    }

    @Test
    void testUpdateName() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(user));
        User updatedUser = new User(null, "updatedName", null);
        UserDto finalUpdatedUser = new UserDto(1L, "updatedName", "email@email.ru");
        when(userRepository.save(any()))
                .thenReturn(user);

        resultUser = userService.update(updatedUser, 1L);

        assertNotNull(resultUser);
        assertEquals(resultUser, finalUpdatedUser);
    }

    @Test
    void testUpdateEmail() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(user));
        User updatedUser = new User(null, null, "updatedEmail@email.ru");
        UserDto finalUpdatedUser = new UserDto(1L, "name", "updatedEmail@email.ru");
        when(userRepository.save(any()))
                .thenReturn(user);

        resultUser = userService.update(updatedUser, 1L);

        assertNotNull(resultUser);
        assertEquals(resultUser, finalUpdatedUser);
    }

    @Test
    void testUpdateNoData() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(user));
        User updatedUser = new User(null, null, null);
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.update(updatedUser, 1L)
        );
        assertEquals(exception.getMessage(), "Отсутствуют данные для обновления");
    }

    @Test
    void testUpdateWrongId() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());

        User updatedUser = new User(99L, "updatedName", "updatedEmail@email.ru");

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.update(updatedUser, 99L)
        );
        assertEquals(exception.getMessage(), "Пользователь с таким id не существует");
    }

    @Test
    void testFindAllEmpty() {
        when(userRepository.findAll())
                .thenReturn(new ArrayList<>());

        List<UserDto> result = userService.findAll();
        assertEquals(result.size(), 0);
    }

    @Test
    void testFindAll() {
        List<User> users = new ArrayList<>();
        users.add(user);
        when(userRepository.findAll())
                .thenReturn(users);

        List<UserDto> result = userService.findAll();

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0), userDto);
    }

    @Test
    void testFindById() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(user));

        resultUser = userService.findById(1L);
        assertNotNull(resultUser);
        assertEquals(resultUser, userDto);
    }

    @Test
    void testFindByIdWrongId() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.findById(99L)
        );
        assertEquals(exception.getMessage(), "Пользователь с таким id не существует");
    }

    @Test
    void testDeleteWrongId() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.delete(99L)
        );
        assertEquals(exception.getMessage(), "Пользователь с таким id не существует");
    }
}