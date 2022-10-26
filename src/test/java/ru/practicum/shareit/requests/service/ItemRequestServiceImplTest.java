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
import javax.transaction.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestServiceImplTest {

    private final EntityManager em;
    private final ItemRequestService itemRequestService;
    private final UserService userService;

    @Test
    void testAdd() {
        ItemRequest itemRequest = ItemRequest.builder().description("description").build();
        Long userId = userService.add(User.builder().name("name").email("email41@email.ru").build()).getId();
        Long requestId = itemRequestService.add(itemRequest, userId).getId();

        TypedQuery<ItemRequest> query = em
                .createQuery("Select ir from ItemRequest ir where ir.id = :id", ItemRequest.class);
        ItemRequest itemRequest1 = query
                .setParameter("id", requestId)
                .getSingleResult();

        assertThat(itemRequest1.getId(), notNullValue());
        assertThat(itemRequest1.getId(), equalTo(requestId));
        assertThat(itemRequest1.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(itemRequest1.getRequesterId(), equalTo(itemRequest.getRequesterId()));
    }

    @Test
    void testFindAllByRequester() {
        ItemRequest itemRequest1 = ItemRequest.builder().description("description1").build();
        ItemRequest itemRequest2 = ItemRequest.builder().description("description2").build();
        Long user1Id = userService.add(User.builder().name("name1").email("email43@email.ru").build()).getId();
        Long user2Id = userService.add(User.builder().name("name2").email("email42@email.ru").build()).getId();
        Long requestId = itemRequestService.add(itemRequest1, user1Id).getId();
        itemRequestService.add(itemRequest2, user2Id);

        TypedQuery<ItemRequest> query = em
                .createQuery("Select ir from ItemRequest ir where ir.requesterId = :requesterId", ItemRequest.class);
        List<ItemRequest> itemRequests = query
                .setParameter("requesterId", itemRequest1.getRequesterId())
                .getResultList();

        assertThat(itemRequests.size(), equalTo(1));
        assertThat(itemRequests.get(0).getId(), notNullValue());
        assertThat(itemRequests.get(0).getId(), equalTo(requestId));
        assertThat(itemRequests.get(0).getDescription(), equalTo(itemRequest1.getDescription()));
        assertThat(itemRequests.get(0).getRequesterId(), equalTo(itemRequest1.getRequesterId()));
    }

    @Test
    void testFindAll() {
        ItemRequest itemRequest1 = ItemRequest.builder().description("description1").build();
        ItemRequest itemRequest2 = ItemRequest.builder().description("description2").build();
        Long userId = userService.add(User.builder().name("name").email("email44@email.ru").build()).getId();
        Long request1Id = itemRequestService.add(itemRequest1, userId).getId();
        Long request2Id = itemRequestService.add(itemRequest2, userId).getId();

        TypedQuery<ItemRequest> query = em
                .createQuery("Select ir from ItemRequest ir", ItemRequest.class);
        List<ItemRequest> itemRequests = query.getResultList();

        assertThat(itemRequests.size(), equalTo(2));
        assertThat(itemRequests.get(0).getId(), notNullValue());
        assertThat(itemRequests.get(1).getId(), notNullValue());
        assertThat(itemRequests.get(0).getId(), equalTo(request1Id));
        assertThat(itemRequests.get(1).getId(), equalTo(request2Id));
        assertThat(itemRequests.get(0).getDescription(), equalTo(itemRequest1.getDescription()));
        assertThat(itemRequests.get(1).getRequesterId(), equalTo(itemRequest2.getRequesterId()));
    }

    @Test
    void testFindById() {
        ItemRequest itemRequest1 = ItemRequest.builder().description("description1").build();
        ItemRequest itemRequest2 = ItemRequest.builder().description("description2").build();
        Long user1Id = userService.add(User.builder().name("name1").email("email45@email.ru").build()).getId();
        Long user2Id = userService.add(User.builder().name("name2").email("email46@email.ru").build()).getId();
        Long request1Id = itemRequestService.add(itemRequest1, user1Id).getId();
        itemRequestService.add(itemRequest2, user2Id);

        TypedQuery<ItemRequest> query = em
                .createQuery("Select ir from ItemRequest ir where ir.id = :id", ItemRequest.class);
        ItemRequest itemRequest = query
                .setParameter("id", request1Id)
                .getSingleResult();

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getId(), equalTo(request1Id));
        assertThat(itemRequest.getDescription(), equalTo(itemRequest1.getDescription()));
        assertThat(itemRequest.getRequesterId(), equalTo(itemRequest1.getRequesterId()));
    }
}