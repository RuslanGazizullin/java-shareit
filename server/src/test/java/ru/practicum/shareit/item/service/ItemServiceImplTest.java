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
import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceImplTest {

    private final EntityManager em;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @Test
    void testAdd() {
        ItemDto item = ItemDto.builder().name("name").description("description").available(true).build();
        Long userId = userService.add(User.builder().name("name").email("email31@email.ru").build()).getId();
        Long itemId = itemService.add(item, userId).getId();

        TypedQuery<Item> query = em
                .createQuery("Select i from Item i where i.id = :id", Item.class);
        Item result = query
                .setParameter("id", itemId)
                .getSingleResult();

        assertThat(result.getId(), notNullValue());
        assertThat(result.getId(), equalTo(itemId));
        assertThat(result.getName(), equalTo(item.getName()));
        assertThat(result.getDescription(), equalTo(item.getDescription()));
        assertThat(result.getAvailable(), equalTo(item.getAvailable()));
        assertThat(result.getOwner(), equalTo(item.getOwner()));
        assertThat(result.getRequestId(), equalTo(item.getRequestId()));
    }

    @Test
    void testUpdate() {
        ItemDto item = ItemDto.builder().name("name").description("description").available(true).build();
        ItemDto updatedItem = ItemDto.builder().name("updatedName").description("updatedDescription").available(true).build();
        Long userId = userService.add(User.builder().name("name").email("email32@email.ru").build()).getId();
        Long itemId = itemService.add(item, userId).getId();
        ItemDto result = itemService.update(updatedItem, itemId, userId);

        TypedQuery<Item> query = em
                .createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item1 = query
                .setParameter("id", result.getId())
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
    void testFindById() {
        ItemDto item1 = ItemDto.builder().name("name1").description("description1").available(true).build();
        ItemDto item2 = ItemDto.builder().name("name2").description("description2").available(true).build();
        Long userId = userService.add(User.builder().name("name").email("email33@email.ru").build()).getId();
        Long itemId = itemService.add(item1, userId).getId();
        itemService.add(item2, userId);

        TypedQuery<Item> query = em
                .createQuery("Select i from Item i where i.id = :id", Item.class);
        Item result = query
                .setParameter("id", itemId)
                .getSingleResult();

        assertThat(result.getId(), notNullValue());
        assertThat(result.getId(), equalTo(itemId));
        assertThat(result.getName(), equalTo(item1.getName()));
        assertThat(result.getDescription(), equalTo(item1.getDescription()));
        assertThat(result.getAvailable(), equalTo(item1.getAvailable()));
        assertThat(result.getOwner(), equalTo(item1.getOwner()));
        assertThat(result.getRequestId(), equalTo(item1.getRequestId()));
    }

    @Test
    void testFindAllByOwner() {
        ItemDto item1 = ItemDto.builder().name("name1").description("description1").available(true).build();
        ItemDto item2 = ItemDto.builder().name("name2").description("description2").available(true).build();
        Long user1Id = userService.add(User.builder().name("name1").email("email34@email.ru").build()).getId();
        Long user2Id = userService.add(User.builder().name("name2").email("email35@email.ru").build()).getId();
        Long itemId = itemService.add(item1, user1Id).getId();
        itemService.add(item2, user2Id);

        TypedQuery<Item> query = em
                .createQuery("Select i from Item i where i.owner = :owner", Item.class);
        List<Item> result = query
                .setParameter("owner", user1Id)
                .getResultList();

        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), notNullValue());
        assertThat(result.get(0).getId(), equalTo(itemId));
        assertThat(result.get(0).getName(), equalTo(item1.getName()));
        assertThat(result.get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(result.get(0).getAvailable(), equalTo(item1.getAvailable()));
        assertThat(result.get(0).getOwner(), equalTo(item1.getOwner()));
        assertThat(result.get(0).getRequestId(), equalTo(item1.getRequestId()));
    }

    @Test
    void testFindByText() {
        ItemDto item1 = ItemDto.builder().name("name1Text").description("description1").available(true).build();
        ItemDto item2 = ItemDto.builder().name("name2").description("description2Text").available(true).build();
        Long user1Id = userService.add(User.builder().name("name1").email("email36@email.ru").build()).getId();
        Long user2Id = userService.add(User.builder().name("name2").email("email37@email.ru").build()).getId();
        Long itemId = itemService.add(item1, user1Id).getId();
        itemService.add(item2, user2Id);

        TypedQuery<Item> query = em
                .createQuery("select i from Item i " +
                        "where upper(i.name) like upper(concat('%', :text, '%')) " +
                        " or upper(i.description) like upper(concat('%', :text, '%'))", Item.class);
        List<Item> result = query
                .setParameter("text", "text")
                .getResultList();

        assertThat(result.size(), equalTo(2));
        assertThat(result.get(0).getId(), notNullValue());
        assertThat(result.get(0).getId(), equalTo(itemId));
        assertThat(result.get(0).getName(), equalTo(item1.getName()));
        assertThat(result.get(1).getDescription(), equalTo(item2.getDescription()));
        assertThat(result.get(0).getAvailable(), equalTo(item1.getAvailable()));
        assertThat(result.get(1).getOwner(), equalTo(item2.getOwner()));
        assertThat(result.get(0).getRequestId(), equalTo(item1.getRequestId()));
    }

    @Test
    void testAddComment() {
        Comment comment = Comment.builder().text("text").build();
        Long user1Id = userService.add(User.builder().name("name").email("email39@email.ru").build()).getId();
        Long user2Id = userService.add(User.builder().name("booker").email("email38@email.ru").build()).getId();
        Long itemId = itemService
                .add(ItemDto.builder().name("name").description("description").available(true).build(), user1Id).getId();
        Long bookingId = bookingService.create(Booking
                        .builder()
                        .start(LocalDateTime.now().plusSeconds(1L))
                        .end(LocalDateTime.now().plusSeconds(2L))
                        .itemId(itemId)
                        .build(),
                user2Id).getId();
        bookingService.approve(bookingId, true, user1Id);
        Long commentId;
        try {
            TimeUnit.SECONDS.sleep(3);
            commentId = itemService.addComment(comment, itemId, user2Id).getId();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        TypedQuery<Comment> query = em
                .createQuery("Select c from Comment c where c.text = :text", Comment.class);
        Comment result = query
                .setParameter("text", comment.getText())
                .getSingleResult();

        assertThat(result.getId(), notNullValue());
        assertThat(result.getId(), equalTo(commentId));
        assertThat(result.getText(), equalTo(comment.getText()));
        assertThat(result.getItemId(), equalTo(itemId));
        assertThat(result.getAuthorId(), equalTo(user2Id));
    }
}
