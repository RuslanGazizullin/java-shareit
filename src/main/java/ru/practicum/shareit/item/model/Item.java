package ru.practicum.shareit.item.model;

import lombok.Data;

@Data
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean isAvailable;
    private Long owner;
    private Long requestId;

    public Item(Long id, String name, String description, Boolean isAvailable, Long owner, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
        this.owner = owner;
        this.requestId = requestId;
    }
}
