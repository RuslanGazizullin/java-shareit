package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.validation.ItemValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final ItemValidation itemValidation;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public ItemServiceImpl(ItemRepository itemRepository, ItemMapper itemMapper, ItemValidation itemValidation,
                           CommentRepository commentRepository, CommentMapper commentMapper) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.itemValidation = itemValidation;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    @Override
    public ItemDto add(ItemDto itemDto, Long userId) {
        itemValidation.itemDbValidation(itemMapper.fromItemDto(itemDto), userId);
        itemDto.setOwner(userId);
        log.info("Вещь успешно добавлена");
        return itemMapper.toItemDto(itemRepository.save(itemMapper.fromItemDto(itemDto)));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        itemValidation.itemIdValidation(itemId);
        Item oldItem = itemRepository.findById(itemId).get();
        if (!oldItem.getOwner().equals(userId)) {
            throw new UserNotFoundException("Пользователь не является владельцем");
        }
        Item updatedItem = new Item();
        updatedItem.setId(itemId);
        updatedItem.setOwner(userId);
        if (itemDto.getName() == null) {
            updatedItem.setName(oldItem.getName());
        } else {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() == null) {
            updatedItem.setDescription(oldItem.getDescription());
        } else {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() == null) {
            updatedItem.setIsAvailable(oldItem.getIsAvailable());
        } else {
            updatedItem.setIsAvailable(itemDto.getAvailable());
        }
        itemRepository.save(updatedItem);
        log.info("Данные о вещи успешно обновлены");
        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemWithBookingDto findById(Long itemId, Long userId) {
        itemValidation.itemIdValidation(itemId);
        log.info("Данные о вещи успешно получены");
        return itemMapper.toItemWithBookingDto(itemRepository.findById(itemId).get(), userId);
    }

    @Override
    public List<ItemWithBookingDto> findAllByOwner(Long userId) {
        log.info("Список вещей пользователя успешно сформирован");
        return itemRepository.findAllByOwner(userId)
                .stream()
                .map(item -> itemMapper.toItemWithBookingDto(item, userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findByText(String text) {
        log.info("Список доступных для аренды вещей успешно сформирован");
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            return itemRepository.findByText(text)
                    .stream()
                    .filter(Item -> Item.getIsAvailable().equals(true))
                    .map(itemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public CommentDto addComment(Comment comment, Long itemId, Long userId) {
        itemValidation.commentValidation(comment, itemId, userId);
        comment.setItemId(itemId);
        comment.setAuthorId(userId);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }
}
