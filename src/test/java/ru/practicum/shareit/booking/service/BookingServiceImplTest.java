package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceImplTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @Test
    void testCreate() {
        Long user1Id = userService.add(User.builder().name("name").email("email11@email.ru").build()).getId();
        Long user2Id = userService.add(User.builder().name("booker").email("email12@email.ru").build()).getId();
        Long itemId = itemService
                .add(ItemDto.builder().name("name").description("description").available(true).build(), user1Id).getId();
        Booking booking = Booking
                .builder()
                .start(LocalDateTime.now().withNano(0).plusSeconds(2L))
                .end(LocalDateTime.now().withNano(0).plusSeconds(3L))
                .itemId(itemId)
                .build();
        Long bookingId = bookingService.create(booking, user2Id).getId();

        TypedQuery<Booking> query = em
                .createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking result = query
                .setParameter("id", bookingId)
                .getSingleResult();

        assertThat(result.getId(), notNullValue());
        assertThat(result.getId(), equalTo(bookingId));
        assertThat(result.getStart(), equalTo(booking.getStart()));
        assertThat(result.getEnd(), equalTo(booking.getEnd()));
        assertThat(result.getItemId(), equalTo(booking.getItemId()));
        assertThat(result.getBookerId(), equalTo(user2Id));
        assertThat(result.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void testApprove() {
        Long user1Id = userService.add(User.builder().name("name").email("email13@email.ru").build()).getId();
        Long user2Id = userService.add(User.builder().name("booker").email("email14@email.ru").build()).getId();
        Long itemId = itemService
                .add(ItemDto.builder().name("name").description("description").available(true).build(), user1Id).getId();
        Booking booking = Booking
                .builder()
                .start(LocalDateTime.now().withNano(0).plusSeconds(2L))
                .end(LocalDateTime.now().withNano(0).plusSeconds(3L))
                .itemId(itemId)
                .build();
        Long bookingId = bookingService.create(booking, user2Id).getId();
        bookingService.approve(bookingId, true, user1Id);

        TypedQuery<Booking> query = em
                .createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking result = query
                .setParameter("id", bookingId)
                .getSingleResult();

        assertThat(result.getId(), notNullValue());
        assertThat(result.getId(), equalTo(bookingId));
        assertThat(result.getStart(), equalTo(booking.getStart()));
        assertThat(result.getEnd(), equalTo(booking.getEnd()));
        assertThat(result.getItemId(), equalTo(booking.getItemId()));
        assertThat(result.getBookerId(), equalTo(user2Id));
        assertThat(result.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void testFindById() {
        Long user1Id = userService.add(User.builder().name("name").email("email15@email.ru").build()).getId();
        Long user2Id = userService.add(User.builder().name("booker1").email("email16@email.ru").build()).getId();
        Long itemId = itemService
                .add(ItemDto.builder().name("name").description("description").available(true).build(), user1Id).getId();
        Booking booking1 = Booking
                .builder()
                .start(LocalDateTime.now().withNano(0).plusSeconds(2L))
                .end(LocalDateTime.now().withNano(0).plusSeconds(3L))
                .itemId(itemId)
                .build();
        Booking booking2 = Booking
                .builder()
                .start(LocalDateTime.now().withNano(0).plusSeconds(4L))
                .end(LocalDateTime.now().withNano(0).plusSeconds(5L))
                .itemId(itemId)
                .build();
        Long booking1Id = bookingService.create(booking1, user2Id).getId();
        Long booking2Id = bookingService.create(booking2, user2Id).getId();
        bookingService.approve(booking1Id, true, user1Id);
        bookingService.approve(booking2Id, true, user1Id);

        TypedQuery<Booking> query = em
                .createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking result = query
                .setParameter("id", booking2Id)
                .getSingleResult();

        assertThat(result.getId(), notNullValue());
        assertThat(result.getId(), equalTo(booking2Id));
        assertThat(result.getStart(), equalTo(booking2.getStart()));
        assertThat(result.getEnd(), equalTo(booking2.getEnd()));
        assertThat(result.getItemId(), equalTo(booking2.getItemId()));
        assertThat(result.getBookerId(), equalTo(user2Id));
        assertThat(result.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void testFindAllByBooker() {
        Long user1Id = userService.add(User.builder().name("name").email("email17@email.ru").build()).getId();
        Long user2Id = userService.add(User.builder().name("booker1").email("email18@email.ru").build()).getId();
        Long user3Id = userService.add(User.builder().name("booker2").email("email19@email.ru").build()).getId();
        Long itemId = itemService
                .add(ItemDto.builder().name("name").description("description").available(true).build(), user1Id).getId();
        Booking booking1 = Booking
                .builder()
                .start(LocalDateTime.now().withNano(0).plusSeconds(2L))
                .end(LocalDateTime.now().withNano(0).plusSeconds(3L))
                .itemId(itemId)
                .build();
        Booking booking2 = Booking
                .builder()
                .start(LocalDateTime.now().withNano(0).plusSeconds(4L))
                .end(LocalDateTime.now().withNano(0).plusSeconds(5L))
                .itemId(itemId)
                .build();
        Long booking1Id = bookingService.create(booking1, user2Id).getId();
        Long booking2Id = bookingService.create(booking2, user3Id).getId();
        bookingService.approve(booking1Id, true, user1Id);
        bookingService.approve(booking2Id, true, user1Id);

        TypedQuery<Booking> query = em
                .createQuery("Select b from Booking b where b.bookerId = :bookerId", Booking.class);
        List<Booking> result = query
                .setParameter("bookerId", user2Id)
                .getResultList();

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), notNullValue());
        assertThat(result.get(0).getId(), equalTo(booking1Id));
        assertThat(result.get(0).getStart(), equalTo(booking1.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(booking1.getEnd()));
        assertThat(result.get(0).getItemId(), equalTo(booking1.getItemId()));
        assertThat(result.get(0).getBookerId(), equalTo(user2Id));
        assertThat(result.get(0).getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void testFindAllByOwner() {
        List<Long> itemIds = new ArrayList<>();
        Long user1Id = userService.add(User.builder().name("name").email("email20@email.ru").build()).getId();
        Long user2Id = userService.add(User.builder().name("booker1").email("email21@email.ru").build()).getId();
        Long user3Id = userService.add(User.builder().name("booker2").email("email22@email.ru").build()).getId();
        Long itemId = itemService
                .add(ItemDto.builder().name("name").description("description").available(true).build(), user1Id).getId();
        Booking booking1 = Booking
                .builder()
                .start(LocalDateTime.now().withNano(0).plusSeconds(2L))
                .end(LocalDateTime.now().withNano(0).plusSeconds(3L))
                .itemId(itemId)
                .build();
        Booking booking2 = Booking
                .builder()
                .start(LocalDateTime.now().withNano(0).plusSeconds(4L))
                .end(LocalDateTime.now().withNano(0).plusSeconds(5L))
                .itemId(itemId)
                .build();
        itemIds.add(itemId);
        Long booking1id = bookingService.create(booking1, user2Id).getId();
        Long booking2Id = bookingService.create(booking2, user3Id).getId();
        bookingService.approve(booking1id, true, user1Id);
        bookingService.approve(booking2Id, true, user1Id);

        TypedQuery<Booking> query = em
                .createQuery("Select b from Booking b", Booking.class);
        List<Booking> result = query
                .getResultList()
                .stream()
                .filter(booking -> itemIds.contains(booking.getItemId()))
                .collect(Collectors.toList());

        assertThat(result.size(), equalTo(2));
        assertThat(result.get(0).getId(), notNullValue());
        assertThat(result.get(0).getId(), equalTo(booking1id));
        assertThat(result.get(0).getStart(), equalTo(booking1.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(booking1.getEnd()));
        assertThat(result.get(0).getItemId(), equalTo(booking1.getItemId()));
        assertThat(result.get(0).getBookerId(), equalTo(user2Id));
        assertThat(result.get(0).getStatus(), equalTo(BookingStatus.APPROVED));
    }
}