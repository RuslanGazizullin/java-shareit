package ru.practicum.shareit.booking.validation;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
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
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    public void itemIdValidation(Booking booking) {
        if (itemRepository.findById(booking.getItemId()).isEmpty()) {
            throw new ItemNotFoundException("Такой вещи не существует");
        }
    }

    public void itemAvailableValidation(Booking booking) {
        if (itemRepository.findById(booking.getItemId()).get().getAvailable().equals(false)) {
            throw new BookingValidationException("Вещь недоступна для аренды");
        }
    }

    public void dateValidation(Booking booking) {
        final LocalDateTime presentTime = LocalDateTime.now();
        if (booking.getEnd().isBefore(presentTime)) {
            throw new BookingValidationException("Некорректная дата окончания");
        }
        if (booking.getStart().isBefore(presentTime)) {
            throw new BookingValidationException("Некорректная дата начала");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new BookingValidationException("Дата начала должна быть раньше даты окончания");
        }
    }

    public void bookingIdValidation(Long id) {
        if (bookingRepository.findById(id).isEmpty()) {
            throw new BookingNotFoundException("Бронирование не найдено");
        }
    }

    public void bookingOwnerValidation(Long bookingId, Long ownerId) {
        if (!itemRepository.findById(bookingRepository.findById(bookingId).get().getItemId()).get().getOwner()
                .equals(ownerId)) {
            throw new BookingValidationException("Пользователь не является хозяином вещи");
        }
    }

    public void bookingBookerValidation(Long bookingId, Long userId) {
        if (bookingRepository.findById(bookingId).get().getBookerId().equals(userId)) {
            throw new UserNotFoundException("Арендатор не может обновлять данные о бронировании");
        }
    }

    public void ownerOrBookerValidation(Long bookingId, Long userId) {
        final Booking booking = bookingRepository.findById(bookingId).get();
        if (!booking.getBookerId().equals(userId) &&
                !itemRepository.findById(booking.getItemId()).get().getOwner()
                        .equals(userId)) {
            throw new UserNotFoundException("Пользователь не авляется ни хозяином, ни арендатором");
        }
    }

    public void bookingStateValidation(String bookingState) {
        List<String> bookingStates = Arrays.asList("ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED");
        if (!bookingStates.contains(bookingState)) {
            throw new BookingValidationException("Unknown state: " + bookingState);
        }
    }

    public void approveStatusValidation(Long bookingId) {
        if (bookingRepository.findById(bookingId).get().getStatus().equals(BookingStatus.APPROVED)) {
            throw new BookingValidationException("Бронирование уже подтверждено");
        }
    }

    public void ownerCreateBookingValidation(Booking booking, Long userId) {
        if (itemRepository.findById(booking.getItemId()).get().getOwner().equals(userId)) {
            throw new UserNotFoundException("Хозяин не может забронировать свою вещь");
        }
    }

    public void fromAndSizeValidation(Integer from, Integer size) {
        if (from < 0 || size <= 0) {
            throw new BookingValidationException("Недопустимые параматры from и size");
        }
    }

    public void bookingValidation(Long bookerId, Booking booking) {
        bookerIdValidation(bookerId);
        itemIdValidation(booking);
        itemAvailableValidation(booking);
        dateValidation(booking);
        ownerCreateBookingValidation(booking, bookerId);
    }
}
