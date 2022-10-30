package ru.practicum.shareit.requests.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.time.LocalDateTime;

@Component
public class ItemRequestMapper {

    private final ItemRepository itemRepository;

    public ItemRequestMapper(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                LocalDateTime.now().withNano(0),
                itemRepository.findAllByRequestId(itemRequest.getId())
        );
    }
}
