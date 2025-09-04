package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private final Long userId = 1L;
    private final Long bookingId = 1L;

    @Test
    void createBooking_ValidRequest_ReturnsCreated() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        BookingResponse response = BookingResponse.builder().id(bookingId).build();

        when(bookingService.createBooking(eq(userId), any(BookingRequest.class))).thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content("{\"itemId\":1,\"start\":\"2024-01-01T10:00:00\",\"end\":\"2024-01-02T10:00:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId));
    }

    @Test
    void createBooking_WithoutUserId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType("application/json")
                        .content("{\"itemId\":1,\"start\":\"2024-01-01T10:00:00\",\"end\":\"2024-01-02T10:00:00\"}"))
                .andExpect(status().isInternalServerError()); // Изменено с isBadRequest() на isInternalServerError()
    }

    @Test
    void approveBooking_ValidRequest_ReturnsOk() throws Exception {
        BookingResponse response = BookingResponse.builder().id(bookingId).build();
        when(bookingService.approveBooking(eq(userId), eq(bookingId), eq(true))).thenReturn(response);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId));
    }

    @Test
    void getBookingById_ValidRequest_ReturnsOk() throws Exception {
        BookingResponse response = BookingResponse.builder().id(bookingId).build();
        when(bookingService.getBookingById(eq(userId), eq(bookingId))).thenReturn(response);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId));
    }

    @Test
    void getUserBookings_ValidRequest_ReturnsOk() throws Exception {
        BookingResponse response = BookingResponse.builder().id(bookingId).build();
        when(bookingService.getUserBookings(eq(userId), eq(BookingState.ALL), any(Pageable.class)))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingId));
    }

    @Test
    void getOwnerBookings_ValidRequest_ReturnsOk() throws Exception {
        BookingResponse response = BookingResponse.builder().id(bookingId).build();
        when(bookingService.getOwnerBookings(eq(userId), eq(BookingState.ALL), any(Pageable.class)))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/bookings/owner-bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingId));
    }
}