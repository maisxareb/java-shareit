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
    public ItemDto createItem(Long userId, NewItemRequest request) {
        log.info("Создание предмета для пользователя ID {}", userId);
        userService.getUserById(userId);

        Item item = itemMapper.toItem(request);
        item.setOwner(userId);
        validateItem(item);

        itemRepository.save(item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long userId, UpdateItemRequest request, Long itemId) {
        log.info("Обновление предмета ID {} пользователем ID {}", itemId, userId);
        userService.getUserById(userId);

        Item existingItem = itemRepository.findItemById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!existingItem.getOwner().equals(userId)) {
            throw new NotFoundException("Недостаточно прав для обновления");
        }

        itemMapper.updateItemFromRequest(request, existingItem);
        itemRepository.update(existingItem);

        return itemMapper.toItemDto(existingItem);
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        log.info("Получение предмета ID {} пользователем ID {}", itemId, userId);
        userService.getUserById(userId);

        return itemRepository.findItemById(itemId)
                .map(itemMapper::toItemDto)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        log.info("Получение всех предметов пользователя ID {}", userId);
        userService.getUserById(userId);

        return itemRepository.findItemsByUserId(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAvailableItems(Long userId, String text) {
        log.info("Поиск доступных предметов по запросу '{}'", text);
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        userService.getUserById(userId);

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
