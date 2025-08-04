package ru.practicum.shareit.item.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
@Qualifier("InMemoryStorage")
public class InMemoryItemRepository implements ItemRepository {
    private final List<Item> items = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(idCounter.getAndIncrement());
        }
        items.add(item);
        return item;
    }

    @Override
    public Optional<Item> findItemById(Long id) {
        return items.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst();
    }

    @Override
    public Item update(Item item) {
        Optional<Item> existingOpt = findItemById(item.getId());
        if (existingOpt.isPresent()) {
            Item existing = existingOpt.get();
            existing.setName(item.getName());
            existing.setDescription(item.getDescription());
            existing.setAvailable(item.getAvailable());
            return existing;
        }
        return null;
    }

    @Override
    public List<Item> findItemsByUserId(Long userId) {
        return items.stream()
                .filter(item -> item.getOwner().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findItemsByTextAndAvailable(String text) {
        String lowerText = text.toLowerCase();
        return items.stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> item.getName().toLowerCase().contains(lowerText) ||
                        item.getDescription().toLowerCase().contains(lowerText))
                .collect(Collectors.toList());
    }
}
