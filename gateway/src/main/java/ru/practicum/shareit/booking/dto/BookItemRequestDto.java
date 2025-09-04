package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookItemRequestDto {
    @NotNull(message = "ID вещи не может быть пустым")
    private Long itemId;

    @FutureOrPresent(message = "Дата начала не может быть в прошлом")
    @NotNull(message = "Дата начала обязательна для заполнения")
    private LocalDateTime start;

    @Future(message = "Дата окончания должна быть в будущем")
    @NotNull(message = "Дата окончания обязательна для заполнения")
    private LocalDateTime end;
}