package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.requests.model.ItemRequest;

@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private ItemRequest request;

    public ItemDto(Long id, String name, String description, Boolean available, Long owner, ItemRequest request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.request = request;
    }
}
