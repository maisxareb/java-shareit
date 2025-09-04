package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.NewItemRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

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

    @Test
    void updateItem_shouldSendCorrectRequest() {
        UpdateItemRequest request = new UpdateItemRequest();
        request.setName("Updated Name");

        String responseBody = "{\"id\":1,\"name\":\"Updated Name\"}";

        server.expect(requestTo("http://localhost:9090/items/1"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        itemClient.updateItem(1L, 1L, request);

        server.verify();
    }

    @Test
    void getUserItems_shouldSendCorrectRequest() {
        String responseBody = "[{\"id\":1,\"name\":\"Test Item\"}]";

        server.expect(requestTo("http://localhost:9090/items"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        itemClient.getUserItems(1L);

        server.verify();
    }

    @Test
    void searchItems_shouldSendCorrectRequest() {
        String responseBody = "[{\"id\":1,\"name\":\"Drill\"}]";

        server.expect(requestTo("http://localhost:9090/items/search?text=drill"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        itemClient.searchItems(1L, "drill");

        server.verify();
    }

    @Test
    void addComment_shouldSendCorrectRequest() {
        CommentDto commentDto = CommentDto.builder()
                .text("Great item!")
                .build();

        String responseBody = "{\"id\":1,\"text\":\"Great item!\"}";

        server.expect(requestTo("http://localhost:9090/items/1/comment"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        itemClient.addComment(1L, 1L, commentDto);

        server.verify();
    }
}