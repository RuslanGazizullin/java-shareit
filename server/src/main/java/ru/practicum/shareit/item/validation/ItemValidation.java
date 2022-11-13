package ru.practicum.shareit.item.validation;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

@Component
public class ItemValidation {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    public ItemValidation(UserRepository userRepository, ItemRepository itemRepository,
                          BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
    }

    public void userIdValidation(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("User not found");
        }
    }

    public void itemIdValidation(Long itemId) {
        if (itemRepository.findById(itemId).isEmpty()) {
            throw new NotFoundException("Item not found");
        }
    }

    public void itemOwnerValidation(Item item, Long userId) {
        if (!item.getOwner().equals(userId)) {
            throw new NotFoundException("User isn't owner");
        }
    }

    public void commentValidation(Long itemId, Long userId) {
        if (bookingRepository.findAllByItemIdAndBookerId(itemId, userId)
                .stream()
                .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                .noneMatch(booking -> booking.getEnd().isBefore(LocalDateTime.now()))) {
            throw new ValidationException("User didn't book this item");
        }
    }
}