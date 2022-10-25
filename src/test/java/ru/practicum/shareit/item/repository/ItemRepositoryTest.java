package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    private final List<Long> itemIds = new ArrayList<>();
    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private Item item3;
    private List<Long> resultIds;

    @BeforeEach
    void beforeEach() {
        user1 = userRepository.save(new User(1L, "name", "email1@email.ru"));
        item1 = itemRepository.save(new Item(1L, "nameText", "description", true, user1.getId(), null));
        item2 = itemRepository.save(new Item(2L, "name", "description", true, user1.getId(), null));
        user2 = userRepository.save(new User(2L, "name", "email2@email.ru"));
        item3 = itemRepository.save(new Item(3L, "name", "descriptionText", true, user2.getId(), null));
        itemIds.add(item1.getId());
        itemIds.add(item2.getId());
        resultIds = new ArrayList<>();
    }

    @Test
    void findAllIdByOwnerNoItems() {
        resultIds = itemRepository.findAllIdByOwner(3L);
        assertEquals(resultIds.size(), 0);
    }

    @Test
    void findAllIdByOwner() {
        resultIds = itemRepository.findAllIdByOwner(user1.getId());
        assertEquals(resultIds.size(), 2);
        assertEquals(resultIds.get(0), item1.getId());
        assertEquals(resultIds.get(1), item2.getId());
        assertEquals(resultIds, itemIds);
    }

    @Test
    void findByText() {
        final Page<Item> resultItems = itemRepository.findByText("text", Pageable.unpaged());
        assertEquals(resultItems.getTotalElements(), 2);
        assertEquals(resultItems.toList().get(0), item1);
        assertEquals(resultItems.toList().get(1), item3);
    }

    @Test
    void findByTextNotFindText() {
        final Page<Item> resultItems = itemRepository.findByText("text1", Pageable.unpaged());
        assertEquals(resultItems.getTotalElements(), 0);
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}