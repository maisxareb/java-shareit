package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void createRequest_WhenUserExists_ShouldCreateRequest() {
        Long userId = 1L;
        ItemRequestRequest request = new ItemRequestRequest();
        request.setDescription("Test request");

        User user = new User();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("test@email.com");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test request");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequestResponse expectedResponse = ItemRequestResponse.builder()
                .id(1L)
                .description("Test request")
                .created(itemRequest.getCreated())
                .items(Collections.emptyList())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestMapper.toItemRequest(request)).thenReturn(itemRequest);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(itemRepository.findByRequestId(anyLong())).thenReturn(Collections.emptyList());

        when(itemRequestMapper.toItemRequestResponse(itemRequest)).thenReturn(expectedResponse);

        ItemRequestResponse result = itemRequestService.createRequest(userId, request);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
        verify(itemRepository, times(1)).findByRequestId(anyLong());
        verify(itemRequestMapper, times(1)).toItemRequestResponse(itemRequest);
    }

    @Test
    void createRequest_WhenUserNotFound_ShouldThrowNotFoundException() {
        Long userId = 1L;
        ItemRequestRequest request = new ItemRequestRequest();
        request.setDescription("Test request");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemRequestService.createRequest(userId, request));
        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void getUserRequests_WhenUserExists_ShouldReturnRequests() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test request");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        List<ItemRequest> requests = List.of(itemRequest);

        ItemRequestResponse response = ItemRequestResponse.builder()
                .id(1L)
                .description("Test request")
                .created(itemRequest.getCreated())
                .items(Collections.emptyList())
                .build();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId)).thenReturn(requests);
        when(itemRepository.findByRequestId(anyLong())).thenReturn(Collections.emptyList());

        when(itemRequestMapper.toItemRequestResponse(itemRequest)).thenReturn(response);

        List<ItemRequestResponse> result = itemRequestService.getUserRequests(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).existsById(userId);
        verify(itemRequestRepository, times(1)).findByRequestorIdOrderByCreatedDesc(userId);
    }

    @Test
    void getUserRequests_WhenUserNotFound_ShouldThrowNotFoundException() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                itemRequestService.getUserRequests(userId));
        verify(userRepository, times(1)).existsById(userId);
        verify(itemRequestRepository, never()).findByRequestorIdOrderByCreatedDesc(anyLong());
    }

    @Test
    void getOtherUsersRequests_WhenUserExists_ShouldReturnRequests() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;

        User otherUser = new User();
        otherUser.setId(2L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test request");
        itemRequest.setRequestor(otherUser);
        itemRequest.setCreated(LocalDateTime.now());

        List<ItemRequest> requests = List.of(itemRequest);
        Pageable pageable = PageRequest.of(from / size, size);

        ItemRequestResponse expectedResponse = ItemRequestResponse.builder()
                .id(1L)
                .description("Test request")
                .created(itemRequest.getCreated())
                .items(Collections.emptyList())
                .build();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId, pageable))
                .thenReturn(requests);
        when(itemRepository.findByRequestId(anyLong())).thenReturn(Collections.emptyList());

        when(itemRequestMapper.toItemRequestResponse(any(ItemRequest.class))).thenReturn(expectedResponse);

        List<ItemRequestResponse> result = itemRequestService.getOtherUsersRequests(userId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).existsById(userId);
        verify(itemRequestRepository, times(1))
                .findByRequestorIdNotOrderByCreatedDesc(userId, pageable);
        verify(itemRequestMapper, times(1)).toItemRequestResponse(any(ItemRequest.class));
    }

    @Test
    void getOtherUsersRequests_WhenUserNotFound_ShouldThrowNotFoundException() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                itemRequestService.getOtherUsersRequests(userId, from, size));
        verify(userRepository, times(1)).existsById(userId);
        verify(itemRequestRepository, never()).findByRequestorIdNotOrderByCreatedDesc(anyLong(), any());
    }

    @Test
    void getRequestById_WhenUserAndRequestExist_ShouldReturnRequest() {
        Long userId = 1L;
        Long requestId = 1L;

        User user = new User();
        user.setId(userId);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        itemRequest.setDescription("Test request");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequestResponse expectedResponse = ItemRequestResponse.builder()
                .id(requestId)
                .description("Test request")
                .created(itemRequest.getCreated())
                .items(Collections.emptyList())
                .build();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestId(requestId)).thenReturn(Collections.emptyList());

        when(itemRequestMapper.toItemRequestResponse(itemRequest)).thenReturn(expectedResponse);

        ItemRequestResponse result = itemRequestService.getRequestById(userId, requestId);

        assertNotNull(result);
        verify(userRepository, times(1)).existsById(userId);
        verify(itemRequestRepository, times(1)).findById(requestId);
        verify(itemRepository, times(1)).findByRequestId(requestId);
        verify(itemRequestMapper, times(1)).toItemRequestResponse(itemRequest);
    }

    @Test
    void getRequestById_WhenUserNotFound_ShouldThrowNotFoundException() {
        Long userId = 1L;
        Long requestId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                itemRequestService.getRequestById(userId, requestId));
        verify(userRepository, times(1)).existsById(userId);
        verify(itemRequestRepository, never()).findById(anyLong());
    }

    @Test
    void getRequestById_WhenRequestNotFound_ShouldThrowNotFoundException() {
        Long userId = 1L;
        Long requestId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemRequestService.getRequestById(userId, requestId));
        verify(userRepository, times(1)).existsById(userId);
        verify(itemRequestRepository, times(1)).findById(requestId);
    }

    @Test
    void getOtherUsersRequests_WithPagination_ShouldUseCorrectPage() {
        Long userId = 1L;
        Integer from = 10;
        Integer size = 5;

        User otherUser = new User();
        otherUser.setId(2L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test request");
        itemRequest.setRequestor(otherUser);
        itemRequest.setCreated(LocalDateTime.now());

        List<ItemRequest> requests = List.of(itemRequest);
        Pageable pageable = PageRequest.of(from / size, size);

        ItemRequestResponse expectedResponse = ItemRequestResponse.builder()
                .id(1L)
                .description("Test request")
                .created(itemRequest.getCreated())
                .items(Collections.emptyList())
                .build();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId, pageable))
                .thenReturn(requests);
        when(itemRepository.findByRequestId(anyLong())).thenReturn(Collections.emptyList());

        when(itemRequestMapper.toItemRequestResponse(any(ItemRequest.class))).thenReturn(expectedResponse);

        List<ItemRequestResponse> result = itemRequestService.getOtherUsersRequests(userId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(itemRequestRepository, times(1))
                .findByRequestorIdNotOrderByCreatedDesc(userId, pageable);
        verify(itemRequestMapper, times(1)).toItemRequestResponse(any(ItemRequest.class));
    }

    @Test
    void getUserRequests_WhenNoRequests_ShouldReturnEmptyList() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId))
                .thenReturn(Collections.emptyList());

        List<ItemRequestResponse> result = itemRequestService.getUserRequests(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).existsById(userId);
        verify(itemRequestRepository, times(1)).findByRequestorIdOrderByCreatedDesc(userId);
    }

    @Test
    void getOtherUsersRequests_WhenNoRequests_ShouldReturnEmptyList() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(eq(userId), any(Pageable.class)))
                .thenReturn(Collections.emptyList());

        List<ItemRequestResponse> result = itemRequestService.getOtherUsersRequests(userId, from, size);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).existsById(userId);
        verify(itemRequestRepository, times(1))
                .findByRequestorIdNotOrderByCreatedDesc(eq(userId), any(Pageable.class));
    }

    @Test
    void getUserRequests_WithMultipleRequests_ShouldReturnAllRequests() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);

        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);
        request1.setDescription("First request");
        request1.setRequestor(user);
        request1.setCreated(LocalDateTime.now().minusDays(1));

        ItemRequest request2 = new ItemRequest();
        request2.setId(2L);
        request2.setDescription("Second request");
        request2.setRequestor(user);
        request2.setCreated(LocalDateTime.now());

        List<ItemRequest> requests = List.of(request2, request1);

        ItemRequestResponse response1 = ItemRequestResponse.builder()
                .id(1L)
                .description("First request")
                .created(request1.getCreated())
                .items(Collections.emptyList())
                .build();

        ItemRequestResponse response2 = ItemRequestResponse.builder()
                .id(2L)
                .description("Second request")
                .created(request2.getCreated())
                .items(Collections.emptyList())
                .build();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId)).thenReturn(requests);
        when(itemRepository.findByRequestId(anyLong())).thenReturn(Collections.emptyList());

        when(itemRequestMapper.toItemRequestResponse(request1)).thenReturn(response1);
        when(itemRequestMapper.toItemRequestResponse(request2)).thenReturn(response2);

        List<ItemRequestResponse> result = itemRequestService.getUserRequests(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).existsById(userId);
        verify(itemRequestRepository, times(1)).findByRequestorIdOrderByCreatedDesc(userId);

        verify(itemRequestMapper, times(1)).toItemRequestResponse(request1);
        verify(itemRequestMapper, times(1)).toItemRequestResponse(request2);
    }

    @Test
    void createRequest_ShouldCorrectlyMapItems() {
        Long userId = 1L;
        Long requestId = 1L;
        ItemRequestRequest request = new ItemRequestRequest();
        request.setDescription("Test request");

        User user = new User();
        user.setId(userId);
        user.setName("Test User");
        user.setEmail("test@email.com");

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        itemRequest.setDescription("Test request");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        Item item = new Item();
        item.setId(1L);
        item.setName("Test item");
        item.setDescription("Test description");
        item.setAvailable(true);
        item.setOwner(userId);
        item.setRequest(itemRequest);

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Test item")
                .description("Test description")
                .available(true)
                .owner(userId)
                .requestId(requestId)
                .build();

        ItemRequestResponse expectedResponse = ItemRequestResponse.builder()
                .id(requestId)
                .description("Test request")
                .created(itemRequest.getCreated())
                .items(List.of(itemDto))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestMapper.toItemRequest(request)).thenReturn(itemRequest);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(itemRepository.findByRequestId(requestId)).thenReturn(List.of(item));

        when(itemRequestMapper.toItemRequestResponse(itemRequest)).thenReturn(expectedResponse);

        ItemRequestResponse result = itemRequestService.createRequest(userId, request);

        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertEquals("Test item", result.getItems().get(0).getName());
        assertEquals(userId, result.getItems().get(0).getOwner());
        verify(itemRepository, times(1)).findByRequestId(requestId);
        verify(itemRequestMapper, times(1)).toItemRequestResponse(itemRequest);
    }

    @Test
    void getRequestById_ShouldCorrectlyMapItems() {
        Long userId = 1L;
        Long requestId = 1L;

        User user = new User();
        user.setId(userId);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestId);
        itemRequest.setDescription("Test request");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());

        Item item = new Item();
        item.setId(1L);
        item.setName("Test item");
        item.setDescription("Test description");
        item.setAvailable(true);
        item.setOwner(userId);
        item.setRequest(itemRequest);

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Test item")
                .description("Test description")
                .available(true)
                .owner(userId)
                .requestId(requestId)
                .build();

        ItemRequestResponse expectedResponse = ItemRequestResponse.builder()
                .id(requestId)
                .description("Test request")
                .created(itemRequest.getCreated())
                .items(List.of(itemDto))
                .build();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestId(requestId)).thenReturn(List.of(item));

        when(itemRequestMapper.toItemRequestResponse(itemRequest)).thenReturn(expectedResponse);

        ItemRequestResponse result = itemRequestService.getRequestById(userId, requestId);

        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());
        assertEquals("Test item", result.getItems().get(0).getName());
        assertEquals(userId, result.getItems().get(0).getOwner());
        verify(itemRepository, times(1)).findByRequestId(requestId);
        verify(itemRequestMapper, times(1)).toItemRequestResponse(itemRequest);
    }
}