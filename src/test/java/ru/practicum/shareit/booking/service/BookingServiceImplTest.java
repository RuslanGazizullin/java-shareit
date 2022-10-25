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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @Test
    @DirtiesContext
    void testCreate() {
        Booking booking = Booking
                .builder()
                .start(LocalDateTime.now().withNano(0).plusSeconds(2L))
                .end(LocalDateTime.now().withNano(0).plusSeconds(3L))
                .itemId(1L)
                .build();
        userService.add(User.builder().name("name").email("email@email.ru").build());
        userService.add(User.builder().name("booker").email("email1@email.ru").build());
        itemService.add(ItemDto.builder().name("name").description("description").available(true).build(), 1L);
        bookingService.create(booking, 2L);

        TypedQuery<Booking> query = em
                .createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking result = query
                .setParameter("id", 1L)
                .getSingleResult();

        assertThat(result.getId(), notNullValue());
        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getStart(), equalTo(booking.getStart()));
        assertThat(result.getEnd(), equalTo(booking.getEnd()));
        assertThat(result.getItemId(), equalTo(booking.getItemId()));
        assertThat(result.getBookerId(), equalTo(2L));
        assertThat(result.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    @DirtiesContext
    void testApprove() {
        Booking booking = Booking
                .builder()
                .start(LocalDateTime.now().withNano(0).plusSeconds(2L))
                .end(LocalDateTime.now().withNano(0).plusSeconds(3L))
                .itemId(1L)
                .build();
        userService.add(User.builder().name("name").email("email@email.ru").build());
        userService.add(User.builder().name("booker").email("email1@email.ru").build());
        itemService.add(ItemDto.builder().name("name").description("description").available(true).build(), 1L);
        bookingService.create(booking, 2L);
        bookingService.approve(1L, true, 1L);

        TypedQuery<Booking> query = em
                .createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking result = query
                .setParameter("id", 1L)
                .getSingleResult();

        assertThat(result.getId(), notNullValue());
        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getStart(), equalTo(booking.getStart()));
        assertThat(result.getEnd(), equalTo(booking.getEnd()));
        assertThat(result.getItemId(), equalTo(booking.getItemId()));
        assertThat(result.getBookerId(), equalTo(2L));
        assertThat(result.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    @DirtiesContext
    void testFindById() {
        Booking booking1 = Booking
                .builder()
                .start(LocalDateTime.now().withNano(0).plusSeconds(2L))
                .end(LocalDateTime.now().withNano(0).plusSeconds(3L))
                .itemId(1L)
                .build();
        Booking booking2 = Booking
                .builder()
                .start(LocalDateTime.now().withNano(0).plusSeconds(4L))
                .end(LocalDateTime.now().withNano(0).plusSeconds(5L))
                .itemId(1L)
                .build();
        userService.add(User.builder().name("name").email("email@email.ru").build());
        userService.add(User.builder().name("booker1").email("email1@email.ru").build());
        itemService.add(ItemDto.builder().name("name").description("description").available(true).build(), 1L);
        bookingService.create(booking1, 2L);
        bookingService.create(booking2, 2L);
        bookingService.approve(1L, true, 1L);
        bookingService.approve(2L, true, 1L);

        TypedQuery<Booking> query = em
                .createQuery("Select b from Booking b where b.id = :id", Booking.class);
        Booking result = query
                .setParameter("id", 2L)
                .getSingleResult();

        assertThat(result.getId(), notNullValue());
        assertThat(result.getId(), equalTo(2L));
        assertThat(result.getStart(), equalTo(booking2.getStart()));
        assertThat(result.getEnd(), equalTo(booking2.getEnd()));
        assertThat(result.getItemId(), equalTo(booking2.getItemId()));
        assertThat(result.getBookerId(), equalTo(2L));
        assertThat(result.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    @DirtiesContext
    void testFindAllByBooker() {
        Booking booking1 = Booking
                .builder()
                .start(LocalDateTime.now().withNano(0).plusSeconds(2L))
                .end(LocalDateTime.now().withNano(0).plusSeconds(3L))
                .itemId(1L)
                .build();
        Booking booking2 = Booking
                .builder()
                .start(LocalDateTime.now().withNano(0).plusSeconds(4L))
                .end(LocalDateTime.now().withNano(0).plusSeconds(5L))
                .itemId(1L)
                .build();
        userService.add(User.builder().name("name").email("email@email.ru").build());
        userService.add(User.builder().name("booker1").email("email1@email.ru").build());
        userService.add(User.builder().name("booker2").email("email2@email.ru").build());
        itemService.add(ItemDto.builder().name("name").description("description").available(true).build(), 1L);
        bookingService.create(booking1, 2L);
        bookingService.create(booking2, 3L);
        bookingService.approve(1L, true, 1L);
        bookingService.approve(2L, true, 1L);

        TypedQuery<Booking> query = em
                .createQuery("Select b from Booking b where b.bookerId = :bookerId", Booking.class);
        List<Booking> result = query
                .setParameter("bookerId", 2L)
                .getResultList();

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), notNullValue());
        assertThat(result.get(0).getId(), equalTo(1L));
        assertThat(result.get(0).getStart(), equalTo(booking1.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(booking1.getEnd()));
        assertThat(result.get(0).getItemId(), equalTo(booking1.getItemId()));
        assertThat(result.get(0).getBookerId(), equalTo(2L));
        assertThat(result.get(0).getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    @DirtiesContext
    void testFindAllByOwner() {
        List<Long> itemIds = new ArrayList<>();
        Booking booking1 = Booking
                .builder()
                .start(LocalDateTime.now().withNano(0).plusSeconds(2L))
                .end(LocalDateTime.now().withNano(0).plusSeconds(3L))
                .itemId(1L)
                .build();
        Booking booking2 = Booking
                .builder()
                .start(LocalDateTime.now().withNano(0).plusSeconds(4L))
                .end(LocalDateTime.now().withNano(0).plusSeconds(5L))
                .itemId(1L)
                .build();
        userService.add(User.builder().name("name").email("email@email.ru").build());
        userService.add(User.builder().name("booker1").email("email1@email.ru").build());
        userService.add(User.builder().name("booker2").email("email2@email.ru").build());
        itemService.add(ItemDto.builder().name("name").description("description").available(true).build(), 1L);
        itemIds.add(1L);
        bookingService.create(booking1, 2L);
        bookingService.create(booking2, 3L);
        bookingService.approve(1L, true, 1L);
        bookingService.approve(2L, true, 1L);

        TypedQuery<Booking> query = em
                .createQuery("Select b from Booking b", Booking.class);
        List<Booking> result = query
                .getResultList()
                .stream()
                .filter(booking -> itemIds.contains(booking.getItemId()))
                .collect(Collectors.toList());

        assertThat(result.size(), equalTo(2));
        assertThat(result.get(0).getId(), notNullValue());
        assertThat(result.get(0).getId(), equalTo(1L));
        assertThat(result.get(0).getStart(), equalTo(booking1.getStart()));
        assertThat(result.get(0).getEnd(), equalTo(booking1.getEnd()));
        assertThat(result.get(0).getItemId(), equalTo(booking1.getItemId()));
        assertThat(result.get(0).getBookerId(), equalTo(2L));
        assertThat(result.get(0).getStatus(), equalTo(BookingStatus.APPROVED));
    }
}