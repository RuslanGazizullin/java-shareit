package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.validation.ItemValidation;

import java.util.Comparator;
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
        itemValidation.userIdValidation(userId);
        itemDto.setOwner(userId);
        log.info("Creating item {}", itemDto);
        return itemMapper.toItemDto(itemRepository.save(itemMapper.fromItemDto(itemDto)));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        itemValidation.itemIdValidation(itemId);
        itemValidation.userIdValidation(userId);
        Item item = itemRepository.findById(itemId).get();
        itemValidation.itemOwnerValidation(item, userId);
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        log.info("Update item №{}", itemId);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemWithBookingDto findById(Long itemId, Long userId) {
        itemValidation.itemIdValidation(itemId);
        itemValidation.userIdValidation(userId);
        log.info("Get item №{}", itemId);
        return itemMapper.toItemWithBookingDto(itemRepository.findById(itemId).get(), userId);
    }

    @Override
    public List<ItemWithBookingDto> findAllByOwner(Long userId, Integer from, Integer size) {
        itemValidation.userIdValidation(userId);
        log.info("Get items by owner №{}, from={}, size={}", userId, from, size);
        return itemRepository.findAllByOwner(userId, PageRequest.of(from / size, size))
                .stream()
                .map(item -> itemMapper.toItemWithBookingDto(item, userId))
                .sorted(Comparator.comparing(ItemWithBookingDto::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findByText(String text, Integer from, Integer size) {
        log.info("Get items by text={}, from={}, size={}", text, from, size);
        return itemRepository.findByText(text, PageRequest.of(from / size, size))
                .stream()
                .filter(Item -> Item.getAvailable().equals(true))
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Comment comment, Long itemId, Long userId) {
        itemValidation.commentValidation(itemId, userId);
        log.info("Creating comment {} to item №{}", comment, itemId);
        comment.setItemId(itemId);
        comment.setAuthorId(userId);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }
}
