package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestBody Booking booking, @RequestHeader("X-Sharer-User-Id") Long bookerId) {
        return bookingService.create(booking, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable Long bookingId, @RequestParam boolean approved,
                              @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return bookingService.approve(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping()
    public List<BookingDto> findAllByBooker(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                            @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllByBooker(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                           @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllByOwner(ownerId, state);
    }
}
