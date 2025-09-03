package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void createItem_shouldReturnCreatedItem() throws Exception {
        NewItemRequest request = new NewItemRequest();
        request.setName("Тестовый предмет");
        request.setDescription("Тестовое описание");
        request.setAvailable(true);

        ItemDto responseDto = ItemDto.builder()
                .id(1L)
                .name("Тестовый предмет")
                .description("Тестовое описание")
                .available(true)
                .build();

        when(itemService.createItem(anyLong(), any(NewItemRequest.class))).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Тестовый предмет"))
                .andExpect(jsonPath("$.description").value("Тестовое описание"));
    }

    @Test
    void getItemById_shouldReturnItem() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Тестовый предмет")
                .description("Тестовое описание")
                .available(true)
                .build();

        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Тестовый предмет"));
    }

    @Test
    void getUserItems_shouldReturnUserItems() throws Exception {
        ItemDto item1 = ItemDto.builder().id(1L).name("Предмет 1").build();
        ItemDto item2 = ItemDto.builder().id(2L).name("Предмет 2").build();

        when(itemService.getUserItems(anyLong())).thenReturn(List.of(item1, item2));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }
}