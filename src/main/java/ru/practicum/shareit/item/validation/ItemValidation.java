package ru.practicum.shareit.item.validation;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.HashMap;

@Component
public class ItemValidation {

    private final UserStorage userStorage;

    public ItemValidation(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void itemWithoutUserIdValidation(Long userId) throws ItemWithoutUserIdException {
        if (userId == null) {
            throw new ItemWithoutUserIdException("Не указан хозяин вещи");
        }
    }

    public void userNotFoundValidation(Long userId) throws UserNotFoundException {
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    public void availableValidation(Item item) throws AvailableException {
        if (item.getAvailable() == null) {
            throw new AvailableException("Не указана доступность вещи");
        }
    }

    public void emptyItemNameValidation(Item item) throws EmptyItemNameException {
        if (item.getName().isBlank()) {
            throw new EmptyItemNameException("Не указано название вещи");
        }
    }

    public void emptyItemDescriptionValidation(Item item) throws EmptyItemDescriptionException {
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new EmptyItemDescriptionException("Не указано описание вещи");
        }
    }

    public void wrongOwnerValidation(HashMap<Long, Item> items, Long itemId, Long userId) throws WrongOwnerException {
        if (!items.get(itemId).getOwner().equals(userId)) {
            throw new WrongOwnerException("Пользователь не является хозяином");
        }
    }

    public void itemValidation(Item item, Long userId) throws ItemWithoutUserIdException, UserNotFoundException,
            AvailableException, EmptyItemNameException, EmptyItemDescriptionException {
        itemWithoutUserIdValidation(userId);
        userNotFoundValidation(userId);
        availableValidation(item);
        emptyItemNameValidation(item);
        emptyItemDescriptionValidation(item);
    }
}
