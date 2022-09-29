package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;
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
    public User add(@RequestBody User user) {
        return userServiceImpl.add(user);
    }

    @PatchMapping("/{id}")
    public User update(@RequestBody User user, @PathVariable Long id) {
        return userServiceImpl.update(user, id);
    }

    @GetMapping()
    public List<User> findAll() {
        return userServiceImpl.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        return userServiceImpl.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userServiceImpl.delete(id);
    }
}
