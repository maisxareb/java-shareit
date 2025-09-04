package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void createRequest_shouldReturnCreatedRequest() throws Exception {
        ItemRequestRequest request = new ItemRequestRequest();
        request.setDescription("Нужна дрель");

        ItemRequestResponse response = ItemRequestResponse.builder()
                .id(1L)
                .description("Нужна дрель")
                .build();

        when(itemRequestService.createRequest(anyLong(), any(ItemRequestRequest.class))).thenReturn(response);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Нужна дрель"));
    }

    @Test
    void getUserRequests_shouldReturnUserRequests() throws Exception {
        ItemRequestResponse response1 = ItemRequestResponse.builder().id(1L).build();
        ItemRequestResponse response2 = ItemRequestResponse.builder().id(2L).build();

        when(itemRequestService.getUserRequests(anyLong())).thenReturn(List.of(response1, response2));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void getOtherUsersRequests_shouldReturnOtherUsersRequests() throws Exception {
        ItemRequestResponse response1 = ItemRequestResponse.builder().id(1L).build();
        ItemRequestResponse response2 = ItemRequestResponse.builder().id(2L).build();

        when(itemRequestService.getOtherUsersRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(response1, response2));

        mockMvc.perform(get("/requests/all?from=0&size=10")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void getRequestById_shouldReturnRequest() throws Exception {
        ItemRequestResponse response = ItemRequestResponse.builder()
                .id(1L)
                .description("Нужна дрель")
                .build();

        when(itemRequestService.getRequestById(anyLong(), anyLong())).thenReturn(response);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Нужна дрель"));
    }
}