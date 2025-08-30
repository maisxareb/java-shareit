package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @Valid @RequestBody NewItemRequest request) {
        log.info("Создание предмета для пользователя ID {}. Данные: {}", userId, request);
        return itemService.createItem(userId, request);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @PathVariable("itemId") Long itemId,
            @Valid @RequestBody UpdateItemRequest request) {
        log.info("Обновление предмета ID {} пользователем ID {}", itemId, userId);
        return itemService.updateItem(userId, request, itemId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItemById(
            @PathVariable("itemId") Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение предмета ID {} пользователем ID {}", itemId, userId);
        return itemService.getItemById(userId, itemId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getUserItems(
            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получение всех предметов пользователя ID {}", userId);
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> searchItems(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "text", required = false) String text) {
        log.info("Поиск предметов по тексту '{}' для пользователя ID {}", text, userId);
        return itemService.searchAvailableItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto addComment(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("itemId") Long itemId,
            @Valid @RequestBody CommentDto commentDto) {
        log.info("Добавление комментария к предмету ID {} пользователем ID {}", itemId, userId);
        return itemService.addComment(userId, itemId, commentDto);
    }
}