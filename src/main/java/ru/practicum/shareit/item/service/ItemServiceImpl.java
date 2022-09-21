package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    private final ItemMapper itemMapper;

    public ItemServiceImpl(ItemStorage itemStorage, ItemMapper itemMapper) {
        this.itemStorage = itemStorage;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemDto add(ItemDto itemDto, Long userId) throws ItemWithoutUserIdException, UserNotFoundException,
            AvailableException, EmptyItemNameException, EmptyItemDescriptionException {
        return itemMapper.toItemDto(itemStorage.add(itemMapper.fromItemDto(itemDto), userId));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) throws UserNotFoundException,
            ItemWithoutUserIdException, EmptyItemNameException, WrongOwnerException, AvailableException,
            EmptyItemDescriptionException {
        return itemMapper.toItemDto(itemStorage.update(itemMapper.fromItemDto(itemDto), itemId, userId));
    }

    @Override
    public ItemDto findById(Long itemId) {
        return itemMapper.toItemDto(itemStorage.findById(itemId));
    }

    @Override
    public List<ItemDto> findAllByOwner(Long userId) {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : itemStorage.findAllByOwner(userId)) {
            itemsDto.add(itemMapper.toItemDto(item));
        }
        return itemsDto;
    }

    @Override
    public List<ItemDto> findByText(String text) {
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : itemStorage.findByText(text)) {
            itemsDto.add(itemMapper.toItemDto(item));
        }
        return itemsDto;
    }
}
