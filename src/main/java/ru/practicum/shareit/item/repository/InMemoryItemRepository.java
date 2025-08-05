package ru.practicum.shareit.item.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
@Qualifier("InMemoryStorage")
public class InMemoryItemRepository implements ItemRepository {
    //Решил использовать ConcurrentHashMap потому что узнал, что он не на много медленнее чем HashMap и то
    // только в однопоточных сценариях + захотелось по эксперементировать пробовать что то новое для развития
    // как с UUID только в этом случае что то не так у меня пошло
    private final ConcurrentHashMap<Long, Item> items = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public void save(Item item) {
        if (item.getId() == null) {
            item.setId(idCounter.getAndIncrement());
        }
        items.put(item.getId(), item);
    }

    @Override
    public Optional<Item> findItemById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public void update(Item item) {
        items.computeIfPresent(item.getId(), (key, existing) -> {
            existing.setName(item.getName());
            existing.setDescription(item.getDescription());
            existing.setAvailable(item.getAvailable());
            return existing;
        });
    }

    @Override
    public List<Item> findItemsByUserId(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findItemsByTextAndAvailable(String text) {
        String lowerText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> item.getName().toLowerCase().contains(lowerText) ||
                        item.getDescription().toLowerCase().contains(lowerText))
                .collect(Collectors.toList());
    }
}
