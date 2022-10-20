package ru.practicum.shareit.requests.validation;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemRequestValidationException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

@Component
public class ItemRequestValidation {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    public ItemRequestValidation(UserRepository userRepository, ItemRequestRepository itemRequestRepository) {
        this.userRepository = userRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    public void userValidation(Long requesterId) {
        if (userRepository.findById(requesterId).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    public void descriptionValidation(ItemRequest itemRequest) {
        if (itemRequest.getDescription() == null) {
            throw new ItemRequestValidationException("Отсутствует описание запроса");
        }
    }

    public void requestIdValidation(Long requestId) {
        if (itemRequestRepository.findById(requestId).isEmpty()) {
            throw new ItemNotFoundException("Запрос не найден");
        }
    }

    public void fromAndSizeValidation(Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new ItemRequestValidationException("Недопустимые параматры from и size");
        }
    }
}
