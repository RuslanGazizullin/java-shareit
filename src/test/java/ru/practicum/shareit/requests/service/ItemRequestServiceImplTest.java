package ru.practicum.shareit.requests.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.requests.validation.ItemRequestValidation;
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

class ItemRequestServiceImplTest {

    @MockBean
    private ItemRequestRepository itemRequestRepository;
    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private UserRepository userRepository;
    private ItemRequestService itemRequestService;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDto resultRequest;
    private List<ItemRequestDto> resultList;
    private PageImpl<ItemRequest> itemRequestPage;
    private final LocalDateTime presentTime = LocalDateTime.now().withNano(0);
    private final User user = new User(1L, "name", "email@email.ru");
    private final List<Item> items = new ArrayList<>();
    private final List<ItemRequest> itemRequests = new ArrayList<>();

    @BeforeEach
    void beforeEach() {
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        ItemRequestMapper itemRequestMapper = new ItemRequestMapper(itemRepository);
        ItemRequestValidation itemRequestValidation = new ItemRequestValidation(userRepository, itemRequestRepository);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, itemRequestMapper, itemRequestValidation);
        itemRequest = new ItemRequest(1L, "description", 1L);
        itemRequestDto = new ItemRequestDto(1L, "description", presentTime, items);
    }

    @Test
    void testAdd() {
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));

        resultRequest = itemRequestService.add(itemRequest, 1L);

        assertNotNull(resultRequest);
        assertEquals(resultRequest, itemRequestDto);
    }

    @Test
    void testAddWrongUser() {
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);
        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.add(itemRequest, 99L)
        );

        assertEquals(exception.getMessage(), "Пользователь не найден");
    }

    @Test
    void testAddNoDescription() {
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));

        itemRequest.setDescription(null);
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemRequestService.add(itemRequest, 1L)
        );

        assertEquals(exception.getMessage(), "Отсутствует описание запроса");
    }

    @Test
    void testFindAllByRequester() {
        itemRequests.add(itemRequest);
        when(itemRequestRepository.findAllByRequesterId(any()))
                .thenReturn(itemRequests);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));

        resultList = itemRequestService.findAllByRequester(1L);

        assertNotNull(resultList);
        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0), itemRequestDto);
    }

    @Test
    void testFindAllByRequesterWrongUser() {
        itemRequests.add(itemRequest);
        when(itemRequestRepository.findAllByRequesterId(any()))
                .thenReturn(itemRequests);
        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.findAllByRequester(99L)
        );

        assertEquals(exception.getMessage(), "Пользователь не найден");
    }

    @Test
    void testFindAllNoPage() {
        itemRequests.add(itemRequest);
        when(itemRequestRepository.findAll())
                .thenReturn(itemRequests);

        resultList = itemRequestService.findAll(2L, null, null);

        assertNotNull(resultList);
        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0), itemRequestDto);
    }

    @Test
    void testFindAllNoPageWithFrom() {
        itemRequests.add(itemRequest);
        when(itemRequestRepository.findAll())
                .thenReturn(itemRequests);

        resultList = itemRequestService.findAll(2L, 0, null);

        assertNotNull(resultList);
        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0), itemRequestDto);
    }

    @Test
    void testFindAllNoPageOwner() {
        itemRequests.add(itemRequest);
        when(itemRequestRepository.findAll())
                .thenReturn(itemRequests);

        resultList = itemRequestService.findAll(1L, null, null);

        assertEquals(resultList.size(), 0);
    }

    @Test
    void testFindAllPage() {
        itemRequestPage = new PageImpl<>(Collections.singletonList(itemRequest));
        when(itemRequestRepository.findAll(any(Pageable.class)))
                .thenReturn(itemRequestPage);

        resultList = itemRequestService.findAll(2L, 0, 1);

        assertNotNull(resultList);
        assertEquals(resultList.size(), 1);
        assertEquals(resultList.get(0), itemRequestDto);
    }

    @Test
    void testFindAllPageOwner() {
        itemRequestPage = new PageImpl<>(Collections.singletonList(itemRequest));
        when(itemRequestRepository.findAll(any(Pageable.class)))
                .thenReturn(itemRequestPage);

        resultList = itemRequestService.findAll(1L, 0, 1);

        assertEquals(resultList.size(), 0);
    }

    @Test
    void testFindAllPageWrongSize() {
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemRequestService.findAll(1L, 0, 0)
        );

        assertEquals(exception.getMessage(), "Недопустимые параматры from и size");
    }

    @Test
    void testFindAllPageWrongFrom() {
        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemRequestService.findAll(1L, -1, 1)
        );

        assertEquals(exception.getMessage(), "Недопустимые параматры from и size");
    }

    @Test
    void testFindById() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(any()))
                .thenReturn(Optional.ofNullable(itemRequest));

        resultRequest = itemRequestService.findById(1L, 1L);

        assertNotNull(resultRequest);
        assertEquals(resultRequest, itemRequestDto);
    }

    @Test
    void testFindByIdWrongUser() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());
        when(itemRequestRepository.findById(any()))
                .thenReturn(Optional.ofNullable(itemRequest));

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.findById(1L, 99L)
        );

        assertEquals(exception.getMessage(), "Пользователь не найден");
    }

    @Test
    void testFindByIdWrongRequestId() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(any()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.findById(99L, 1L)
        );

        assertEquals(exception.getMessage(), "Запрос не найден");
    }
}