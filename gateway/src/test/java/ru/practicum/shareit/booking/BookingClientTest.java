package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(BookingClient.class)
class BookingClientTest {

    @Autowired
    private BookingClient bookingClient;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void getBookings_shouldSendCorrectRequest() {
        String responseBody = "[{\"id\":1,\"status\":\"APPROVED\"}]";

        server.expect(requestTo("http://localhost:9090/bookings?state=ALL&from=0&size=10"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        bookingClient.getBookings(1L, "ALL", 0, 10);

        server.verify();
    }

    @Test
    void bookItem_shouldSendCorrectRequest() {
        BookItemRequestDto request = new BookItemRequestDto();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        String responseBody = "{\"id\":1,\"status\":\"WAITING\"}";

        server.expect(requestTo("http://localhost:9090/bookings"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        bookingClient.bookItem(1L, request);

        server.verify();
    }

    @Test
    void getBooking_shouldSendCorrectRequest() {
        String responseBody = "{\"id\":1,\"status\":\"APPROVED\"}";

        server.expect(requestTo("http://localhost:9090/bookings/1"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        bookingClient.getBooking(1L, 1L);

        server.verify();
    }

    @Test
    void approveBooking_shouldSendCorrectRequest() {
        String responseBody = "{\"id\":1,\"status\":\"APPROVED\"}";

        server.expect(requestTo("http://localhost:9090/bookings/1?approved=true"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        bookingClient.approveBooking(1L, 1L, true);

        server.verify();
    }

    @Test
    void getOwnerBookings_shouldSendCorrectRequest() {
        String responseBody = "[{\"id\":1,\"status\":\"APPROVED\"}]";

        server.expect(requestTo("http://localhost:9090/bookings/owner-bookings?state=ALL&from=0&size=10"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        bookingClient.getOwnerBookings(1L, "ALL", 0, 10);

        server.verify();
    }

    @Test
    void getBookings_withDifferentStates_shouldSendCorrectRequests() {
        String[] states = {"CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED"};

        for (String state : states) {
            server.expect(requestTo("http://localhost:9090/bookings?state=" + state + "&from=0&size=5"))
                    .andExpect(method(HttpMethod.GET))
                    .andExpect(header("X-Sharer-User-Id", "1"))
                    .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));
        }

        for (String state : states) {
            bookingClient.getBookings(1L, state, 0, 5);
        }

        server.verify();
    }

    @Test
    void getBookings_withInvalidState_shouldHandleGracefully() {
        server.expect(requestTo("http://localhost:9090/bookings?state=INVALID&from=0&size=10"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withBadRequest());

        bookingClient.getBookings(1L, "INVALID", 0, 10);

        server.verify();
    }

    @Test
    void approveBooking_withFalse_shouldSendCorrectRequest() {
        String responseBody = "{\"id\":1,\"status\":\"REJECTED\"}";

        server.expect(requestTo("http://localhost:9090/bookings/1?approved=false"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        bookingClient.approveBooking(1L, 1L, false);

        server.verify();
    }
}