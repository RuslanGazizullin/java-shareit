package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item add(Item itemDto, Long userId) throws ItemWithoutUserIdException, UserNotFoundException, AvailableException, EmptyItemNameException, EmptyItemDescriptionException;

    Item update(Item item, Long itemId, Long userId) throws WrongOwnerException, UserNotFoundException, ItemWithoutUserIdException, EmptyItemNameException, AvailableException, EmptyItemDescriptionException;

    Item findById(Long itemId);

    List<Item> findAllByOwner(Long userId);

    List<Item> findByText(String text);
}
