package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void findByItemId_shouldReturnCommentsForItem() {
        User user = User.builder()
                .name("Test User")
                .email("test@email.com")
                .build();
        entityManager.persist(user);

        Item item = Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(user.getId())
                .build();
        entityManager.persist(item);

        Comment comment1 = Comment.builder()
                .text("Comment 1")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        Comment comment2 = Comment.builder()
                .text("Comment 2")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        entityManager.persist(comment1);
        entityManager.persist(comment2);
        entityManager.flush();

        List<Comment> result = commentRepository.findByItemId(item.getId());

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(c -> c.getText().equals("Comment 1")));
        assertTrue(result.stream().anyMatch(c -> c.getText().equals("Comment 2")));
    }

    @Test
    void findByItemIdIn_shouldReturnCommentsForMultipleItems() {
        User user = User.builder()
                .name("Test User")
                .email("test@email.com")
                .build();
        entityManager.persist(user);

        Item item1 = Item.builder()
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .owner(user.getId())
                .build();
        entityManager.persist(item1);

        Item item2 = Item.builder()
                .name("Item 2")
                .description("Description 2")
                .available(true)
                .owner(user.getId())
                .build();
        entityManager.persist(item2);

        Comment comment1 = Comment.builder()
                .text("Comment for item 1")
                .item(item1)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        Comment comment2 = Comment.builder()
                .text("Comment for item 2")
                .item(item2)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        entityManager.persist(comment1);
        entityManager.persist(comment2);
        entityManager.flush();

        List<Comment> result = commentRepository.findByItemIdIn(List.of(item1.getId(), item2.getId()));

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(c -> c.getText().equals("Comment for item 1")));
        assertTrue(result.stream().anyMatch(c -> c.getText().equals("Comment for item 2")));
    }

    @Test
    void findByItemId_shouldReturnEmptyListForNonExistentItem() {
        List<Comment> result = commentRepository.findByItemId(999L);

        assertTrue(result.isEmpty());
    }
}