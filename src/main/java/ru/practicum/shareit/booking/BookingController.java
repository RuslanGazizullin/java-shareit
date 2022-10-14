package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final String USER_ID = "X-Sharer-User-Id";

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public Booking create(@RequestBody Booking booking, @RequestHeader(USER_ID) Long bookerId) {
        return bookingService.create(booking, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable Long bookingId, @RequestParam boolean approved,
                              @RequestHeader(USER_ID) Long ownerId) {
        return bookingService.approve(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@PathVariable Long bookingId, @RequestHeader(USER_ID) Long userId) {
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping()
    public List<BookingDto> findAllByBooker(@RequestHeader(USER_ID) Long bookerId,
                                            @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllByBooker(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwner(@RequestHeader(USER_ID) Long ownerId,
                                           @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.findAllByOwner(ownerId, state);
    }
}
