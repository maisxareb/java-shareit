package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.BookingState;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(Long userId, BookingRequest request);

    BookingResponse approveBooking(Long userId, Long bookingId, Boolean approved);

    BookingResponse getBookingById(Long userId, Long bookingId);

    List<BookingResponse> getUserBookings(Long userId, BookingState state, Pageable pageable);

    List<BookingResponse> getOwnerBookings(Long userId, BookingState state, Pageable pageable);
}