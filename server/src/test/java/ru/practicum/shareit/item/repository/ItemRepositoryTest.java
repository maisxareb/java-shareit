package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findByOwnerOrderById_shouldReturnUserItems() {
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

        Item item2 = Item.builder()
                .name("Item 2")
                .description("Description 2")
                .available(true)
                .owner(user.getId())
                .build();

        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.flush();

        List<Item> result = itemRepository.findByOwnerOrderById(user.getId());

        assertEquals(2, result.size());
        assertEquals("Item 1", result.get(0).getName());
        assertEquals("Item 2", result.get(1).getName());
    }

    @Test
    void searchAvailableItemsByText_shouldFindItemsByText() {
        User user = User.builder()
                .name("Test User")
                .email("test@email.com")
                .build();
        entityManager.persist(user);

        Item item1 = Item.builder()
                .name("Дрель аккумуляторная")
                .description("Мощная дрель с аккумулятором")
                .available(true)
                .owner(user.getId())
                .build();

        Item item2 = Item.builder()
                .name("Молоток")
                .description("Строительный молоток")
                .available(true)
                .owner(user.getId())
                .build();

        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.flush();

        List<Item> result = itemRepository.searchAvailableItemsByText("дрель");

        assertEquals(1, result.size());
        assertEquals("Дрель аккумуляторная", result.get(0).getName());
    }

    @Test
    void searchAvailableItemsByText_shouldNotFindUnavailableItems() {
        User user = User.builder()
                .name("Test User")
                .email("test@email.com")
                .build();
        entityManager.persist(user);

        Item item = Item.builder()
                .name("Дрель")
                .description("Мощная дрель")
                .available(false) // Недоступна
                .owner(user.getId())
                .build();

        entityManager.persist(item);
        entityManager.flush();

        List<Item> result = itemRepository.searchAvailableItemsByText("дрель");

        assertTrue(result.isEmpty());
    }

    @Test
    void findByRequestId_shouldReturnItemsForRequest() {
        User user = User.builder()
                .name("Test User")
                .email("test@email.com")
                .build();
        entityManager.persist(user);

        ru.practicum.shareit.request.model.ItemRequest request = ru.practicum.shareit.request.model.ItemRequest.builder()
                .description("Need a drill")
                .requestor(user)
                .created(java.time.LocalDateTime.now())
                .build();
        entityManager.persist(request);

        Item item1 = Item.builder()
                .name("Дрель 1")
                .description("Description 1")
                .available(true)
                .owner(user.getId())
                .request(request)
                .build();

        Item item2 = Item.builder()
                .name("Дрель 2")
                .description("Description 2")
                .available(true)
                .owner(user.getId())
                .request(request)
                .build();

        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.flush();

        List<Item> result = itemRepository.findByRequestId(request.getId());

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(item -> item.getRequest().getId().equals(request.getId())));
    }
}