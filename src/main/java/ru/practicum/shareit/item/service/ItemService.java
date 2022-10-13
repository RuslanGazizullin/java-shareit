package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {

    ItemDto add(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long itemId, Long userId);

    ItemWithBookingDto findById(Long itemId, Long userId);

    List<ItemWithBookingDto> findAllByOwner(Long userId);

    List<ItemDto> findByText(String text);

    CommentDto addComment(Comment comment, Long itemId, Long userId);
}
