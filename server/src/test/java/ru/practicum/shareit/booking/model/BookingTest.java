package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Test
    void testBookingBuilder() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        User booker = User.builder().id(1L).build();
        Item item = Item.builder().id(1L).build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        assertNotNull(booking);
        assertEquals(1L, booking.getId());
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void testEquals_SameId_ShouldReturnTrue() {
        Booking booking1 = Booking.builder().id(1L).build();
        Booking booking2 = Booking.builder().id(1L).build();

        assertEquals(booking1, booking2);
        assertEquals(booking1.hashCode(), booking2.hashCode());
    }

    @Test
    void testEquals_DifferentId_ShouldReturnFalse() {
        Booking booking1 = Booking.builder().id(1L).build();
        Booking booking2 = Booking.builder().id(2L).build();

        assertNotEquals(booking1, booking2);
    }

    @Test
    void testEquals_Null_ShouldReturnFalse() {
        Booking booking = Booking.builder().id(1L).build();

        assertNotEquals(null, booking);
    }

    @Test
    void testEquals_DifferentClass_ShouldReturnFalse() {
        Booking booking = Booking.builder().id(1L).build();
        Object other = new Object();

        assertNotEquals(booking, other);
    }

    @Test
    void testSetters() {
        Booking booking = new Booking();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        User booker = User.builder().id(1L).build();
        Item item = Item.builder().id(1L).build();

        booking.setId(1L);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);

        assertEquals(1L, booking.getId());
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }

    @Test
    void testNoArgsConstructor() {
        Booking booking = new Booking();

        assertNotNull(booking);
        assertNull(booking.getId());
        assertNull(booking.getStart());
        assertNull(booking.getEnd());
        assertNull(booking.getItem());
        assertNull(booking.getBooker());
        assertNull(booking.getStatus());
    }
}