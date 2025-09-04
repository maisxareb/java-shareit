package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestTest {

    @Test
    void testItemRequestBuilder() {
        LocalDateTime created = LocalDateTime.now();
        User requestor = User.builder().id(1L).build();

        ItemRequest request = ItemRequest.builder()
                .id(1L)
                .description("Test Description")
                .requestor(requestor)
                .created(created)
                .build();

        assertNotNull(request);
        assertEquals(1L, request.getId());
        assertEquals("Test Description", request.getDescription());
        assertEquals(requestor, request.getRequestor());
        assertEquals(created, request.getCreated());
    }

    @Test
    void testEquals_SameId_ShouldReturnTrue() {
        ItemRequest request1 = ItemRequest.builder().id(1L).build();
        ItemRequest request2 = ItemRequest.builder().id(1L).build();

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void testEquals_DifferentId_ShouldReturnFalse() {
        ItemRequest request1 = ItemRequest.builder().id(1L).build();
        ItemRequest request2 = ItemRequest.builder().id(2L).build();

        assertNotEquals(request1, request2);
    }

    @Test
    void testEquals_Null_ShouldReturnFalse() {
        ItemRequest request = ItemRequest.builder().id(1L).build();

        assertNotEquals(null, request);
    }

    @Test
    void testEquals_DifferentClass_ShouldReturnFalse() {
        ItemRequest request = ItemRequest.builder().id(1L).build();
        Object other = new Object();

        assertNotEquals(request, other);
    }

    @Test
    void testSetters() {
        ItemRequest request = new ItemRequest();
        User requestor = User.builder().id(1L).build();
        LocalDateTime created = LocalDateTime.now();

        request.setId(1L);
        request.setDescription("New Description");
        request.setRequestor(requestor);
        request.setCreated(created);

        assertEquals(1L, request.getId());
        assertEquals("New Description", request.getDescription());
        assertEquals(requestor, request.getRequestor());
        assertEquals(created, request.getCreated());
    }

    @Test
    void testNoArgsConstructor() {
        ItemRequest request = new ItemRequest();

        assertNotNull(request);
        assertNull(request.getId());
        assertNull(request.getDescription());
        assertNull(request.getRequestor());
        assertNull(request.getCreated());
    }
}