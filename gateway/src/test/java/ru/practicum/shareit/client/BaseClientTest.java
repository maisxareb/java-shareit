package ru.practicum.shareit.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.*;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(UserClient.class)
class BaseClientTest {

    @Autowired
    private UserClient userClient;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void put_shouldSendCorrectRequest() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Test User");
        request.setEmail("test@test.com");

        String responseBody = "{\"id\":1,\"name\":\"Test User\",\"email\":\"test@test.com\"}";

        server.expect(requestTo("http://localhost:9090/users/1"))
                .andExpect(method(HttpMethod.PATCH)) // Изменено с PUT на PATCH
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        userClient.updateUser(1L, request);
        server.verify();
    }

    @Test
    void delete_withParameters_shouldSendRequest() {
        server.expect(requestTo("http://localhost:9090/users/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        userClient.deleteUser(1L);
        server.verify();
    }

    @Test
    void get_withParameters_shouldSendRequest() {
        String responseBody = "{\"id\":1,\"name\":\"Test User\"}";

        server.expect(requestTo("http://localhost:9090/users/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.getUserById(1L);
        assertNotNull(response);
    }

    @Test
    void post_withoutUserId_shouldSendRequest() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Test");
        request.setEmail("test@test.com");

        String responseBody = "{\"id\":1,\"name\":\"Test\",\"email\":\"test@test.com\"}";

        server.expect(requestTo("http://localhost:9090/users"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.createUser(request);
        assertNotNull(response);
    }

    @Test
    void patch_withoutBody_shouldSendRequest() {
        String responseBody = "{\"id\":1,\"name\":\"Test\"}";

        server.expect(requestTo("http://localhost:9090/users/1"))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        ru.practicum.shareit.user.dto.UpdateUserRequest request = new ru.practicum.shareit.user.dto.UpdateUserRequest();
        request.setName("Test");
        ResponseEntity<Object> response = userClient.updateUser(1L, request);
        assertNotNull(response);
    }

    @Test
    void handleHttpStatusCodeException_shouldReturnErrorResponse() {
        server.expect(requestTo("http://localhost:9090/users/999"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .body("User not found")
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.getUserById(999L);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void makeAndSendRequest_withNullUserId_shouldNotIncludeHeader() {
        String responseBody = "[{\"id\":1,\"name\":\"Test User\"}]";

        server.expect(requestTo("http://localhost:9090/users"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(headerDoesNotExist("X-Sharer-User-Id"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.getAllUsers();
        assertNotNull(response);
    }

    @Test
    void prepareGatewayResponse_withErrorBody_shouldReturnBody() {
        server.expect(requestTo("http://localhost:9090/users/999"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .body("{\"error\":\"Invalid request\"}")
                        .contentType(MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.getUserById(999L);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}