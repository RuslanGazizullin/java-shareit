package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item add(Item itemDto, Long userId);

    Item update(Item item, Long itemId, Long userId);

    Item findById(Long itemId);

    List<Item> findAllByOwner(Long userId);

    List<Item> findByText(String text);
}
