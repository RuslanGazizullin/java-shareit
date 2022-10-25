package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;

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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceImplTest {

    private final EntityManager em;
    private final UserService userService;

    @Test
    @DirtiesContext
    void testAdd() {
        User user = User.builder().name("name").email("email51@email.ru").build();
        userService.add(user);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user1 = query
                .setParameter("email", user.getEmail())
                .getSingleResult();

        assertThat(user1.getId(), notNullValue());
        assertThat(user1.getId(), equalTo(1L));
        assertThat(user1.getName(), equalTo(user.getName()));
        assertThat(user1.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    @DirtiesContext
    void testUpdate() {
        User user = User.builder().name("name").email("email52@email.ru").build();
        Long id = userService.add(user).getId();
        User updatedUser = User.builder().name("updatedName").email("updatedEmail@email.ru").build();
        userService.update(updatedUser, id);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User result = query
                .setParameter("email", updatedUser.getEmail())
                .getSingleResult();

        assertThat(result.getId(), notNullValue());
        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getName(), equalTo(updatedUser.getName()));
        assertThat(result.getEmail(), equalTo(updatedUser.getEmail()));
    }

    @Test
    @DirtiesContext
    void testFindAll() {
        User user = User.builder().name("name").email("email53@email.ru").build();
        userService.add(user);

        TypedQuery<User> query = em.createQuery("Select u from User u", User.class);
        List<User> users = query.getResultList();

        assertThat(users.size(), equalTo(1));
        assertThat(users.get(0).getId(), notNullValue());
        assertThat(users.get(0).getName(), equalTo(user.getName()));
        assertThat(users.get(0).getEmail(), equalTo(user.getEmail()));
    }

    @Test
    @DirtiesContext
    void testFindById() {
        User user = User.builder().name("name").email("email54@email.ru").build();
        userService.add(user);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user1 = query
                .setParameter("id", 1L)
                .getSingleResult();

        assertThat(user1.getId(), notNullValue());
        assertThat(user1.getName(), equalTo(user.getName()));
        assertThat(user1.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    @DirtiesContext
    void testDelete() {
        User user1 = User.builder().name("name1").email("email55@email.ru").build();
        userService.add(user1);
        User user2 = User.builder().name("name2").email("email56@email.ru").build();
        userService.add(user2);
        userService.delete(user1.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u", User.class);
        List<User> users = query.getResultList();

        assertThat(users.size(), equalTo(1));
        assertThat(users.get(0).getId(), notNullValue());
        assertThat(users.get(0).getName(), equalTo(user2.getName()));
        assertThat(users.get(0).getEmail(), equalTo(user2.getEmail()));
    }
}