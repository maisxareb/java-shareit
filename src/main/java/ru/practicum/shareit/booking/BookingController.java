package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @Valid @RequestBody BookingRequest request) {
        log.info("Создание бронирования пользователем ID {}: {}", userId, request);
        return bookingService.createBooking(userId, request);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponse approveBooking(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {
        log.info("Подтверждение бронирования ID {} пользователем ID {}", bookingId, userId);
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponse getBookingById(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @PathVariable Long bookingId) {
        log.info("Получение бронирования ID {} пользователем ID {}", bookingId, userId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponse> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получение бронирований пользователя ID {} со статусом {}", userId, state);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        return bookingService.getUserBookings(userId, state, pageable);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingResponse> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получение бронирований владельца ID {} со статусом {}", userId, state);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        return bookingService.getOwnerBookings(userId, state, pageable);
    }
}