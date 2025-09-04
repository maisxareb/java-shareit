package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@Import(ItemServiceImpl.class)
class ItemServiceImplIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemServiceImpl itemService;

    @MockBean
    private ItemMapper itemMapper;

    @Test
    void getUserItems_shouldReturnUserItems() {
        User user = User.builder()
                .name("Тестовый пользователь")
                .email("test@email.com")
                .build();
        entityManager.persist(user);
        entityManager.flush();

        Item item1 = Item.builder()
                .name("Предмет 1")
                .description("Описание 1")
                .available(true)
                .owner(user.getId())
                .build();

        Item item2 = Item.builder()
                .name("Предмет 2")
                .description("Описание 2")
                .available(true)
                .owner(user.getId())
                .build();

        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.flush();

        when(itemMapper.toItemDto(any(Item.class))).thenAnswer(invocation -> {
            Item item = invocation.getArgument(0);
            return ItemDto.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .description(item.getDescription())
                    .available(item.getAvailable())
                    .build();
        });

        var result = itemService.getUserItems(user.getId());

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("Предмет 1", result.get(0).getName());
        Assertions.assertEquals("Предмет 2", result.get(1).getName());
    }
}