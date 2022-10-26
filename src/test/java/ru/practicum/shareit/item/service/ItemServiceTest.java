package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemWithoutUserIdException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.validation.ItemValidation;
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

class ItemServiceTest {

    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private BookingRepository bookingRepository;
    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private UserRepository userRepository;
    private ItemService itemService;
    private Item item;
    private ItemDto itemDto;
    private ItemDto resultItem;
    private PageImpl<Item> itemPage;
    private final LocalDateTime presentTime = LocalDateTime.now().withNano(0);
    private final Comment comment = new Comment(1L, "text", 1L, 1L);
    private final CommentDto commentDto = new CommentDto(1L, "text", "name", presentTime);
    private final List<Booking> bookings = new ArrayList<>();
    private final ItemWithBookingDto itemWithBookingDto = ItemWithBookingDto
            .builder()
            .id(1L)
            .name("name")
            .description("description")
            .available(true)
            .lastBooking(null)
            .nextBooking(null)
            .comments(new ArrayList<>())
            .requestId(1L)
            .build();
    private final ItemDto updateItem = ItemDto
            .builder()
            .id(1L)
            .name("updatedName")
            .description("updatedDescription")
            .available(true)
            .owner(1L)
            .requestId(null)
            .build();
    private final ItemDto updatedItem = ItemDto
            .builder()
            .id(1L)
            .name("updatedName")
            .description("updatedDescription")
            .available(true)
            .owner(1L)
            .requestId(null)
            .build();
    private final User user = new User(1L, "name", "email@email.ru");
    private final Booking booking = Booking
            .builder()
            .id(1L)
            .start(presentTime.minusDays(1L))
            .end(presentTime.minusHours(2L))
            .itemId(1L)
            .bookerId(1L)
            .status(BookingStatus.APPROVED)
            .build();

    @BeforeEach
    void beforeEach() {
        itemRepository = mock(ItemRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        userRepository = mock(UserRepository.class);
        CommentMapper commentMapper = new CommentMapper(userRepository);
        ItemMapper itemMapper = new ItemMapper(bookingRepository, commentRepository, commentMapper);
        ItemValidation itemValidation = new ItemValidation(userRepository, itemRepository, bookingRepository);
        itemService = new ItemServiceImpl(itemRepository, itemMapper, itemValidation, commentRepository, commentMapper);
        item = Item
                .builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(1L)
                .requestId(1L)
                .build();
        itemDto = ItemDto
                .builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(true)
                .owner(1L)
                .requestId(1L)
                .build();
    }

    @Test
    void testAdd() {
        when(itemRepository.save(any()))
                .thenReturn(item);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));

        resultItem = itemService.add(itemDto, 1L);

