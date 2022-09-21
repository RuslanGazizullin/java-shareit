package ru.practicum.shareit.booking.model;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * // TODO .
 */
@Data
public class Booking {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    Item item;
    User booker;
    BookingStatus bookingStatus;
}
