package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto.BookingInfo;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    @Test
    void itemDtoBuilder_shouldWorkCorrectly() {
        BookingInfo lastBooking = BookingInfo.builder().id(1L).bookerId(2L).build();
        BookingInfo nextBooking = BookingInfo.builder().id(3L).bookerId(4L).build();
        CommentDto comment = CommentDto.builder().id(1L).text("Comment").build();

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(5L)
                .requestId(6L)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(List.of(comment))
                .build();

        assertNotNull(itemDto);
        assertEquals(1L, itemDto.getId());
        assertEquals("Test Item", itemDto.getName());
        assertEquals("Test Description", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertEquals(5L, itemDto.getOwner());
        assertEquals(6L, itemDto.getRequestId());
        assertEquals(lastBooking, itemDto.getLastBooking());
        assertEquals(nextBooking, itemDto.getNextBooking());
        assertEquals(1, itemDto.getComments().size());
    }

    @Test
    void commentDtoBuilder_shouldWorkCorrectly() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Test Comment")
                .authorName("Author")
                .build();

        assertNotNull(commentDto);
        assertEquals(1L, commentDto.getId());
        assertEquals("Test Comment", commentDto.getText());
        assertEquals("Author", commentDto.getAuthorName());
        assertNull(commentDto.getCreated());
    }

    @Test
    void newItemRequest_shouldHaveSettersAndGetters() {
        NewItemRequest request = new NewItemRequest();
        request.setName("Test");
        request.setDescription("Desc");
        request.setAvailable(true);
        request.setRequestId(1L);

        assertEquals("Test", request.getName());
        assertEquals("Desc", request.getDescription());
        assertTrue(request.getAvailable());
        assertEquals(1L, request.getRequestId());
    }

    @Test
    void updateItemRequest_shouldHaveSettersAndGetters() {
        UpdateItemRequest request = new UpdateItemRequest();
        request.setName("Test");
        request.setDescription("Desc");
        request.setAvailable(false);

        assertEquals("Test", request.getName());
        assertEquals("Desc", request.getDescription());
        assertFalse(request.getAvailable());
    }

    @Test
    void bookingInfo_shouldWorkCorrectly() {
        BookingInfo bookingInfo = BookingInfo.builder()
                .id(1L)
                .bookerId(2L)
                .build();

        assertNotNull(bookingInfo);
        assertEquals(1L, bookingInfo.getId());
        assertEquals(2L, bookingInfo.getBookerId());
    }
}