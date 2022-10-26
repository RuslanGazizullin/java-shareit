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
            throw new NotFoundException("Пользователь не найден");
        }
    }

    public void itemIdValidation(Booking booking) {
        if (itemRepository.findById(booking.getItemId()).isEmpty()) {
            throw new NotFoundException("Такой вещи не существует");
        }
    }

    public void itemAvailableValidation(Booking booking) {
        if (itemRepository.findById(booking.getItemId()).get().getAvailable().equals(false)) {
            throw new ValidationException("Вещь недоступна для аренды");
        }
    }

    public void dateValidation(Booking booking) {
        final LocalDateTime presentTime = LocalDateTime.now();
        if (booking.getEnd().isBefore(presentTime)) {
            throw new ValidationException("Некорректная дата окончания");
        }
        if (booking.getStart().isBefore(presentTime)) {
            throw new ValidationException("Некорректная дата начала");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("Дата начала должна быть раньше даты окончания");
        }
    }

    public void bookingIdValidation(Long id) {
        if (bookingRepository.findById(id).isEmpty()) {
            throw new NotFoundException("Бронирование не найдено");
        }
    }

    public void bookingOwnerValidation(Long bookingId, Long ownerId) {
        if (!itemRepository.findById(bookingRepository.findById(bookingId).get().getItemId()).get().getOwner()
                .equals(ownerId)) {
            throw new ValidationException("Пользователь не является хозяином вещи");
        }
    }

    public void bookingBookerValidation(Long bookingId, Long userId) {
        if (bookingRepository.findById(bookingId).get().getBookerId().equals(userId)) {
            throw new NotFoundException("Арендатор не может обновлять данные о бронировании");
        }
    }

    public void ownerOrBookerValidation(Long bookingId, Long userId) {
        final Booking booking = bookingRepository.findById(bookingId).get();
        if (!booking.getBookerId().equals(userId) &&
                !itemRepository.findById(booking.getItemId()).get().getOwner()
                        .equals(userId)) {
            throw new NotFoundException("Пользователь не авляется ни хозяином, ни арендатором");
        }
    }

    public void bookingStateValidation(String bookingState) {
        List<String> bookingStates = Arrays.asList("ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED");
        if (!bookingStates.contains(bookingState)) {
            throw new ValidationException("Unknown state: " + bookingState);
        }
    }

    public void approveStatusValidation(Long bookingId) {
        if (bookingRepository.findById(bookingId).get().getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException("Бронирование уже подтверждено");
        }
    }

    public void ownerCreateBookingValidation(Booking booking, Long userId) {
        if (itemRepository.findById(booking.getItemId()).get().getOwner().equals(userId)) {
            throw new NotFoundException("Хозяин не может забронировать свою вещь");
        }
    }

    public void itemIdValidation(List<Long> itemsId) {
        if (itemsId.size() == 0) {
            throw new ValidationException("У владельца нет ни одной вещи");
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
