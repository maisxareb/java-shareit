package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingDateValidationTest {

    private final BookingServiceImpl bookingService = new BookingServiceImpl(null, null, null, null);

    @Test
    void validateBookingDates_WithNullStart_ThrowsValidationException() {
        assertThrows(ValidationException.class,
                () -> invokePrivateValidateMethod(null, LocalDateTime.now()));
    }

    @Test
    void validateBookingDates_WithNullEnd_ThrowsValidationException() {
        assertThrows(ValidationException.class,
                () -> invokePrivateValidateMethod(LocalDateTime.now(), null));
    }

    @Test
    void validateBookingDates_WithStartAfterEnd_ThrowsValidationException() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        assertThrows(ValidationException.class,
                () -> invokePrivateValidateMethod(start, end));
    }

    @Test
    void validateBookingDates_WithStartEqualEnd_ThrowsValidationException() {
        LocalDateTime now = LocalDateTime.now();
        assertThrows(ValidationException.class,
                () -> invokePrivateValidateMethod(now, now));
    }

    @Test
    void validateBookingDates_WithStartInPast_ThrowsValidationException() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        assertThrows(ValidationException.class,
                () -> invokePrivateValidateMethod(start, end));
    }

    @Test
    void validateBookingDates_WithValidDates_DoesNotThrow() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        assertDoesNotThrow(() -> invokePrivateValidateMethod(start, end));
    }

    private void invokePrivateValidateMethod(LocalDateTime start, LocalDateTime end) {
        try {
            var method = BookingServiceImpl.class.getDeclaredMethod("validateBookingDates",
                    LocalDateTime.class, LocalDateTime.class);
            method.setAccessible(true);
            method.invoke(bookingService, start, end);
        } catch (Exception e) {
            if (e.getCause() instanceof ValidationException) {
                throw (ValidationException) e.getCause();
            }
            throw new RuntimeException(e);
        }
    }
}