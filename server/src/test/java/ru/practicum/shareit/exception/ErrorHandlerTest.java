package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleNotFoundException_ReturnsNotFoundStatus() {
        NotFoundException exception = new NotFoundException("Item not found");

        ErrorHandler.ErrorResponse response = errorHandler.handleNotFoundException(exception);

        assertNotNull(response);
        assertEquals("Item not found", response.getError());
    }

    @Test
    void handleValidationException_ReturnsBadRequestStatus() {
        ValidationException exception = new ValidationException("Validation error");

        ErrorHandler.ErrorResponse response = errorHandler.handleValidationException(exception);

        assertNotNull(response);
        assertEquals("Validation error", response.getError());
    }

    @Test
    void handleConflictException_ReturnsConflictStatus() {
        EliminatingConflict exception = new EliminatingConflict("Conflict occurred");

        ErrorHandler.ErrorResponse response = errorHandler.handleConflictException(exception);

        assertNotNull(response);
        assertEquals("Conflict occurred", response.getError());
    }

    @Test
    void handleMethodArgumentTypeMismatchException_ReturnsBadRequest() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getName()).thenReturn("itemId");
        when(exception.getValue()).thenReturn("invalid");

        ErrorHandler.ErrorResponse response = errorHandler.handleMethodArgumentTypeMismatchException(exception);

        assertNotNull(response);
        assertTrue(response.getError().contains("Неверный тип параметра: itemId"));
    }

    @Test
    void handleThrowable_ReturnsInternalServerError() {
        RuntimeException exception = new RuntimeException("Unexpected error");

        ErrorHandler.ErrorResponse response = errorHandler.handleThrowable(exception);

        assertNotNull(response);
        assertEquals("Произошла непредвиденная ошибка", response.getError());
    }

    @Test
    void errorResponse_ConstructorAndGettersWorkCorrectly() {
        ErrorHandler.ErrorResponse response = new ErrorHandler.ErrorResponse("Test error");

        assertEquals("Test error", response.getError());

        // Test setter
        response.setError("New error");
        assertEquals("New error", response.getError());
    }
}