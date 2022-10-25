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

class BookingServiceImplTest {

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
    private final List<Booking> bookings = new ArrayList<>();
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

        assertEquals(exception.getMessage(), "Пользователь не найден");
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

        assertEquals(exception.getMessage(), "Такой вещи не существует");
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

        assertEquals(exception.getMessage(), "Вещь недоступна для аренды");
    }

    @Test
    void testCreateWrongStartDate() {
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        booking.setStart(presentTime.minusDays(1L));

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.create(booking, 1L)
        );

        assertEquals(exception.getMessage(), "Некорректная дата начала");
    }

    @Test
    void testCreateWrongEndDate() {
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        booking.setEnd(presentTime.minusDays(1L));

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.create(booking, 1L)
        );

        assertEquals(exception.getMessage(), "Некорректная дата окончания");
    }

    @Test
    void testCreateWrongDate() {
        when(bookingRepository.save(any()))
                .thenReturn(booking);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        booking.setEnd(presentTime.plusMinutes(30L));

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.create(booking, 1L)
        );

        assertEquals(exception.getMessage(), "Дата начала должна быть раньше даты окончания");
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

        assertEquals(exception.getMessage(), "Хозяин не может забронировать свою вещь");
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

        assertEquals(exception.getMessage(), "Арендатор не может обновлять данные о бронировании");
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

        assertEquals(exception.getMessage(), "Бронирование не найдено");
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

        assertEquals(exception.getMessage(), "Пользователь не является хозяином вещи");
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

        assertEquals(exception.getMessage(), "Бронирование уже подтверждено");
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

        assertEquals(exception.getMessage(), "Бронирование не найдено");
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

        assertEquals(exception.getMessage(), "Пользователь не авляется ни хозяином, ни арендатором");
    }

    @Test
    void testFindAllByBookerStateAllNoPage() {
        bookings.add(booking);
        when(bookingRepository.findAllByBookerId(any()))
                .thenReturn(bookings);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByBooker(1L, "ALL", null, null);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByBookerStateAllNoPageWithFrom() {
        bookings.add(booking);
        when(bookingRepository.findAllByBookerId(any()))
                .thenReturn(bookings);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByBooker(1L, "ALL", 0, null);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByBookerStateCurrentNoPage() {
        bookings.add(booking);
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(any(), any(), any()))
                .thenReturn(bookings);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByBooker(1L, "CURRENT", null, null);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByBookerStatePastNoPage() {
        bookings.add(booking);
        when(bookingRepository.findAllByBookerIdAndEndBefore(any(), any()))
                .thenReturn(bookings);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByBooker(1L, "PAST", null, null);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByBookerStateFutureNoPage() {
        bookings.add(booking);
        when(bookingRepository.findAllByBookerIdAndStartAfter(any(), any()))
                .thenReturn(bookings);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByBooker(1L, "FUTURE", null, null);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByBookerStateWaitingNoPage() {
        bookings.add(booking);
        when(bookingRepository.findAllByBookerId(any()))
                .thenReturn(bookings);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByBooker(1L, "WAITING", null, null);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByBookerStateRejectedNoPage() {
        booking.setStatus(BookingStatus.REJECTED);
        bookings.add(booking);
        when(bookingRepository.findAllByBookerId(any()))
                .thenReturn(bookings);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByBooker(1L, "REJECTED", null, null);

        bookingDto.setStatus(BookingStatus.REJECTED);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByBookerStateAllPage() {
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
    void testFindAllByBookerStateCurrentPage() {
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
    void testFindAllByBookerStatePastPage() {
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
    void testFindAllByBookerStateFuturePage() {
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
    void testFindAllByBookerStateWaitingPage() {
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
    void testFindAllByBookerStateRejectedPage() {
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
    void testFindAllByBookerWrongStatePage() {
        bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(bookingRepository.findAllByBookerId(any(), any(Pageable.class)))
                .thenReturn(bookingPage);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.findAllByBooker(1L, "WRONG", 0, 1)
        );

        assertEquals(exception.getMessage(), "Unknown state: WRONG");
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

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.findAllByBooker(1L, "REJECTED", 0, 0)
        );

        assertEquals(exception.getMessage(), "Недопустимые параматры from и size");
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

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.findAllByBooker(1L, "REJECTED", -1, 1)
        );

        assertEquals(exception.getMessage(), "Недопустимые параматры from и size");
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

        assertEquals(exception.getMessage(), "У владельца нет ни одной вещи");
    }

    @Test
    void testFindAllByOwnerStateAllNoPage() {
        itemsId.add(1L);
        bookings.add(booking);
        when(itemRepository.findAllIdByOwner(any()))
                .thenReturn(itemsId);
        when(bookingRepository.findAll())
                .thenReturn(bookings);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByOwner(1L, "ALL", null, null);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByOwnerStateAllNoPageWithFrom() {
        itemsId.add(1L);
        bookings.add(booking);
        when(itemRepository.findAllIdByOwner(any()))
                .thenReturn(itemsId);
        when(bookingRepository.findAll())
                .thenReturn(bookings);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByOwner(1L, "ALL", 0, null);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByOwnerStateFutureNoPage() {
        itemsId.add(1L);
        bookings.add(booking);
        when(itemRepository.findAllIdByOwner(any()))
                .thenReturn(itemsId);
        when(bookingRepository.findAllByStartAfter(any()))
                .thenReturn(bookings);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByOwner(1L, "FUTURE", null, null);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByOwnerStatePastNoPage() {
        itemsId.add(1L);
        bookings.add(booking);
        when(itemRepository.findAllIdByOwner(any()))
                .thenReturn(itemsId);
        when(bookingRepository.findAllByEndBefore(any()))
                .thenReturn(bookings);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByOwner(1L, "PAST", null, null);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByOwnerStateCurrentNoPage() {
        itemsId.add(1L);
        bookings.add(booking);
        when(itemRepository.findAllIdByOwner(any()))
                .thenReturn(itemsId);
        when(bookingRepository.findAllByStartBeforeAndEndAfter(any(), any()))
                .thenReturn(bookings);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByOwner(1L, "CURRENT", null, null);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByOwnerStateWaitingNoPage() {
        itemsId.add(1L);
        bookings.add(booking);
        when(itemRepository.findAllIdByOwner(any()))
                .thenReturn(itemsId);
        when(bookingRepository.findAll())
                .thenReturn(bookings);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByOwner(1L, "WAITING", null, null);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
    }

    @Test
    void testFindAllByOwnerStateRejectedNoPage() {
        itemsId.add(1L);
        booking.setStatus(BookingStatus.REJECTED);
        bookings.add(booking);
        when(itemRepository.findAllIdByOwner(any()))
                .thenReturn(itemsId);
        when(bookingRepository.findAll())
                .thenReturn(bookings);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        resultBookings = bookingService.findAllByOwner(1L, "REJECTED", null, null);

        bookingDto.setStatus(BookingStatus.REJECTED);

        assertEquals(resultBookings.size(), 1);
        assertEquals(resultBookings.get(0), bookingDto);
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
    void testFindAllByOwnerStateFuturePage() {
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
    void testFindAllByOwnerStatePastPage() {
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
    void testFindAllByOwnerStateCurrentPage() {
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
    void testFindAllByOwnerStateWaitingPage() {
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
    void testFindAllByOwnerStateRejectedPage() {
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

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.findAllByOwner(1L, "ALL", -1, 1)
        );

        assertEquals(exception.getMessage(), "Недопустимые параматры from и size");
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

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.findAllByOwner(1L, "ALL", -1, 1)
        );

        assertEquals(exception.getMessage(), "Недопустимые параматры from и size");
    }
}