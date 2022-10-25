package ru.practicum.shareit.item.validation;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Comment;
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

    public void itemWithoutUserIdValidation(Long userId) {
        if (userId == null) {
            throw new ItemWithoutUserIdException("Не указан хозяин вещи");
        }
    }

    public void userNotFoundValidation(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public void availableValidation(Item item) {
        if (item.getAvailable() == null) {
            throw new ValidationException("Не указана доступность вещи");
        }
    }

    public void emptyItemNameValidation(Item item) {
        if (item.getName().isBlank()) {
            throw new ValidationException("Не указано название вещи");
        }
    }

    public void emptyItemDescriptionValidation(Item item) {
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Не указано описание вещи");
        }
    }

    public void itemIdValidation(Long id) {
        if (itemRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Вещь с таким id не существует");
        }
    }

    public void commentValidation(Comment comment, Long itemId, Long userId) {
        if (comment.getText().isBlank()) {
            throw new ValidationException("Текст комментария отсутствует");
        }
        if (bookingRepository.findAllByItemIdAndBookerId(itemId, userId)
                .stream()
                .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                .noneMatch(booking -> booking.getEnd().isBefore(LocalDateTime.now()))) {
            throw new ValidationException("Пользователь не арендовал данную вещь");
        }
    }

    public void itemValidation(Item item, Long userId) {
        itemWithoutUserIdValidation(userId);
        userNotFoundValidation(userId);
        availableValidation(item);
        emptyItemNameValidation(item);
        emptyItemDescriptionValidation(item);
    }

    public void fromAndSizeValidation(Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Недопустимые параматры from и size");
        }
    }
}
