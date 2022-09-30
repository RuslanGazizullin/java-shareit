package ru.practicum.shareit.requests.model;

import lombok.Data;

@Data
public class ItemRequest {
    private Long id;
    private String description;
    private Long requesterId;
}
