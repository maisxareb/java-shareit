package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

    Optional<Item> findItemById(Long id);

    Item update(Item item);

    List<Item> findItemsByUserId(Long userId);

    List<Item> findItemsByTextAndAvailable(String text);
}