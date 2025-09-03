package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

public interface ItemService {
    ItemDto createItem(Long userId, NewItemRequest request);

    ItemDto updateItem(Long userId, UpdateItemRequest request, Long itemId);

    ItemDto getItemById(Long userId, Long itemId);

    List<ItemDto> getUserItems(Long userId);

    List<ItemDto> searchAvailableItems(Long userId, String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}