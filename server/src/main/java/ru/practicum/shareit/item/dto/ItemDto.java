package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private Long requestId;
    private BookingInfo lastBooking;
    private BookingInfo nextBooking;
    private List<CommentDto> comments;

    @Data
    @Builder
    public static class BookingInfo {
        private Long id;
        private Long bookerId;
    }
}