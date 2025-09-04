package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {

    private final BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @Test
    void toBooking_FromBookingRequest_MapsCorrectly() {
        BookingRequest request = new BookingRequest();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        Booking booking = bookingMapper.toBooking(request);

        assertNotNull(booking);
        assertEquals(request.getStart(), booking.getStart());
        assertEquals(request.getEnd(), booking.getEnd());
        assertNull(booking.getId());
        assertNull(booking.getStatus());
        assertNull(booking.getBooker());
        assertNull(booking.getItem());
    }
}