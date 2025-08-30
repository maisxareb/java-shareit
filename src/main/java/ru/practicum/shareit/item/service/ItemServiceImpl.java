package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemDto createItem(Long userId, NewItemRequest request) {
        log.info("Создание предмета для пользователя ID {}", userId);
        userService.getUserById(userId);

        Item item = itemMapper.toItem(request);
        item.setOwner(userId);
        validateItem(item);

        item = itemRepository.save(item);
        return enrichItemWithBookingsAndComments(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, UpdateItemRequest request, Long itemId) {
        log.info("Обновление предмета ID {} пользователем ID {}", itemId, userId);
        userService.getUserById(userId);

        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!existingItem.getOwner().equals(userId)) {
            throw new NotFoundException("Недостаточно прав для обновления");
        }

        itemMapper.updateItemFromRequest(request, existingItem);
        itemRepository.save(existingItem);

        return enrichItemWithBookingsAndComments(existingItem);
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        log.info("Получение предмета ID {} пользователем ID {}", itemId, userId);
        userService.getUserById(userId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        return enrichItemWithBookingsAndComments(item);
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        log.info("Получение всех предметов пользователя ID {}", userId);
        userService.getUserById(userId);

        return itemRepository.findByOwnerOrderById(userId).stream()
                .map(this::enrichItemWithBookingsAndComments)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAvailableItems(Long userId, String text) {
        log.info("Поиск доступных предметов по запросу '{}'", text);
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        userService.getUserById(userId);

        return itemRepository.searchAvailableItemsByText(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private ItemDto enrichItemWithBookingsAndComments(Item item) {
        ItemDto itemDto = itemMapper.toItemDto(item);

        LocalDateTime now = LocalDateTime.now();

        bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(
                        item.getId(), BookingStatus.APPROVED, now)
                .ifPresent(booking -> {
                    if (booking.getEnd().isAfter(now)) {
                        ItemDto.BookingInfo lastBooking = ItemDto.BookingInfo.builder()
                                .id(booking.getId())
                                .bookerId(booking.getBooker().getId())
                                .build();
                        itemDto.setLastBooking(lastBooking);
                    }
                });

        bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                        item.getId(), BookingStatus.APPROVED, now)
                .ifPresent(booking -> {
                    ItemDto.BookingInfo nextBooking = ItemDto.BookingInfo.builder()
                            .id(booking.getId())
                            .bookerId(booking.getBooker().getId())
                            .build();
                    itemDto.setNextBooking(nextBooking);
                });

        List<CommentDto> comments = commentRepository.findByItemId(item.getId()).stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
        itemDto.setComments(comments);

        return itemDto;
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        log.info("Добавление комментария к предмету ID {} пользователем ID {}", itemId, userId);

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        boolean hasBooked = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                userId, itemId, BookingStatus.APPROVED, LocalDateTime.now());

        if (!hasBooked) {
            throw new ValidationException("Пользователь не брал эту вещь в аренду");
        }

        Comment comment = commentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        comment = commentRepository.save(comment);
        return commentMapper.toCommentDto(comment);
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
