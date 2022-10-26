package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Data
@Builder
public class ItemWithBookingDto {

    private final Long id;
    private final String name;
    private final String description;
    private final boolean available;
    private final Booking lastBooking;
    private final Booking nextBooking;
    private final List<CommentDto> comments;
    private final Long requestId;
}
