package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto saveItem(Long userId, NewItemRequest request) {
        userService.findUserById(userId);

        Item item = itemMapper.toItem(request);
        item.setOwner(userId);

        validateItem(item);

        Item savedItem = itemRepository.save(item);
        return itemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(Long userId, UpdateItemRequest request, Long itemId) {
        userService.findUserById(userId);

        Item existingItem = itemRepository.findItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!existingItem.getOwner().equals(userId)) {
            throw new NotFoundException("Недостаточно прав для обновления");
        }

        Item updatedItem = itemMapper.updateItemFields(existingItem, request);
        updatedItem = itemRepository.update(updatedItem);

        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto findItemById(Long userId, Long itemId) {
        userService.findUserById(userId);
        return itemRepository.findItemById(itemId)
                .map(itemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }

    @Override
    public List<ItemDto> findItemsByUserId(Long userId) {
        userService.findUserById(userId);
        return itemRepository.findItemsByUserId(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findItemsByTextAndAvailable(Long userId, String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        userService.findUserById(userId);
        return itemRepository.findItemsByTextAndAvailable(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void validateItem(Item item) {
        if (item.getAvailable() == null) {
            throw new ValidationException("available", "Поле available обязательно");
        }
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("name", "Название не может быть пустым");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("description", "Описание не может быть пустым");
        }
    }
}