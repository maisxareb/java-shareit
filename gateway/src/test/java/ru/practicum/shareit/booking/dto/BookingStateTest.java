package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookingStateTest {

    @Test
    void from_shouldReturnCorrectState() {
        Optional<BookingState> result = BookingState.from("ALL");
        assertTrue(result.isPresent());
        assertEquals(BookingState.ALL, result.get());
    }

    @Test
    void from_shouldReturnEmptyForInvalidState() {
        Optional<BookingState> result = BookingState.from("INVALID");
        assertFalse(result.isPresent());
    }

    @Test
    void from_shouldBeCaseInsensitive() {
        Optional<BookingState> result = BookingState.from("all");
        assertTrue(result.isPresent());
        assertEquals(BookingState.ALL, result.get());
    }

    @ParameterizedTest
    @ValueSource(strings = {"all", "CURRENT", "past", "FUTURE", "waiting", "rejected"})
    void fromString_shouldHandleAllValidStates(String state) {
        assertDoesNotThrow(() -> {
            BookingState result = BookingState.fromString(state);
            assertNotNull(result);
        });
    }

    @Test
    void fromString_shouldReturnAllForInvalidState() {
        BookingState result = BookingState.fromString("invalid");
        assertEquals(BookingState.ALL, result);
    }

    @Test
    void fromString_shouldHandleNull() {
        BookingState result = BookingState.fromString(null);
        assertEquals(BookingState.ALL, result);
    }

    @Test
    void fromString_shouldHandleEmptyString() {
        BookingState result = BookingState.fromString("");
        assertEquals(BookingState.ALL, result);
    }

    @Test
    void values_shouldContainAllStates() {
        BookingState[] values = BookingState.values();
        assertEquals(6, values.length);
        assertArrayEquals(new BookingState[]{
                BookingState.ALL, BookingState.CURRENT, BookingState.PAST,
                BookingState.FUTURE, BookingState.WAITING, BookingState.REJECTED
        }, values);
    }

    @Test
    void valueOf_shouldWorkForAllStates() {
        assertDoesNotThrow(() -> {
            for (BookingState state : BookingState.values()) {
                BookingState.valueOf(state.name());
            }
        });
    }
}