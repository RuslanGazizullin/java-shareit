package ru.practicum.shareit.item.validation;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Map;

@Component
public class ItemValidation {

    private final UserStorage userStorage;

    public ItemValidation(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void itemWithoutUserIdValidation(Long userId) {
        if (userId == null) {
            throw new ItemWithoutUserIdException("Не указан хозяин вещи");
        }
    }

    public void userNotFoundValidation(Long userId) {
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new ItemOwnerValidationException("Пользователь не найден");
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
            throw new ItemOwnerValidationException("Пользователь не является хозяином");
        }
    }

    public void itemValidation(Item item, Long userId) {
        itemWithoutUserIdValidation(userId);
        userNotFoundValidation(userId);
        availableValidation(item);
        emptyItemNameValidation(item);
        emptyItemDescriptionValidation(item);
    }
}
