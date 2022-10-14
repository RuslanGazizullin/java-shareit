package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.validation.BookingValidation;
import ru.practicum.shareit.exception.BookingValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingValidation bookingValidation;
    private final BookingMapper bookingMapper;
    private final ItemRepository itemRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, BookingValidation bookingValidation,
                              BookingMapper bookingMapper, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingValidation = bookingValidation;
        this.bookingMapper = bookingMapper;
        this.itemRepository = itemRepository;
    }

    @Override
    public Booking create(Booking booking, Long bookerId) {
        bookingValidation.bookingValidation(bookerId, booking);
        booking.setBookerId(bookerId);
        booking.setStatus(BookingStatus.WAITING);
        log.info("Запрос на бронирование успешно создан");
        return bookingRepository.save(booking);
    }

    @Override
    public BookingDto approve(Long bookingId, boolean approved, Long ownerId) {
        bookingValidation.bookingBookerValidation(bookingId, ownerId);
        bookingValidation.bookingIdValidation(bookingId);
        Booking booking = bookingRepository.findById(bookingId).get();
        bookingValidation.bookingOwnerValidation(bookingId, ownerId);
        bookingValidation.approveStatusValidation(bookingId);
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        log.info("Данные о бронировании успешно обновлены");
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto findById(Long id, Long userId) {
        bookingValidation.bookingIdValidation(id);
        bookingValidation.ownerOrBookerValidation(id, userId);
        log.info("Данные о бронировании успешно получены");
        return bookingMapper.toBookingDto(bookingRepository.findById(id).get());
    }

    @Override
    public List<BookingDto> findAllByBooker(Long bookerId, String bookingState) {
        bookingValidation.bookerIdValidation(bookerId);
        bookingValidation.bookingStateValidation(bookingState);
        final LocalDateTime presentTime = LocalDateTime.now();
        log.info("Список бронирований успешно получен");
        switch (bookingState) {
            case "ALL":
                return toBookingDtoSortedByTime(bookingRepository.findAllByBookerId(bookerId));
            case "FUTURE":
                return toBookingDtoSortedByTime(bookingRepository.findAllByBookerIdAndStartAfter(bookerId, presentTime));
            case "PAST":
                return toBookingDtoSortedByTime(bookingRepository.findAllByBookerIdAndEndBefore(bookerId, presentTime));
            case "CURRENT":
                return toBookingDtoSortedByTime(bookingRepository
                        .findAllByBookerIdAndStartBeforeAndEndAfter(bookerId, presentTime, presentTime));
            default:
                return toBookingDtoSortedByTime(bookingRepository.findAllByBookerId(bookerId)
                        .stream()
                        .filter(booking -> booking.getStatus().name().equals(bookingState))
                        .collect(Collectors.toList()));
        }
    }

    @Override
    public List<BookingDto> findAllByOwner(Long ownerId, String bookingState) {
        bookingValidation.bookerIdValidation(ownerId);
        bookingValidation.bookingStateValidation(bookingState);
        final LocalDateTime presentTime = LocalDateTime.now();
        List<Long> itemsId = itemRepository.findAllIdByOwner(ownerId);
        if (itemsId.size() == 0) {
            throw new BookingValidationException("У владельца нет ни одной вещи");
        }
        log.info("Список бронирований успешно получен");
        switch (bookingState) {
            case "ALL":
                return toBookingDtoSortedByTime(bookingRepository.findAll()
                        .stream()
                        .filter(booking -> itemsId.contains(booking.getItemId()))
                        .collect(Collectors.toList()));
            case "FUTURE":
                return toBookingDtoSortedByTime(bookingRepository.findAllByStartAfter(presentTime)
                        .stream()
                        .filter(booking -> itemsId.contains(booking.getItemId()))
                        .collect(Collectors.toList()));
            case "PAST":
                return toBookingDtoSortedByTime(bookingRepository.findAllByEndBefore(presentTime)
                        .stream()
                        .filter(booking -> itemsId.contains(booking.getItemId()))
                        .collect(Collectors.toList()));
            case "CURRENT":
                return toBookingDtoSortedByTime(bookingRepository
                        .findAllByStartBeforeAndEndAfter(presentTime, presentTime)
                        .stream()
                        .filter(booking -> itemsId.contains(booking.getItemId()))
                        .collect(Collectors.toList()));
            default:
                return toBookingDtoSortedByTime(bookingRepository.findAll()
                        .stream()
                        .filter(booking -> itemsId.contains(booking.getItemId()))
                        .filter(booking -> booking.getStatus().name().equals(bookingState))
                        .collect(Collectors.toList()));
        }
    }

    private List<BookingDto> toBookingDtoSortedByTime(List<Booking> bookings) {
        return bookings
                .stream()
                .map(bookingMapper::toBookingDto)
                .sorted(Comparator.comparing(BookingDto::getStart).reversed())
                .collect(Collectors.toList());
    }
}
