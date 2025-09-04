package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemDto createItem(Long userId, NewItemRequest request) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        ItemRequest itemRequest = null;
        if (request.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(request.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Request not found"));
        }

        Item item = itemMapper.toItem(request);
        item.setOwner(userId);
        item.setRequest(itemRequest);

        item = itemRepository.save(item);
        return itemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, UpdateItemRequest request, Long itemId) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!existingItem.getOwner().equals(userId)) {
            throw new NotFoundException("Only owner can update item");
        }

        itemMapper.updateItemFromRequest(request, existingItem);
        existingItem = itemRepository.save(existingItem);
        return itemMapper.toItemDto(existingItem);
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        ItemDto itemDto = itemMapper.toItemDto(item);
        enrichItemDtoWithBookingsAndComments(itemDto, item, userId);

        return itemDto;
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        return itemRepository.findByOwnerOrderById(userId).stream()
                .map(item -> {
                    ItemDto itemDto = itemMapper.toItemDto(item);
                    enrichItemDtoWithBookingsAndComments(itemDto, item, userId);
                    return itemDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAvailableItems(Long userId, String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return itemRepository.searchAvailableItemsByText(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        List<Booking> userBookings = bookingRepository.findByBookerIdAndItemIdAndStatusOrderByStartDesc(
                userId, itemId, BookingStatus.APPROVED);

        boolean hasCompletedBooking = false;
        for (Booking booking : userBookings) {
            if (booking.getEnd().isBefore(LocalDateTime.now())) {
                hasCompletedBooking = true;
                break;
            }
        }

        if (!hasCompletedBooking) {
            throw new ValidationException("User has not completed any approved booking for this item");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        comment = commentRepository.save(comment);

        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    private void enrichItemDtoWithBookingsAndComments(ItemDto itemDto, Item item, Long userId) {
        if (item.getOwner().equals(userId)) {
            LocalDateTime now = LocalDateTime.now();

            Optional<Booking> lastBooking = bookingRepository
                    .findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(
                            item.getId(), BookingStatus.APPROVED, now);

            lastBooking.ifPresent(booking -> {
                ItemDto.BookingInfo lastBookingInfo = ItemDto.BookingInfo.builder()
                        .id(booking.getId())
                        .bookerId(booking.getBooker().getId())
                        .build();
                itemDto.setLastBooking(lastBookingInfo);
            });

            Optional<Booking> nextBooking = bookingRepository
                    .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                            item.getId(), BookingStatus.APPROVED, now);

            nextBooking.ifPresent(booking -> {
                ItemDto.BookingInfo nextBookingInfo = ItemDto.BookingInfo.builder()
                        .id(booking.getId())
                        .bookerId(booking.getBooker().getId())
                        .build();
                itemDto.setNextBooking(nextBookingInfo);
            });
        }

        List<CommentDto> comments = commentRepository.findByItemId(item.getId()).stream()
                .map(comment -> CommentDto.builder()
                        .id(comment.getId())
                        .text(comment.getText())
                        .authorName(comment.getAuthor().getName())
                        .created(comment.getCreated())
                        .build())
                .collect(Collectors.toList());

        itemDto.setComments(comments);
    }
}