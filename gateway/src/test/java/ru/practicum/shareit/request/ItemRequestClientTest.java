package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.request.dto.ItemRequestRequest;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(ItemRequestClient.class)
class ItemRequestClientTest {

    @Autowired
    private ItemRequestClient itemRequestClient;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void createRequest_shouldSendCorrectRequest() {
        ItemRequestRequest request = new ItemRequestRequest();
        request.setDescription("Need a drill");

        String responseBody = "{\"id\":1,\"description\":\"Need a drill\"}";

        server.expect(requestTo("http://localhost:9090/requests"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        itemRequestClient.createRequest(1L, request);
        server.verify();
    }

    @Test
    void getOtherUsersRequests_withPagination_shouldSendCorrectRequest() {
        String responseBody = "[{\"id\":1,\"description\":\"Request 1\"}]";

        server.expect(requestTo("http://localhost:9090/requests/all?from=10&size=20"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        itemRequestClient.getOtherUsersRequests(1L, 10, 20);
        server.verify();
    }

    @Test
    void getRequestById_shouldSendCorrectRequest() {
        String responseBody = "{\"id\":1,\"description\":\"Test request\"}";

        server.expect(requestTo("http://localhost:9090/requests/1"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        itemRequestClient.getRequestById(1L, 1L);
        server.verify();
    }
}