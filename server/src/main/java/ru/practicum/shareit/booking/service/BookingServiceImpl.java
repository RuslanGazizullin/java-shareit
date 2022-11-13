package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.validation.BookingValidation;
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
    public BookingDto create(Booking booking, Long bookerId) {
        bookingValidation.bookingValidation(bookerId, booking);
        booking.setBookerId(bookerId);
        booking.setStatus(BookingStatus.WAITING);
        log.info("Creating booking {}, userId={}", booking, bookerId);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approve(Long bookingId, boolean approved, Long ownerId) {
        bookingValidation.bookingIdValidation(bookingId);
        bookingValidation.bookingBookerValidation(bookingId, ownerId);
        Booking booking = bookingRepository.findById(bookingId).get();
        bookingValidation.bookingOwnerValidation(bookingId, ownerId);
        bookingValidation.approveStatusValidation(bookingId);
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        log.info("Approving booking №{}, ownerId={}", bookingId, ownerId);
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    public BookingDto findById(Long id, Long userId) {
        bookingValidation.bookingIdValidation(id);
        bookingValidation.ownerOrBookerValidation(id, userId);
        log.info("Get booking №{}, userId={}", id, userId);
        return bookingMapper.toBookingDto(bookingRepository.findById(id).get());
    }

    @Override
    public List<BookingDto> findAllByBooker(Long bookerId, String bookingState, Integer from, Integer size) {
        bookingValidation.bookerIdValidation(bookerId);
        final LocalDateTime presentTime = LocalDateTime.now();
        log.info("Get booking with state {}, bookerId={}, from={}, size={}", bookingState, bookerId, from, size);
        int page = from / size;
        switch (bookingState) {
            case "ALL":
                return toBookingDtoSortedByTime(bookingRepository.findAllByBookerId(bookerId,
                        PageRequest.of(page, size, Sort.by("start").descending())).toList());
            case "FUTURE":
                return toBookingDtoSortedByTime(bookingRepository.findAllByBookerIdAndStartAfter(bookerId,
                        presentTime, PageRequest.of(page, size, Sort.by("start").descending())).toList());
            case "PAST":
                return toBookingDtoSortedByTime(bookingRepository.findAllByBookerIdAndEndBefore(bookerId,
                        presentTime, PageRequest.of(page, size, Sort.by("start").descending())).toList());
            case "CURRENT":
                return toBookingDtoSortedByTime(bookingRepository
                        .findAllByBookerIdAndStartBeforeAndEndAfter(bookerId, presentTime, presentTime,
                                PageRequest.of(page, size, Sort.by("start").descending())).toList());
            default:
                return toBookingDtoSortedByTime(bookingRepository.findAllByBookerId(bookerId,
                                PageRequest.of(page, size, Sort.by("start").descending()))
                        .stream()
                        .filter(booking -> booking.getStatus().name().equals(bookingState))
                        .collect(Collectors.toList()));
        }
    }

    @Override
    public List<BookingDto> findAllByOwner(Long ownerId, String bookingState, Integer from, Integer size) {
        bookingValidation.bookerIdValidation(ownerId);
        final LocalDateTime presentTime = LocalDateTime.now();
        List<Long> itemsId = itemRepository.findAllIdByOwner(ownerId);
        bookingValidation.itemIdValidation(itemsId);
        log.info("Get booking with state {}, ownerId={}, from={}, size={}", bookingState, ownerId, from, size);
        int page = from / size;
        switch (bookingState) {
            case "ALL":
                return toBookingDtoSortedByTime(bookingRepository
                        .findAll(PageRequest.of(page, size, Sort.by("start").descending()))
                        .stream()
                        .filter(booking -> itemsId.contains(booking.getItemId()))
                        .collect(Collectors.toList()));
            case "FUTURE":
                return toBookingDtoSortedByTime(bookingRepository
                        .findAllByStartAfter(presentTime, PageRequest.of(page, size, Sort.by("start").descending()))
                        .stream()
                        .filter(booking -> itemsId.contains(booking.getItemId()))
                        .collect(Collectors.toList()));
            case "PAST":
                return toBookingDtoSortedByTime(bookingRepository
                        .findAllByEndBefore(presentTime, PageRequest.of(page, size, Sort.by("start").descending()))
                        .stream()
                        .filter(booking -> itemsId.contains(booking.getItemId()))
                        .collect(Collectors.toList()));
            case "CURRENT":
                return toBookingDtoSortedByTime(bookingRepository
                        .findAllByStartBeforeAndEndAfter(presentTime, presentTime,
                                PageRequest.of(page, size, Sort.by("start").descending()))
                        .stream()
                        .filter(booking -> itemsId.contains(booking.getItemId()))
                        .collect(Collectors.toList()));
            default:
                return toBookingDtoSortedByTime(bookingRepository
                        .findAll(PageRequest.of(page, size, Sort.by("start").descending()))
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
