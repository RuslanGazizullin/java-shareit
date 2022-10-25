package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private final UserService userService;

    @Test
    @DirtiesContext
    void testAdd() {
        ItemRequest itemRequest = ItemRequest.builder().description("description").build();
        userService.add(User.builder().name("name").email("email41@email.ru").build());
        itemRequestService.add(itemRequest, 1L);

        TypedQuery<ItemRequest> query = em
                .createQuery("Select ir from ItemRequest ir where ir.description = :description", ItemRequest.class);
        ItemRequest itemRequest1 = query
                .setParameter("description", itemRequest.getDescription())
                .getSingleResult();

        assertThat(itemRequest1.getId(), notNullValue());
        assertThat(itemRequest1.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(itemRequest1.getRequesterId(), equalTo(itemRequest.getRequesterId()));
    }

    @Test
    @DirtiesContext
    void testFindAllByRequester() {
        ItemRequest itemRequest1 = ItemRequest.builder().description("description1").build();
        ItemRequest itemRequest2 = ItemRequest.builder().description("description2").build();
        userService.add(User.builder().name("name1").email("email43@email.ru").build());
        userService.add(User.builder().name("name2").email("email42@email.ru").build());
        itemRequestService.add(itemRequest1, 1L);
        itemRequestService.add(itemRequest2, 2L);

        TypedQuery<ItemRequest> query = em
                .createQuery("Select ir from ItemRequest ir where ir.requesterId = :requesterId", ItemRequest.class);
        List<ItemRequest> itemRequests = query
                .setParameter("requesterId", itemRequest1.getRequesterId())
                .getResultList();

        assertThat(itemRequests.size(), equalTo(1));
        assertThat(itemRequests.get(0).getId(), notNullValue());
        assertThat(itemRequests.get(0).getDescription(), equalTo(itemRequest1.getDescription()));
        assertThat(itemRequests.get(0).getRequesterId(), equalTo(itemRequest1.getRequesterId()));
    }

    @Test
    @DirtiesContext
    void testFindAll() {
        ItemRequest itemRequest1 = ItemRequest.builder().description("description1").build();
        ItemRequest itemRequest2 = ItemRequest.builder().description("description2").build();
        userService.add(User.builder().name("name").email("email44@email.ru").build());
        itemRequestService.add(itemRequest1, 1L);
        itemRequestService.add(itemRequest2, 1L);

        TypedQuery<ItemRequest> query = em
                .createQuery("Select ir from ItemRequest ir", ItemRequest.class);
        List<ItemRequest> itemRequests = query.getResultList();

        assertThat(itemRequests.size(), equalTo(2));
        assertThat(itemRequests.get(0).getId(), notNullValue());
        assertThat(itemRequests.get(0).getDescription(), equalTo(itemRequest1.getDescription()));
        assertThat(itemRequests.get(1).getRequesterId(), equalTo(itemRequest2.getRequesterId()));
    }

    @Test
    @DirtiesContext
    void testFindById() {
        ItemRequest itemRequest1 = ItemRequest.builder().description("description1").build();
        ItemRequest itemRequest2 = ItemRequest.builder().description("description2").build();
        userService.add(User.builder().name("name1").email("email45@email.ru").build());
        userService.add(User.builder().name("name2").email("email46@email.ru").build());
        itemRequestService.add(itemRequest1, 1L);
        itemRequestService.add(itemRequest2, 2L);

        TypedQuery<ItemRequest> query = em
                .createQuery("Select ir from ItemRequest ir where ir.id = :id", ItemRequest.class);
        ItemRequest itemRequest = query
                .setParameter("id", itemRequest1.getId())
                .getSingleResult();

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequest1.getDescription()));
        assertThat(itemRequest.getRequesterId(), equalTo(itemRequest1.getRequesterId()));
    }
}