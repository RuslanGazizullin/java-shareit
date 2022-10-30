package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.Comment;
import ru.practicum.shareit.item.dto.Item;
import ru.practicum.shareit.item.dto.UpdatedItem;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping()
    public ResponseEntity<Object> createItem(@RequestBody @Valid Item itemDto,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Creating item {}", itemDto);
        return itemClient.createItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody @Valid UpdatedItem updatedItem, @PathVariable long itemId,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Update item №{}", itemId);
        return itemClient.updateItem(updatedItem, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItem(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get item №{}", itemId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> findAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                      @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Get items by owner №{}, from={}, size={}", userId, from, size);
        return itemClient.getAllItemsByOwner(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findAllItemsByText(@RequestParam String text,
                                                     @RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                     @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Get items by text={}, from={}, size={}", text, from, size);
        return itemClient.getAllItemsByText(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestBody @Valid Comment comment, @PathVariable long itemId,
                                                @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Creating comment {} to item №{}", comment, itemId);
        return itemClient.createComment(comment, itemId, userId);
    }
}
