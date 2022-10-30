package ru.practicum.shareit.requests.validation;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
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
            throw new NotFoundException("User not found");
        }
    }

    public void requestIdValidation(Long requestId) {
        if (itemRequestRepository.findById(requestId).isEmpty()) {
            throw new NotFoundException("Request not found");
        }
    }
}
