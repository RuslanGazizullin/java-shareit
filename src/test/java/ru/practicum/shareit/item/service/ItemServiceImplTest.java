package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @Test
    @DirtiesContext
    void testAdd() {
        ItemDto item = ItemDto.builder().name("name").description("description").available(true).build();
        userService.add(User.builder().name("name").email("email31@email.ru").build());
        itemService.add(item, 1L);

        TypedQuery<Item> query = em
                .createQuery("Select i from Item i where i.description = :description", Item.class);
        Item item1 = query
                .setParameter("description", item.getDescription())
                .getSingleResult();

        assertThat(item1.getId(), notNullValue());
        assertThat(item1.getId(), equalTo(1L));
        assertThat(item1.getName(), equalTo(item.getName()));
        assertThat(item1.getDescription(), equalTo(item.getDescription()));
        assertThat(item1.getAvailable(), equalTo(item.getAvailable()));
        assertThat(item1.getOwner(), equalTo(item.getOwner()));
        assertThat(item1.getRequestId(), equalTo(item.getRequestId()));
    }

    @Test
    @DirtiesContext
    void testUpdate() {
        ItemDto item = ItemDto.builder().name("name").description("description").available(true).build();
        ItemDto updatedItem = ItemDto.builder().name("updatedName").description("updatedDescription").available(true).build();
        userService.add(User.builder().name("name").email("email32@email.ru").build());
        itemService.add(item, 1L);
        ItemDto result = itemService.update(updatedItem, 1L, 1L);

        TypedQuery<Item> query = em
                .createQuery("Select i from Item i where i.description = :description", Item.class);
        Item item1 = query
                .setParameter("description", updatedItem.getDescription())
                .getSingleResult();

        assertThat(item1.getId(), notNullValue());
        assertThat(item1.getId(), equalTo(result.getId()));
        assertThat(item1.getName(), equalTo(result.getName()));
        assertThat(item1.getDescription(), equalTo(result.getDescription()));
        assertThat(item1.getAvailable(), equalTo(result.getAvailable()));
        assertThat(item1.getOwner(), equalTo(result.getOwner()));
        assertThat(item1.getRequestId(), equalTo(result.getRequestId()));
    }

    @Test
    @DirtiesContext
    void testFindById() {
        ItemDto item1 = ItemDto.builder().name("name1").description("description1").available(true).build();
        ItemDto item2 = ItemDto.builder().name("name2").description("description2").available(true).build();
        userService.add(User.builder().name("name").email("email33@email.ru").build());
        itemService.add(item1, 1L);
        itemService.add(item2, 1L);

        TypedQuery<Item> query = em
                .createQuery("Select i from Item i where i.id = :id", Item.class);
        Item result = query
                .setParameter("id", 1L)
                .getSingleResult();

        assertThat(result.getId(), notNullValue());
        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getName(), equalTo(item1.getName()));
        assertThat(result.getDescription(), equalTo(item1.getDescription()));
        assertThat(result.getAvailable(), equalTo(item1.getAvailable()));
        assertThat(result.getOwner(), equalTo(item1.getOwner()));
        assertThat(result.getRequestId(), equalTo(item1.getRequestId()));
    }

    @Test
    @DirtiesContext
    void testFindAllByOwner() {
        ItemDto item1 = ItemDto.builder().name("name1").description("description1").available(true).build();
        ItemDto item2 = ItemDto.builder().name("name2").description("description2").available(true).build();
        userService.add(User.builder().name("name1").email("email34@email.ru").build());
        userService.add(User.builder().name("name2").email("email35@email.ru").build());
        itemService.add(item1, 1L);
        itemService.add(item2, 2L);

        TypedQuery<Item> query = em
                .createQuery("Select i from Item i where i.owner = :owner", Item.class);
        List<Item> result = query
                .setParameter("owner", 1L)
                .getResultList();

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), notNullValue());
        assertThat(result.get(0).getId(), equalTo(1L));
        assertThat(result.get(0).getName(), equalTo(item1.getName()));
        assertThat(result.get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(result.get(0).getAvailable(), equalTo(item1.getAvailable()));
        assertThat(result.get(0).getOwner(), equalTo(item1.getOwner()));
        assertThat(result.get(0).getRequestId(), equalTo(item1.getRequestId()));
    }

    @Test
    @DirtiesContext
    void testFindByText() {
        ItemDto item1 = ItemDto.builder().name("name1Text").description("description1").available(true).build();
        ItemDto item2 = ItemDto.builder().name("name2").description("description2Text").available(true).build();
        userService.add(User.builder().name("name1").email("email36@email.ru").build());
        userService.add(User.builder().name("name2").email("email37@email.ru").build());
        itemService.add(item1, 1L);
        itemService.add(item2, 2L);

        TypedQuery<Item> query = em
                .createQuery("select i from Item i " +
                        "where upper(i.name) like upper(concat('%', :text, '%')) " +
                        " or upper(i.description) like upper(concat('%', :text, '%'))", Item.class);
        List<Item> result = query
                .setParameter("text", "text")
                .getResultList();

        assertThat(result.size(), equalTo(2));
        assertThat(result.get(0).getId(), notNullValue());
        assertThat(result.get(0).getId(), equalTo(1L));
        assertThat(result.get(0).getName(), equalTo(item1.getName()));
        assertThat(result.get(1).getDescription(), equalTo(item2.getDescription()));
        assertThat(result.get(0).getAvailable(), equalTo(item1.getAvailable()));
        assertThat(result.get(1).getOwner(), equalTo(item2.getOwner()));
        assertThat(result.get(0).getRequestId(), equalTo(item1.getRequestId()));
    }

    @Test
    @DirtiesContext
    void testAddComment() {
        Comment comment = Comment.builder().text("text").build();
        userService.add(User.builder().name("name").email("email39@email.ru").build());
        userService.add(User.builder().name("booker").email("email38@email.ru").build());
        itemService.add(ItemDto.builder().name("name").description("description").available(true).build(), 1L);
        bookingService.create(Booking
                        .builder()
                        .start(LocalDateTime.now().plusSeconds(1L))
                        .end(LocalDateTime.now().plusSeconds(2L))
                        .itemId(1L)
                        .build(),
                2L);
        bookingService.approve(1L, true, 1L);
        try {
            TimeUnit.SECONDS.sleep(3);
            itemService.addComment(comment, 1L, 2L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        TypedQuery<Comment> query = em
                .createQuery("Select c from Comment c where c.text = :text", Comment.class);
        Comment result = query
                .setParameter("text", comment.getText())
                .getSingleResult();

        assertThat(result.getId(), notNullValue());
        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getText(), equalTo(comment.getText()));
        assertThat(result.getItemId(), equalTo(1L));
        assertThat(result.getAuthorId(), equalTo(2L));
    }
}