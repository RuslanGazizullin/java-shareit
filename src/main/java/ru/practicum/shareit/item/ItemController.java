package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final String USER_ID = "X-Sharer-User-Id";

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping()
    public ItemDto add(@RequestBody ItemDto itemDto, @RequestHeader(USER_ID) Long userId) {
        return itemService.add(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                          @RequestHeader(USER_ID) Long userId) {
        return itemService.update(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingDto findById(@PathVariable Long itemId, @RequestHeader(USER_ID) Long userId) {
        return itemService.findById(itemId, userId);
    }

    @GetMapping()
    public List<ItemWithBookingDto> findAllByOwner(@RequestHeader(USER_ID) Long userId) {
        return itemService.findAllByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestParam String text) {
        return itemService.findByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestBody Comment comment, @PathVariable Long itemId,
                                 @RequestHeader(USER_ID) Long userId) {
        return itemService.addComment(comment, itemId, userId);
    }
}
