package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.item.dto.NewItemRequest;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(ItemClient.class)
public class ItemClientTest {

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void createItem_shouldSendCorrectRequest() {
        NewItemRequest request = new NewItemRequest();
        request.setName("Тестовый предмет");
        request.setDescription("Тестовое описание");
        request.setAvailable(true);

        String responseBody = "{\"id\":1,\"name\":\"Тестовый предмет\",\"description\":\"Тестовое описание\",\"available\":true}";

        server.expect(requestTo("http://localhost:9090/items"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        itemClient.createItem(1L, request);

        server.verify();
    }

    @Test
    void getItemById_shouldSendCorrectRequest() {
        String responseBody = "{\"id\":1,\"name\":\"Test Item\"}";

        server.expect(requestTo("http://localhost:9090/items/1"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        itemClient.getItemById(1L, 1L);

        server.verify();
    }
}