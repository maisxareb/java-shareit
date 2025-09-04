package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Test
    void toItem_shouldMapNewItemRequestToItemIgnoringCertainFields() {
        NewItemRequest request = new NewItemRequest();
        request.setName("Аккумуляторная дрель");
        request.setDescription("Беспроводная аккумуляторная дрель с батареей");
        request.setAvailable(true);
        request.setRequestId(5L);

        Item item = itemMapper.toItem(request);

        assertNotNull(item);
        assertEquals("Аккумуляторная дрель", item.getName());
        assertEquals("Беспроводная аккумуляторная дрель с батареей", item.getDescription());
        assertTrue(item.getAvailable());

        assertNull(item.getId());
        assertNull(item.getOwner());
        assertNull(item.getRequest());
    }

    @Test
    void toItem_shouldHandleNullNewItemRequest() {
        Item item = itemMapper.toItem(null);

        assertNull(item);
    }

    @Test
    void toItem_shouldHandleNewItemRequestWithNullFields() {
        NewItemRequest request = new NewItemRequest(); // Все поля null

        Item item = itemMapper.toItem(request);

        assertNotNull(item);
        assertNull(item.getName());
        assertNull(item.getDescription());
        assertNull(item.getAvailable());
        assertNull(item.getId());
        assertNull(item.getOwner());
        assertNull(item.getRequest());
    }

    @Test
    void toItemDto_shouldMapItemToItemDtoWithRequestId() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(10L)
                .description("Нужен электроинструмент")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Перфоратор")
                .description("Профессиональный перфоратор")
                .available(true)
                .owner(2L)
                .request(itemRequest)
                .build();

        ItemDto itemDto = itemMapper.toItemDto(item);

        assertNotNull(itemDto);
        assertEquals(1L, itemDto.getId());
        assertEquals("Перфоратор", itemDto.getName());
        assertEquals("Профессиональный перфоратор", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertEquals(2L, itemDto.getOwner());
        assertEquals(10L, itemDto.getRequestId());

        assertNull(itemDto.getLastBooking());
        assertNull(itemDto.getNextBooking());
        assertNull(itemDto.getComments());
    }

    @Test
    void toItemDto_shouldHandleItemWithNullRequest() {
        Item item = Item.builder()
                .id(1L)
                .name("Отвертка")
                .description("Набор отверток")
                .available(true)
                .owner(3L)
                .request(null)
                .build();

        ItemDto itemDto = itemMapper.toItemDto(item);

        assertNotNull(itemDto);
        assertEquals(1L, itemDto.getId());
        assertEquals("Отвертка", itemDto.getName());
        assertEquals("Набор отверток", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertEquals(3L, itemDto.getOwner());
        assertNull(itemDto.getRequestId());
    }

    @Test
    void toItemDto_shouldHandleItemWithRequestHavingNullId() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(null)
                .description("Тестовый запрос")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Тестовый предмет")
                .description("Тестовое описание")
                .available(false)
                .owner(4L)
                .request(itemRequest)
                .build();

        ItemDto itemDto = itemMapper.toItemDto(item);

        assertNotNull(itemDto);
        assertEquals(1L, itemDto.getId());
        assertEquals("Тестовый предмет", itemDto.getName());
        assertEquals("Тестовое описание", itemDto.getDescription());
        assertFalse(itemDto.getAvailable());
        assertEquals(4L, itemDto.getOwner());
        assertNull(itemDto.getRequestId());
    }

    @Test
    void toItemDto_shouldHandleNullItem() {
        ItemDto itemDto = itemMapper.toItemDto(null);

        assertNull(itemDto);
    }

    @Test
    void toItemDto_shouldHandleItemWithNullFields() {
        Item item = Item.builder().build();

        ItemDto itemDto = itemMapper.toItemDto(item);

        assertNotNull(itemDto);
        assertNull(itemDto.getId());
        assertNull(itemDto.getName());
        assertNull(itemDto.getDescription());
        assertNull(itemDto.getAvailable());
        assertNull(itemDto.getOwner());
        assertNull(itemDto.getRequestId());
        assertNull(itemDto.getLastBooking());
        assertNull(itemDto.getNextBooking());
        assertNull(itemDto.getComments());
    }

    @Test
    void updateItemFromRequest_shouldUpdateItemFieldsFromRequest() {
        UpdateItemRequest request = new UpdateItemRequest();
        request.setName("Обновленная дрель");
        request.setDescription("Обновленное описание");
        request.setAvailable(false);

        Item existingItem = Item.builder()
                .id(1L)
                .name("Старая дрель")
                .description("Старое описание")
                .available(true)
                .owner(5L)
                .build();

        itemMapper.updateItemFromRequest(request, existingItem);

        assertEquals("Обновленная дрель", existingItem.getName());
        assertEquals("Обновленное описание", existingItem.getDescription());
        assertFalse(existingItem.getAvailable());

        assertEquals(1L, existingItem.getId());
        assertEquals(5L, existingItem.getOwner());
        assertNull(existingItem.getRequest());
    }

    @Test
    void updateItemFromRequest_shouldHandleNullUpdateRequest() {
        Item existingItem = Item.builder()
                .id(1L)
                .name("Оригинальное имя")
                .description("Оригинальное описание")
                .available(true)
                .owner(6L)
                .build();

        String originalName = existingItem.getName();
        String originalDescription = existingItem.getDescription();
        Boolean originalAvailable = existingItem.getAvailable();

        itemMapper.updateItemFromRequest(null, existingItem);

        assertEquals(originalName, existingItem.getName());
        assertEquals(originalDescription, existingItem.getDescription());
        assertEquals(originalAvailable, existingItem.getAvailable());
        assertEquals(1L, existingItem.getId());
        assertEquals(6L, existingItem.getOwner());
    }

    @Test
    void updateItemFromRequest_shouldHandleUpdateRequestWithNullFields() {
        UpdateItemRequest request = new UpdateItemRequest();

        Item existingItem = Item.builder()
                .id(1L)
                .name("Оригинальное имя")
                .description("Оригинальное описание")
                .available(true)
                .owner(7L)
                .build();

        String originalName = existingItem.getName();
        String originalDescription = existingItem.getDescription();
        Boolean originalAvailable = existingItem.getAvailable();

        itemMapper.updateItemFromRequest(request, existingItem);

        assertEquals(originalName, existingItem.getName());
        assertEquals(originalDescription, existingItem.getDescription());
        assertEquals(originalAvailable, existingItem.getAvailable());
        assertEquals(1L, existingItem.getId());
        assertEquals(7L, existingItem.getOwner());
    }

    @Test
    void updateItemFromRequest_shouldHandlePartialUpdate() {
        UpdateItemRequest request = new UpdateItemRequest();
        request.setName("Только новое имя");

        Item existingItem = Item.builder()
                .id(1L)
                .name("Старое имя")
                .description("Старое описание")
                .available(true)
                .owner(8L)
                .build();

        String originalDescription = existingItem.getDescription();
        Boolean originalAvailable = existingItem.getAvailable();

        itemMapper.updateItemFromRequest(request, existingItem);

        assertEquals("Только новое имя", existingItem.getName());
        assertEquals(originalDescription, existingItem.getDescription());
        assertEquals(originalAvailable, existingItem.getAvailable());
        assertEquals(1L, existingItem.getId());
        assertEquals(8L, existingItem.getOwner());
    }

    @Test
    void updateItemFromRequest_shouldThrowExceptionWhenTargetItemIsNull() {
        UpdateItemRequest request = new UpdateItemRequest();
        request.setName("Тестовое имя");

        assertThrows(NullPointerException.class,
                () -> itemMapper.updateItemFromRequest(request, null));
    }

    @Test
    void updateItemFromRequest_shouldUpdateOnlyAvailableField() {
        UpdateItemRequest request = new UpdateItemRequest();
        request.setAvailable(false);

        Item existingItem = Item.builder()
                .id(1L)
                .name("Оставить это имя")
                .description("Оставить это описание")
                .available(true)
                .owner(9L)
                .build();

        String originalName = existingItem.getName();
        String originalDescription = existingItem.getDescription();

        itemMapper.updateItemFromRequest(request, existingItem);

        assertEquals(originalName, existingItem.getName());
        assertEquals(originalDescription, existingItem.getDescription());
        assertFalse(existingItem.getAvailable());
        assertEquals(1L, existingItem.getId());
        assertEquals(9L, existingItem.getOwner());
    }

    @Test
    void updateItemFromRequest_shouldUpdateOnlyDescriptionField() {
        UpdateItemRequest request = new UpdateItemRequest();
        request.setDescription("Только новое описание"); // Установлено только описание

        Item existingItem = Item.builder()
                .id(1L)
                .name("Оставить это имя")
                .description("Старое описание")
                .available(true)
                .owner(10L)
                .build();

        String originalName = existingItem.getName();
        Boolean originalAvailable = existingItem.getAvailable();

        itemMapper.updateItemFromRequest(request, existingItem);

        assertEquals(originalName, existingItem.getName());
        assertEquals("Только новое описание", existingItem.getDescription());
        assertEquals(originalAvailable, existingItem.getAvailable());
        assertEquals(1L, existingItem.getId());
        assertEquals(10L, existingItem.getOwner());
    }

    @Test
    void toItemDto_shouldHandleItemWithoutRequest() {
        Item item = Item.builder()
                .id(1L)
                .name("Самостоятельный предмет")
                .description("Предмет без запроса")
                .available(true)
                .owner(11L)
                .request(null)
                .build();

        ItemDto itemDto = itemMapper.toItemDto(item);

        assertNotNull(itemDto);
        assertEquals(1L, itemDto.getId());
        assertEquals("Самостоятельный предмет", itemDto.getName());
        assertEquals("Предмет без запроса", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertEquals(11L, itemDto.getOwner());
        assertNull(itemDto.getRequestId());
    }
}