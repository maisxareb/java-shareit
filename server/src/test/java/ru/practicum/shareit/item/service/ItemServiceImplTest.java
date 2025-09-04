package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
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
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void createItem_shouldCreateItemSuccessfully() {
        Long userId = 1L;
        NewItemRequest newItemRequest = new NewItemRequest();
        newItemRequest.setName("Дрель");
        newItemRequest.setDescription("Аккумуляторная дрель");
        newItemRequest.setAvailable(true);

        User owner = User.builder().id(userId).build();
        Item item = Item.builder().id(1L).owner(userId).build();
        ItemDto responseDto = ItemDto.builder().id(1L).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemMapper.toItem(any(NewItemRequest.class))).thenReturn(item);
        when(itemRepository.save(any())).thenReturn(item);
        when(itemMapper.toItemDto(any())).thenReturn(responseDto);

        ItemDto result = itemService.createItem(userId, newItemRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(itemRepository).save(any());
    }

    @Test
    void createItem_shouldThrowWhenUserNotFound() {
        Long userId = 1L;
        NewItemRequest newItemRequest = new NewItemRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(userId, newItemRequest));
    }

    @Test
    void createItem_shouldCreateItemWithRequest() {
        Long userId = 1L;
        Long requestId = 1L;
        NewItemRequest newItemRequest = new NewItemRequest();
        newItemRequest.setName("Дрель");
        newItemRequest.setRequestId(requestId);

        User owner = User.builder().id(userId).build();
        ItemRequest itemRequest = ItemRequest.builder().id(requestId).build();
        Item item = Item.builder().id(1L).owner(userId).request(itemRequest).build();
        ItemDto responseDto = ItemDto.builder().id(1L).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemMapper.toItem(any(NewItemRequest.class))).thenReturn(item);
        when(itemRepository.save(any())).thenReturn(item);
        when(itemMapper.toItemDto(any())).thenReturn(responseDto);

        ItemDto result = itemService.createItem(userId, newItemRequest);

        assertNotNull(result);
        verify(itemRequestRepository).findById(requestId);
    }

    @Test
    void updateItem_shouldUpdateItemSuccessfully() {
        Long userId = 1L;
        Long itemId = 1L;
        UpdateItemRequest updateItemRequest = new UpdateItemRequest();
        updateItemRequest.setName("Обновленная дрель");
        updateItemRequest.setDescription("Новое описание");
        updateItemRequest.setAvailable(false);

        Item existingItem = Item.builder()
                .id(itemId)
                .name("Старая дрель")
                .description("Старое описание")
                .available(true)
                .owner(userId)
                .build();

        Item updatedItem = Item.builder()
                .id(itemId)
                .name("Обновленная дрель")
                .description("Новое описание")
                .available(false)
                .owner(userId)
                .build();

        ItemDto responseDto = ItemDto.builder().id(itemId).build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any())).thenReturn(updatedItem);
        when(itemMapper.toItemDto(any())).thenReturn(responseDto);

        ItemDto result = itemService.updateItem(userId, updateItemRequest, itemId);

        assertNotNull(result);
        verify(itemRepository).save(any());
    }

    @Test
    void updateItem_shouldThrowWhenUserNotOwner() {
        Long userId = 1L;
        Long itemId = 1L;
        UpdateItemRequest updateItemRequest = new UpdateItemRequest();

        Item item = Item.builder().id(itemId).owner(2L).build(); // Владелец с ID 2

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> itemService.updateItem(userId, updateItemRequest, itemId));

        verify(userRepository, never()).findById(any());
    }

    @Test
    void getItemById_shouldReturnItemWithBookingsAndComments() {
        Long userId = 1L;
        Long itemId = 1L;

        User owner = User.builder().id(userId).build();
        Item item = Item.builder().id(itemId).owner(userId).build();
        Booking lastBooking = Booking.builder().id(1L).booker(owner).build();
        Booking nextBooking = Booking.builder().id(2L).booker(owner).build();
        Comment comment = Comment.builder().id(1L).author(owner).build();
        ItemDto itemDto = ItemDto.builder().id(itemId).build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto); // Добавлен мок для toItemDto
        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(
                eq(itemId), eq(BookingStatus.APPROVED), any(LocalDateTime.class))).thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                eq(itemId), eq(BookingStatus.APPROVED), any(LocalDateTime.class))).thenReturn(Optional.of(nextBooking));
        when(commentRepository.findByItemId(itemId)).thenReturn(List.of(comment));

        ItemDto result = itemService.getItemById(userId, itemId);

        assertNotNull(result);
        verify(bookingRepository).findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(any(), any(), any());
        verify(bookingRepository).findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(any(), any(), any());
    }

    @Test
    void getItemById_shouldReturnItemWithoutBookingsForNonOwner() {
        Long userId = 2L;
        Long itemId = 1L;

        User owner = User.builder().id(1L).build();
        Item item = Item.builder().id(itemId).owner(1L).build();
        ItemDto itemDto = ItemDto.builder().id(itemId).build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto); // Добавлен мок для toItemDto

        ItemDto result = itemService.getItemById(userId, itemId);

        assertNotNull(result);
        verify(bookingRepository, never()).findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(any(), any(), any());
        verify(bookingRepository, never()).findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(any(), any(), any());
    }

    @Test
    void getUserItems_shouldReturnUserItems() {
        Long userId = 1L;

        User owner = User.builder().id(userId).build();
        Item item = Item.builder().id(1L).owner(userId).build();
        ItemDto itemDto = ItemDto.builder().id(1L).build();

        Booking lastBooking = Booking.builder()
                .id(1L)
                .booker(owner)
                .build();
        Booking nextBooking = Booking.builder()
                .id(2L)
                .booker(owner)
                .build();

        when(itemRepository.findByOwnerOrderById(userId)).thenReturn(List.of(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);
        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(
                any(), any(), any(LocalDateTime.class))).thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                any(), any(), any(LocalDateTime.class))).thenReturn(Optional.of(nextBooking));

        List<ItemDto> result = itemService.getUserItems(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void searchAvailableItems_shouldReturnAvailableItems() {
        Long userId = 1L;
        String text = "дрель";

        Item item = Item.builder().id(1L).available(true).owner(1L).build();
        ItemDto itemDto = ItemDto.builder().id(1L).build();

        when(itemRepository.searchAvailableItemsByText(text)).thenReturn(List.of(item));
        when(itemMapper.toItemDto(item)).thenReturn(itemDto);

        List<ItemDto> result = itemService.searchAvailableItems(userId, text);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void searchAvailableItems_shouldReturnEmptyListWhenTextBlank() {
        Long userId = 1L;
        String text = " ";

        List<ItemDto> result = itemService.searchAvailableItems(userId, text);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void addComment_shouldAddCommentSuccessfully() {
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto commentDto = CommentDto.builder()
                .text("Отличная дрель!")
                .build();

        User author = User.builder().id(userId).name("Автор").build();
        Item item = Item.builder().id(itemId).owner(2L).build();

        // Создаем завершенное бронирование
        Booking completedBooking = Booking.builder()
                .id(1L)
                .booker(author)
                .item(item)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1)) // Завершено (в прошлом)
                .build();

        Comment savedComment = Comment.builder()
                .id(1L)
                .text("Отличная дрель!")
                .author(author)
                .item(item)
                .created(LocalDateTime.now())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        when(bookingRepository.findByBookerIdAndItemIdAndStatusOrderByStartDesc(
                eq(userId), eq(itemId), eq(BookingStatus.APPROVED)))
                .thenReturn(List.of(completedBooking)); // Возвращаем список с завершенным бронированием

        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentDto result = itemService.addComment(userId, itemId, commentDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Отличная дрель!", result.getText());
        assertEquals("Автор", result.getAuthorName());
        verify(commentRepository).save(any(Comment.class));
        verify(commentMapper, never()).toCommentDto(any());
    }

    @Test
    void addComment_shouldThrowWhenUserNotBookedItem() {
        Long userId = 1L;
        Long itemId = 1L;
        CommentDto commentDto = CommentDto.builder().text("Комментарий").build();

        User author = User.builder().id(userId).build();
        Item item = Item.builder().id(itemId).owner(2L).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(author));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        when(bookingRepository.findByBookerIdAndItemIdAndStatusOrderByStartDesc(
                userId, itemId, BookingStatus.APPROVED))
                .thenReturn(List.of());

        assertThrows(ValidationException.class, () -> itemService.addComment(userId, itemId, commentDto));
    }

    @Test
    void getItemById_shouldThrowWhenItemNotFound() {
        Long userId = 1L;
        Long itemId = 1L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(userId, itemId));
    }

    @Test
    void updateItem_shouldThrowWhenItemNotFound() {
        Long userId = 1L;
        Long itemId = 1L;
        UpdateItemRequest updateItemRequest = new UpdateItemRequest();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.updateItem(userId, updateItemRequest, itemId));

        verify(userRepository, never()).findById(any());
    }

    @Test
    void createItem_shouldThrowWhenRequestNotFound() {
        Long userId = 1L;
        Long requestId = 999L;
        NewItemRequest newItemRequest = new NewItemRequest();
        newItemRequest.setName("Дрель");
        newItemRequest.setRequestId(requestId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(userId, newItemRequest));
    }
}