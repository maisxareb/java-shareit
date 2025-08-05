package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    void save(Item item);

    Optional<Item> findItemById(Long id);

    void update(Item item);

    List<Item> findItemsByUserId(Long userId);

    List<Item> findItemsByTextAndAvailable(String text);
}
