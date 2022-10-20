package ru.practicum.shareit.requests.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemRequestDto {

    private final Long id;
    private final String description;
    private final LocalDateTime created;
    private final List<Item> items;
}
