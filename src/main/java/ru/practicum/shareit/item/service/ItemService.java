package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto add(ItemDto itemDto, Long userId) throws ItemWithoutUserIdException, UserNotFoundException, AvailableException, EmptyItemNameException, EmptyItemDescriptionException;

    ItemDto update(ItemDto itemDto, Long itemId, Long userId) throws UserNotFoundException, ItemWithoutUserIdException, EmptyItemNameException, WrongOwnerException, AvailableException, EmptyItemDescriptionException;

    ItemDto findById(Long itemId);

    List<ItemDto> findAllByOwner(Long userId);

    List<ItemDto> findByText(String text);
}
