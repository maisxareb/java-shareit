package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(UserClient.class)
class UserClientTest {

    @Autowired
    private UserClient userClient;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void createUser_shouldSendCorrectRequest() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Иван Иванов");
        request.setEmail("ivan@example.com");

        String responseBody = "{\"id\":1,\"name\":\"Иван Иванов\",\"email\":\"ivan@example.com\"}";

        server.expect(requestTo("http://localhost:9090/users"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        userClient.createUser(request);

        server.verify();
    }

    @Test
    void updateUser_shouldSendCorrectRequest() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Иван Петров");

        String responseBody = "{\"id\":1,\"name\":\"Иван Петров\"}";

        server.expect(requestTo("http://localhost:9090/users/1"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        userClient.updateUser(1L, request);

        server.verify();
    }

    @Test
    void getUserById_shouldSendCorrectRequest() {
        String responseBody = "{\"id\":1,\"name\":\"Иван Иванов\"}";

        server.expect(requestTo("http://localhost:9090/users/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        userClient.getUserById(1L);

        server.verify();
    }

    @Test
    void deleteUser_shouldSendCorrectRequest() {
        server.expect(requestTo("http://localhost:9090/users/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        userClient.deleteUser(1L);

        server.verify();
    }

    @Test
    void getAllUsers_shouldSendCorrectRequest() {
        String responseBody = "[{\"id\":1,\"name\":\"Иван Иванов\"}]";

        server.expect(requestTo("http://localhost:9090/users"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        userClient.getAllUsers();

        server.verify();
    }
}