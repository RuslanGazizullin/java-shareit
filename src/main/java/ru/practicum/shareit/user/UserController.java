package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.InvalidEmailException;
import ru.practicum.shareit.exception.NoEmailException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserServiceImpl userServiceImpl;

    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping()
    public User add(@RequestBody User user) throws ValidationException, DuplicateEmailException, NoEmailException,
            InvalidEmailException {
        return userServiceImpl.add(user);
    }

    @PatchMapping("/{id}")
    public User update(@RequestBody User user, @PathVariable Long id) throws ValidationException, NoEmailException,
            DuplicateEmailException, InvalidEmailException {
        return userServiceImpl.update(user, id);
    }

    @GetMapping()
    public List<User> findAll() {
        return userServiceImpl.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) throws ValidationException {
        return userServiceImpl.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws ValidationException {
        userServiceImpl.delete(id);
    }
}
