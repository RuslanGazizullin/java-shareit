package ru.practicum.shareit.requests.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * // TODO .
 */
@Data
public class ItemRequest {
    Long id;
    String description;
    User requester;
    LocalDateTime created;
}
