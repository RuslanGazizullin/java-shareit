package ru.practicum.shareit.item.validation;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class ItemValidation {

    private final UserStorage userStorage;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    public ItemValidation(UserStorage userStorage, UserRepository userRepository, ItemRepository itemRepository,
                          BookingRepository bookingRepository) {
        this.userStorage = userStorage;
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
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    public void userDbNotFoundValidation(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    public void availableValidation(Item item) {
        if (item.getIsAvailable() == null) {
            throw new ItemRequestValidationException("Не указана доступность вещи");
        }
    }

    public void emptyItemNameValidation(Item item) {
        if (item.getName().isBlank()) {
            throw new ItemRequestValidationException("Не указано название вещи");
        }
    }

    public void emptyItemDescriptionValidation(Item item) {
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ItemRequestValidationException("Не указано описание вещи");
        }
    }

    public void wrongOwnerValidation(Map<Long, Item> items, Long itemId, Long userId) {
        if (!items.get(itemId).getOwner().equals(userId)) {
            throw new UserNotFoundException("Пользователь не является хозяином");
        }
    }

    public void itemIdValidation(Long id) {
        if (itemRepository.findById(id).isEmpty()) {
            throw new ItemNotFoundException("Вещь с таким id не существует");
        }
    }

    public void commentValidation(Comment comment, Long itemId, Long userId) {
        if (comment.getText().isBlank()) {
            throw new CommentValidationException("Текст комментария отсутствует");
        }
        if (bookingRepository.findAllByItemIdAndBookerId(itemId, userId)
                .stream()
                .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                .noneMatch(booking -> booking.getEnd().isBefore(LocalDateTime.now()))) {
            throw new CommentValidationException("Пользователь не арендовал данную вещь");
        }
    }

    public void itemValidation(Item item, Long userId) {
        itemWithoutUserIdValidation(userId);
        userNotFoundValidation(userId);
        availableValidation(item);
        emptyItemNameValidation(item);
        emptyItemDescriptionValidation(item);
    }

    public void itemDbValidation(Item item, Long userId) {
        itemWithoutUserIdValidation(userId);
        userDbNotFoundValidation(userId);
        availableValidation(item);
        emptyItemNameValidation(item);
        emptyItemDescriptionValidation(item);
    }
}
