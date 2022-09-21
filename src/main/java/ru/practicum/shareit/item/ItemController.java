package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping()
    public ItemDto add(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId)
            throws ItemWithoutUserIdException, UserNotFoundException, AvailableException, EmptyItemNameException,
            EmptyItemDescriptionException {
        return itemService.add(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long userId) throws UserNotFoundException,
            ItemWithoutUserIdException, EmptyItemNameException, WrongOwnerException, AvailableException,
            EmptyItemDescriptionException {
        return itemService.update(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@PathVariable Long itemId) {
        return itemService.findById(itemId);
    }

    @GetMapping()
    public List<ItemDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findAllByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByText(@RequestParam String text) {
        return itemService.findByText(text);
    }
}
