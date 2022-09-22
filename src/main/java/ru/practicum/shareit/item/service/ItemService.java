package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto add(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long itemId, Long userId);

    ItemDto findById(Long itemId);

    List<ItemDto> findAllByOwner(Long userId);

    List<ItemDto> findByText(String text);
}
