package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {

    BookingDto create(Booking booking, Long bookerId);

    BookingDto approve(Long bookingId, boolean approved, Long bookerId);

    BookingDto findById(Long id, Long userId);

    List<BookingDto> findAllByBooker(Long bookerId, String state);

    List<BookingDto> findAllByOwner(Long ownerId, String state);
}
