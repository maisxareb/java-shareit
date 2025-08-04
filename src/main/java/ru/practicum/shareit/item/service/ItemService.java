package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

public interface ItemService {
    ItemDto saveItem(Long userId, NewItemRequest request);

    ItemDto updateItem(Long userId, UpdateItemRequest request, Long id);

    ItemDto findItemById(Long userId, Long id);

    List<ItemDto> findItemsByUserId(Long userId);

    List<ItemDto> findItemsByTextAndAvailable(Long userId, String text);
}