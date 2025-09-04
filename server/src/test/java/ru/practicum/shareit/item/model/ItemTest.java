package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.model.ItemRequest;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void testItemBuilder() {
        ItemRequest request = ItemRequest.builder().id(1L).build();

        Item item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(1L)
                .request(request)
                .build();

        assertNotNull(item);
        assertEquals(1L, item.getId());
        assertEquals("Test Item", item.getName());
        assertEquals("Test Description", item.getDescription());
        assertTrue(item.getAvailable());
        assertEquals(1L, item.getOwner());
        assertEquals(request, item.getRequest());
    }

    @Test
    void testEquals_SameId_ShouldReturnTrue() {
        Item item1 = Item.builder().id(1L).build();
        Item item2 = Item.builder().id(1L).build();

        assertEquals(item1, item2);
        assertEquals(item1.hashCode(), item2.hashCode());
    }

    @Test
    void testEquals_DifferentId_ShouldReturnFalse() {
        Item item1 = Item.builder().id(1L).build();
        Item item2 = Item.builder().id(2L).build();

        assertNotEquals(item1, item2);
    }

    @Test
    void testEquals_Null_ShouldReturnFalse() {
        Item item = Item.builder().id(1L).build();

        assertNotEquals(null, item);
    }

    @Test
    void testEquals_DifferentClass_ShouldReturnFalse() {
        Item item = Item.builder().id(1L).build();
        Object other = new Object();

        assertNotEquals(item, other);
    }

    @Test
    void testSetters() {
        Item item = new Item();
        ItemRequest request = ItemRequest.builder().id(1L).build();

        item.setId(1L);
        item.setName("New Name");
        item.setDescription("New Description");
        item.setAvailable(false);
        item.setOwner(2L);
        item.setRequest(request);

        assertEquals(1L, item.getId());
        assertEquals("New Name", item.getName());
        assertEquals("New Description", item.getDescription());
        assertFalse(item.getAvailable());
        assertEquals(2L, item.getOwner());
        assertEquals(request, item.getRequest());
    }

    @Test
    void testNoArgsConstructor() {
        Item item = new Item();

        assertNotNull(item);
        assertNull(item.getId());
        assertNull(item.getName());
        assertNull(item.getDescription());
        assertNull(item.getAvailable());
        assertNull(item.getOwner());
        assertNull(item.getRequest());
    }
}