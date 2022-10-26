package ru.practicum.shareit.requests;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.service.ItemRequestService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping()
    public ItemRequestDto add(@RequestBody ItemRequest itemRequest, @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        return itemRequestService.add(itemRequest, requesterId);
    }

    @GetMapping()
    public List<ItemRequestDto> findAllByRequester(@RequestHeader("X-Sharer-User-Id") Long requesterId) {
        return itemRequestService.findAllByRequester(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                        @RequestParam(defaultValue = "10") @Positive Integer size) {
        return itemRequestService.findAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@PathVariable Long requestId, @RequestHeader("X-Sharer-User-Id") Long requesterId) {
        return itemRequestService.findById(requestId, requesterId);
    }
}
