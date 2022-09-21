package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.validation.ItemValidation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryItemStorage implements ItemStorage {

    private final HashMap<Long, Item> items = new HashMap<>();
    private Long id = 1L;

    private final ItemValidation itemValidation;

    public InMemoryItemStorage(ItemValidation itemValidation) {
        this.itemValidation = itemValidation;
    }

    private Long generateId() {
        return id++;
    }

    @Override
    public Item add(Item item, Long userId) throws ItemWithoutUserIdException, UserNotFoundException, AvailableException,
            EmptyItemNameException, EmptyItemDescriptionException {
        itemValidation.itemValidation(item, userId);
        item.setId(generateId());
        item.setOwner(userId);
        items.put(item.getId(), item);
        log.info("Вещь успешно добавлена");
        return item;
    }

    @Override
    public Item update(Item item, Long itemId, Long userId) throws WrongOwnerException, UserNotFoundException,
            ItemWithoutUserIdException, EmptyItemNameException, AvailableException, EmptyItemDescriptionException {
        itemValidation.wrongOwnerValidation(items, itemId, userId);
        Item updatedItem = items.get(itemId);
        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }
        items.remove(itemId);
        items.put(itemId, updatedItem);
        log.info("Данные о вещи успешно обновлены");
        return updatedItem;
    }

    @Override
    public Item findById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> findAllByOwner(Long userId) {
        List<Item> allItems = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner().equals(userId)) {
                allItems.add(item);
            }
        }
        return allItems;
    }

    @Override
    public List<Item> findByText(String text) {
        List<Item> itemsWithText = new ArrayList<>();
        if (text != null && !text.isBlank()) {
            for (Item item : items.values()) {
                if ((item.getName().toLowerCase().contains(text.toLowerCase()) && item.getAvailable().equals(true)) ||
                        (item.getDescription().toLowerCase().contains(text.toLowerCase()) &&
                                item.getAvailable().equals(true))) {
                    itemsWithText.add(item);
                }
            }
        }
        return itemsWithText;
    }
}
