package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.validation.BookingValidation;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @MockBean
    private BookingRepository bookingRepository;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private UserRepository userRepository;
    private BookingService bookingService;
    private BookingDto bookingDto;
    private BookingDto resultBooking;
    private Booking booking;
    private List<BookingDto> resultBookings;
    private PageImpl<Booking> bookingPage;
    private final List<Long> itemsId = new ArrayList<>();
    private final LocalDateTime presentTime = LocalDateTime.now().withNano(0);
    private final User user = new User(1L, "name", "email@email.ru");
    private final Item item = new Item(1L, "name", "description", true, 2L, null);

    @BeforeEach
    void beforeEach() {
        bookingRepository = mock(BookingRepository.class);
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        BookingValidation bookingValidation = new BookingValidation(userRepository, itemRepository, bookingRepository);
        BookingMapper bookingMapper = new BookingMapper(itemRepository, userRepository);
        bookingService = new BookingServiceImpl(bookingRepository, bookingValidation, bookingMapper, itemRepository);
        booking = Booking
                .builder()
                .id(1L)
                .start(presentTime.plusHours(1L))
                .end(presentTime.plusHours(2L))
                .itemId(1L)
                .bookerId(1L)
                .status(BookingStatus.WAITING)
                .build();
        bookingDto = BookingDto
                .builder()
                .id(1L)
                .start(LocalDateTime.now().withNano(0).plusHours(1L))
                .end(LocalDateTime.now().withNano(0).plusHours(2L))
                .status(BookingStatus.WAITING)
                .booker(user)
                .item(item)
                .build();
    }

    @Test
    void testCreate() {
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBooking = bookingService.create(booking, 1L);

        assertNotNull(resultBooking);
        assertEquals(resultBooking, bookingDto);
    }

    @Test
    void testCreateWrongBookerId() {
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        booking.setBookerId(99L);
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.create(booking, 99L)
        );

        assertEquals(exception.getMessage(), "User not found");
    }

    @Test
    void testCreateWrongItemId() {
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.empty());

        booking.setItemId(99L);
        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.create(booking, 1L)
        );

        assertEquals(exception.getMessage(), "Item not found");
    }

    @Test
    void testCreateNotAvailable() {
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        item.setAvailable(false);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.create(booking, 1L)
        );

        assertEquals(exception.getMessage(), "Item isn't available for booking");
    }

    @Test
    void testCreateOwner() {
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.create(booking, 2L)
        );

        assertEquals(exception.getMessage(), "The owner can't book his item");
    }

    @Test
    void testApprove() {
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(booking));

        resultBooking = bookingService.approve(1L, true, 2L);

        assertNotNull(resultBooking);
        assertEquals(resultBooking.getStatus(), BookingStatus.APPROVED);
    }

    @Test
    void testApproveRejected() {
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(booking));

        resultBooking = bookingService.approve(1L, false, 2L);

        assertNotNull(resultBooking);
        assertEquals(resultBooking.getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void testApproveByBooker() {
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(booking));

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.approve(1L, true, 1L)
        );

        assertEquals(exception.getMessage(), "Booker cannot update the booking data");
    }

    @Test
    void testApproveWrongBookingId() {
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.approve(99L, true, 2L)
        );

        assertEquals(exception.getMessage(), "Booking not found");
    }

    @Test
    void testApproveByWrongUser() {
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(booking));

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.approve(1L, true, 3L)
        );

        assertEquals(exception.getMessage(), "User isn't owner");
    }

    @Test
    void testApproveAlreadyApproved() {
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(booking));

        booking.setStatus(BookingStatus.APPROVED);

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.approve(1L, true, 2L)
        );

        assertEquals(exception.getMessage(), "The booking has already been approved");
    }

    @Test
    void testFindById() {
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.ofNullable(booking));
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBooking = bookingService.findById(1L, 2L);

        assertNotNull(resultBooking);
        assertEquals(resultBooking, bookingDto);
    }

    @Test
    void testFindByIdWrongBookingId() {
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.empty());
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.findById(99L, 1L)
        );

        assertEquals(exception.getMessage(), "Booking not found");
    }

    @Test
    void testFindByIdOtherUser() {
        when(bookingRepository.findById(any()))
                .thenReturn(Optional.of(booking));
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.findById(1L, 3L)
        );

        assertEquals(exception.getMessage(), "User isn't owner or booker");
    }

    @Test
    void testFindAllByBookerStateAll() {
        bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(bookingRepository.findAllByBookerId(any(), any(Pageable.class)))
                .thenReturn(bookingPage);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByBooker(1L, "ALL", 0, 1);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByBookerStateCurrent() {
        bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(any(), any(), any(), any(Pageable.class)))
                .thenReturn(bookingPage);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByBooker(1L, "CURRENT", 0, 1);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByBookerStatePast() {
        bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(bookingRepository.findAllByBookerIdAndEndBefore(any(), any(), any(Pageable.class)))
                .thenReturn(bookingPage);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByBooker(1L, "PAST", 0, 1);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByBookerStateFuture() {
        bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(bookingRepository.findAllByBookerIdAndStartAfter(any(), any(), any(Pageable.class)))
                .thenReturn(bookingPage);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByBooker(1L, "FUTURE", 0, 1);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByBookerStateWaiting() {
        bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(bookingRepository.findAllByBookerId(any(), any(Pageable.class)))
                .thenReturn(bookingPage);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByBooker(1L, "WAITING", 0, 1);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByBookerStateRejected() {
        booking.setStatus(BookingStatus.REJECTED);
        bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(bookingRepository.findAllByBookerId(any(), any(Pageable.class)))
                .thenReturn(bookingPage);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByBooker(1L, "REJECTED", 0, 1);

        bookingDto.setStatus(BookingStatus.REJECTED);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByBookerWrongSize() {
        booking.setStatus(BookingStatus.REJECTED);
        bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(bookingRepository.findAllByBookerId(any(), any(Pageable.class)))
                .thenReturn(bookingPage);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        final ArithmeticException exception = assertThrows(
                ArithmeticException.class,
                () -> bookingService.findAllByBooker(1L, "REJECTED", 0, 0)
        );

        assertEquals(exception.getClass(), ArithmeticException.class);
        assertEquals(exception.getMessage(), "/ by zero");
    }

    @Test
    void testFindAllByBookerWrongFrom() {
        booking.setStatus(BookingStatus.REJECTED);
        bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(bookingRepository.findAllByBookerId(any(), any(Pageable.class)))
                .thenReturn(bookingPage);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.findAllByBooker(1L, "REJECTED", -1, 1)
        );

        assertEquals(exception.getClass(), IllegalArgumentException.class);
        assertEquals(exception.getMessage(), "Page index must not be less than zero");
    }

    @Test
    void testFindAllByOwnerNoItem() {
        when(itemRepository.findAllIdByOwner(any()))
                .thenReturn(itemsId);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.findAllByOwner(1L, "ALL", null, null)
        );

        assertEquals(exception.getMessage(), "The owner doesn't have a single item");
    }

    @Test
    void testFindAllByOwnerStateAllPage() {
        itemsId.add(1L);
        bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(itemRepository.findAllIdByOwner(any()))
                .thenReturn(itemsId);
        when(bookingRepository.findAll(any(Pageable.class)))
                .thenReturn(bookingPage);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByOwner(1L, "ALL", 0, 1);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByOwnerStateFuture() {
        itemsId.add(1L);
        bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(itemRepository.findAllIdByOwner(any()))
                .thenReturn(itemsId);
        when(bookingRepository.findAllByStartAfter(any(), any(Pageable.class)))
                .thenReturn(bookingPage);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByOwner(1L, "FUTURE", 0, 1);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByOwnerStatePast() {
        itemsId.add(1L);
        bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(itemRepository.findAllIdByOwner(any()))
                .thenReturn(itemsId);
        when(bookingRepository.findAllByEndBefore(any(), any(Pageable.class)))
                .thenReturn(bookingPage);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByOwner(1L, "PAST", 0, 1);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByOwnerStateCurrent() {
        itemsId.add(1L);
        bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(itemRepository.findAllIdByOwner(any()))
                .thenReturn(itemsId);
        when(bookingRepository.findAllByStartBeforeAndEndAfter(any(), any(), any(Pageable.class)))
                .thenReturn(bookingPage);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByOwner(1L, "CURRENT", 0, 1);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByOwnerStateWaiting() {
        itemsId.add(1L);
        bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(itemRepository.findAllIdByOwner(any()))
                .thenReturn(itemsId);
        when(bookingRepository.findAll(any(Pageable.class)))
                .thenReturn(bookingPage);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByOwner(1L, "WAITING", 0, 1);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByOwnerStateRejected() {
        itemsId.add(1L);
        booking.setStatus(BookingStatus.REJECTED);
        bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(itemRepository.findAllIdByOwner(any()))
                .thenReturn(itemsId);
        when(bookingRepository.findAll(any(Pageable.class)))
                .thenReturn(bookingPage);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByOwner(1L, "REJECTED", 0, 1);

        bookingDto.setStatus(BookingStatus.REJECTED);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByOwnerWrongSize() {
        itemsId.add(1L);
        bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(itemRepository.findAllIdByOwner(any()))
                .thenReturn(itemsId);
        when(bookingRepository.findAll(any(Pageable.class)))
                .thenReturn(bookingPage);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        final ArithmeticException exception = assertThrows(
                ArithmeticException.class,
                () -> bookingService.findAllByOwner(1L, "ALL", 0, 0)
        );

        assertEquals(exception.getClass(), ArithmeticException.class);
        assertEquals(exception.getMessage(), "/ by zero");
    }

    @Test
    void testFindAllByOwnerWrongFrom() {
        itemsId.add(1L);
        bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(itemRepository.findAllIdByOwner(any()))
                .thenReturn(itemsId);
        when(bookingRepository.findAll(any(Pageable.class)))
                .thenReturn(bookingPage);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.findAllByOwner(1L, "ALL", -1, 1)
        );

        assertEquals(exception.getClass(), IllegalArgumentException.class);
        assertEquals(exception.getMessage(), "Page index must not be less than zero");
    }
}
