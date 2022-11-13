package ru.practicum.shareit.booking.validation;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Component
public class BookingValidation {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    public BookingValidation(UserRepository userRepository, ItemRepository itemRepository,
                             BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
    }

    public void bookerIdValidation(Long bookerId) {
        if (userRepository.findById(bookerId).isEmpty()) {
            throw new NotFoundException("User not found");
        }
    }

    public void itemIdValidation(Booking booking) {
        if (itemRepository.findById(booking.getItemId()).isEmpty()) {
            throw new NotFoundException("Item not found");
        }
    }

    public void itemAvailableValidation(Booking booking) {
        if (itemRepository.findById(booking.getItemId()).get().getAvailable().equals(false)) {
            throw new ValidationException("Item isn't available for booking");
        }
    }

    public void bookingIdValidation(Long id) {
        if (bookingRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Booking not found");
        }
    }

    public void bookingOwnerValidation(Long bookingId, Long ownerId) {
        if (!itemRepository.findById(bookingRepository.findById(bookingId).get().getItemId()).get().getOwner()
                .equals(ownerId)) {
            throw new ValidationException("User isn't owner");
        }
    }

    public void bookingBookerValidation(Long bookingId, Long userId) {
        if (bookingRepository.findById(bookingId).get().getBookerId().equals(userId)) {
            throw new NotFoundException("Booker cannot update the booking data");
        }
    }

    public void ownerOrBookerValidation(Long bookingId, Long userId) {
        final Booking booking = bookingRepository.findById(bookingId).get();
        if (!booking.getBookerId().equals(userId) &&
                !itemRepository.findById(booking.getItemId()).get().getOwner()
                        .equals(userId)) {
            throw new NotFoundException("User isn't owner or booker");
        }
    }

    public void approveStatusValidation(Long bookingId) {
        if (bookingRepository.findById(bookingId).get().getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("The booking has already been approved");
        }
    }

    public void ownerCreateBookingValidation(Booking booking, Long userId) {
        if (itemRepository.findById(booking.getItemId()).get().getOwner().equals(userId)) {
            throw new NotFoundException("The owner can't book his item");
        }
    }

    public void itemIdValidation(List<Long> itemsId) {
        if (itemsId.size() == 0) {
            throw new ValidationException("The owner doesn't have a single item");
        }
    }

    public void bookingValidation(Long bookerId, Booking booking) {
        bookerIdValidation(bookerId);
        itemIdValidation(booking);
        itemAvailableValidation(booking);
        ownerCreateBookingValidation(booking, bookerId);
    }
}