        assertNotNull(resultItem);
        assertEquals(resultItem, itemDto);
    }

    @Test
    void testAddNoUser() {
        final ItemWithoutUserIdException exception = assertThrows(
                ItemWithoutUserIdException.class,
                () -> itemService.add(itemDto, null)
        );

        assertEquals(exception.getMessage(), "Не указан хозяин вещи");
    }

    @Test
    void testAddWrongUser() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.add(itemDto, 99L)
        );

        assertEquals(exception.getMessage(), "Пользователь не найден");
    }

    @Test
    void testAddNoAvailable() {
        itemDto.setAvailable(null);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.add(itemDto, 1L)
        );

        assertEquals(exception.getMessage(), "Не указана доступность вещи");
    }

    @Test
    void testAddNoName() {
        itemDto.setName("");
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.add(itemDto, 1L)
        );

        assertEquals(exception.getMessage(), "Не указано название вещи");
    }

    @Test
    void testAddNullDescription() {
        itemDto.setDescription(null);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.add(itemDto, 1L)
        );

        assertEquals(exception.getMessage(), "Не указано описание вещи");
    }

    @Test
    void testAddNoDescription() {
        itemDto.setDescription("");
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.add(itemDto, 1L)
        );

        assertEquals(exception.getMessage(), "Не указано описание вещи");
    }

    @Test
    void testUpdateNoItem() {
        when(itemRepository.findById(any()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.update(itemDto, 99L, 1L)
        );

        assertEquals(exception.getMessage(), "Вещь с таким id не существует");
    }

    @Test
    void testUpdateWrongOwner() {
        when(itemRepository.findById(any()))
                .thenReturn(Optional.ofNullable(item));

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.update(itemDto, 1L, 2L)
        );

        assertEquals(exception.getMessage(), "Пользователь не является владельцем");
    }

    @Test
    void testUpdate() {
        when(itemRepository.findById(any()))
                .thenReturn(Optional.ofNullable(item));

        resultItem = itemService.update(updateItem, 1L, 1L);

        assertNotNull(resultItem);
        assertEquals(resultItem, updatedItem);
    }

    @Test
    void testUpdateNoName() {
        updateItem.setName(null);
        when(itemRepository.findById(any()))
                .thenReturn(Optional.ofNullable(item));

        resultItem = itemService.update(updateItem, 1L, 1L);

        updatedItem.setName(item.getName());

        assertNotNull(resultItem);
        assertEquals(resultItem, updatedItem);
    }

    @Test
    void testUpdateNoDescription() {
        updateItem.setDescription(null);
        when(itemRepository.findById(any()))
                .thenReturn(Optional.ofNullable(item));

        resultItem = itemService.update(updateItem, 1L, 1L);

        updatedItem.setDescription(item.getDescription());

        assertNotNull(resultItem);
        assertEquals(resultItem, updatedItem);
    }

    @Test
    void testUpdateNoAvailable() {
        updateItem.setAvailable(null);
        when(itemRepository.findById(any()))
                .thenReturn(Optional.ofNullable(item));

        resultItem = itemService.update(updateItem, 1L, 1L);

        updatedItem.setAvailable(item.getAvailable());

        assertNotNull(resultItem);
        assertEquals(resultItem, updatedItem);
    }

    @Test
    void testFindByIdNoItem() {
        when(itemRepository.findById(any()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.findById(99L, 1L)
        );

        assertEquals(exception.getMessage(), "Вещь с таким id не существует");
    }

    @Test
    void testFindById() {
        bookings.add(new Booking());
        when(itemRepository.findById(any()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findAllByItemIdAndEndBefore(1L, presentTime))
                .thenReturn(bookings);
        when(bookingRepository.findAllByItemIdAndStartAfter(1L, presentTime))
                .thenReturn(bookings);
        when(commentRepository.findAllByItemId(any()))
                .thenReturn(new ArrayList<>());
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));

        ItemWithBookingDto resultItemWithBooking = itemService.findById(1L, 1L);

        assertEquals(resultItemWithBooking, itemWithBookingDto);
    }

    @Test
    void testFindAllByOwner() {
        itemPage = new PageImpl<>(Collections.singletonList(item));
        when(itemRepository.findAllByOwner(any(), any(Pageable.class)))
                .thenReturn(itemPage);

        List<ItemWithBookingDto> resultItemsWithBookings = itemService.findAllByOwner(1L, 0, 1);

        assertEquals(resultItemsWithBookings.size(), 1);
        assertEquals(resultItemsWithBookings.get(0), itemWithBookingDto);
    }

    @Test
    void testFindAllByOwnerWrongSize() {
        itemPage = new PageImpl<>(Collections.singletonList(item));
        when(itemRepository.findAllByOwner(any(), any(Pageable.class)))
                .thenReturn(itemPage);

        final ArithmeticException exception = assertThrows(
                ArithmeticException.class,
                () -> itemService.findAllByOwner(1L, 0, 0)
        );

        assertEquals(exception.getClass(), ArithmeticException.class);
        assertEquals(exception.getMessage(), "/ by zero");
    }

    @Test
    void testFindAllByOwnerWrongFrom() {
        itemPage = new PageImpl<>(Collections.singletonList(item));
        when(itemRepository.findAllByOwner(any(), any(Pageable.class)))
                .thenReturn(itemPage);

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> itemService.findAllByOwner(1L, -1, 1)
        );

        assertEquals(exception.getClass(), IllegalArgumentException.class);
        assertEquals(exception.getMessage(), "Page index must not be less than zero");
    }

    @Test
    void testFindByText() {
        itemPage = new PageImpl<>(Collections.singletonList(item));
        when(itemRepository.findByText(any(), any(Pageable.class)))
                .thenReturn(itemPage);

        List<ItemDto> resultItems = itemService.findByText("name", 0, 1);

        assertEquals(resultItems.size(), 1);
        assertEquals(resultItems.get(0), itemDto);
    }

    @Test
    void testFindByTextWrongSize() {
        itemPage = new PageImpl<>(Collections.singletonList(item));
        when(itemRepository.findByText(any(), any(Pageable.class)))
                .thenReturn(itemPage);

        final ArithmeticException exception = assertThrows(
                ArithmeticException.class,
                () -> itemService.findByText("name", 0, 0)
        );

        assertEquals(exception.getClass(), ArithmeticException.class);
        assertEquals(exception.getMessage(), "/ by zero");
    }

    @Test
    void testFindByTextWrongFrom() {
        itemPage = new PageImpl<>(Collections.singletonList(item));
        when(itemRepository.findByText(any(), any(Pageable.class)))
                .thenReturn(itemPage);

        final IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> itemService.findByText("name", -1, 1)
        );

        assertEquals(exception.getClass(), IllegalArgumentException.class);
        assertEquals(exception.getMessage(), "Page index must not be less than zero");
    }

    @Test
    void testAddComment() {
        bookings.add(booking);
        when(commentRepository.save(comment))
                .thenReturn(comment);
        when(bookingRepository.findAllByItemIdAndBookerId(any(), any()))
                .thenReturn(bookings);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));

        assertEquals(itemService.addComment(comment, 1L, 1L), commentDto);
    }

    @Test
    void testAddCommentNoText() {
        comment.setText("");
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.addComment(comment, 1L, 1L)
        );

        assertEquals(exception.getMessage(), "Текст комментария отсутствует");
    }

    @Test
    void testAddCommentWrongBooker() {
        booking.setEnd(presentTime.plusHours(10L));
        bookings.add(booking);
        when(commentRepository.save(comment))
                .thenReturn(comment);
        when(bookingRepository.findAllByItemIdAndBookerId(any(), any()))
                .thenReturn(bookings);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.addComment(comment, 1L, 1L)
        );

        assertEquals(exception.getMessage(), "Пользователь не арендовал данную вещь");
    }
}