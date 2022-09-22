package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    private final ItemMapper itemMapper;

    public ItemServiceImpl(ItemStorage itemStorage, ItemMapper itemMapper) {
        this.itemStorage = itemStorage;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemDto add(ItemDto itemDto, Long userId) {
        return itemMapper.toItemDto(itemStorage.add(itemMapper.fromItemDto(itemDto), userId));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        return itemMapper.toItemDto(itemStorage.update(itemMapper.fromItemDto(itemDto), itemId, userId));
    }

    @Override
    public ItemDto findById(Long itemId) {
        return itemMapper.toItemDto(itemStorage.findById(itemId));
    }

    @Override
    public List<ItemDto> findAllByOwner(Long userId) {
        return itemStorage.findAllByOwner(userId)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findByText(String text) {
        return itemStorage.findByText(text)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
