package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ItemRequestRequest {
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
}