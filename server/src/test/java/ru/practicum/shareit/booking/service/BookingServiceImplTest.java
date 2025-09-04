package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingRequest bookingRequest;
    private BookingResponse bookingResponse;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@email.com")
                .build();

        owner = User.builder()
                .id(2L)
                .name("Owner")
                .email("owner@email.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(2L)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();

        bookingRequest = new BookingRequest();
        bookingRequest.setItemId(1L);
        bookingRequest.setStart(LocalDateTime.now().plusDays(1));
        bookingRequest.setEnd(LocalDateTime.now().plusDays(2));

        bookingResponse = BookingResponse.builder()
                .id(1L)
                .start(bookingRequest.getStart())
                .end(bookingRequest.getEnd())
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void createBooking_ValidData_ReturnsBookingResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingMapper.toBooking(any(BookingRequest.class))).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        BookingResponse result = bookingService.createBooking(1L, bookingRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(1L, bookingRequest));
    }

    @Test
    void createBooking_ItemNotFound_ThrowsNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(1L, bookingRequest));
    }

    @Test
    void createBooking_ItemNotAvailable_ThrowsValidationException() {
        item.setAvailable(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () ->
                bookingService.createBooking(1L, bookingRequest));
    }

    @Test
    void createBooking_OwnerBooksOwnItem_ThrowsNotFoundException() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(2L, bookingRequest));
    }

    @Test
    void createBooking_StartDateInPast_ThrowsValidationException() {
        bookingRequest.setStart(LocalDateTime.now().minusDays(1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () ->
                bookingService.createBooking(1L, bookingRequest));
    }

    @Test
    void createBooking_StartAfterEnd_ThrowsValidationException() {
        bookingRequest.setStart(LocalDateTime.now().plusDays(2));
        bookingRequest.setEnd(LocalDateTime.now().plusDays(1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () ->
                bookingService.createBooking(1L, bookingRequest));
    }

    @Test
    void createBooking_StartEqualsEnd_ThrowsValidationException() {
        LocalDateTime now = LocalDateTime.now();
        bookingRequest.setStart(now);
        bookingRequest.setEnd(now);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () ->
                bookingService.createBooking(1L, bookingRequest));
    }

    @Test
    void createBooking_NullDates_ThrowsValidationException() {
        bookingRequest.setStart(null);
        bookingRequest.setEnd(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () ->
                bookingService.createBooking(1L, bookingRequest));
    }

    @Test
    void approveBooking_ValidApproval_ReturnsApprovedBooking() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        BookingResponse result = bookingService.approveBooking(2L, 1L, true);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void approveBooking_ValidRejection_ReturnsRejectedBooking() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        BookingResponse result = bookingService.approveBooking(2L, 1L, false);

        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, booking.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void approveBooking_BookingNotFound_ThrowsNotFoundException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.approveBooking(2L, 1L, true));
    }

    @Test
    void approveBooking_NotOwner_ThrowsValidationException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () ->
                bookingService.approveBooking(1L, 1L, true));
    }

    @Test
    void approveBooking_AlreadyApproved_ThrowsValidationException() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () ->
                bookingService.approveBooking(2L, 1L, true));
    }

    @Test
    void getBookingById_ValidRequest_ReturnsBookingResponse() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        BookingResponse result = bookingService.getBookingById(1L, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getBookingById_BookingNotFound_ThrowsNotFoundException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                bookingService.getBookingById(1L, 1L));
    }

    @Test
    void getBookingById_UnauthorizedUser_ThrowsNotFoundException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () ->
                bookingService.getBookingById(3L, 1L));
    }

    @Test
    void getUserBookings_AllState_ReturnsBookings() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        List<BookingResponse> result = bookingService.getUserBookings(1L, BookingState.ALL, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getUserBookings_CurrentState_ReturnsBookings() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        List<BookingResponse> result = bookingService.getUserBookings(1L, BookingState.CURRENT, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getUserBookings_PastState_ReturnsBookings() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        List<BookingResponse> result = bookingService.getUserBookings(1L, BookingState.PAST, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getUserBookings_FutureState_ReturnsBookings() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        List<BookingResponse> result = bookingService.getUserBookings(1L, BookingState.FUTURE, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getUserBookings_WaitingState_ReturnsBookings() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        List<BookingResponse> result = bookingService.getUserBookings(1L, BookingState.WAITING, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getUserBookings_RejectedState_ReturnsBookings() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        List<BookingResponse> result = bookingService.getUserBookings(1L, BookingState.REJECTED, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getUserBookings_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                bookingService.getUserBookings(1L, BookingState.ALL, Pageable.unpaged()));
    }

    @Test
    void bookingStateValueOf_InvalidName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                BookingState.valueOf("INVALID_STATE_NAME"));
    }

    @Test
    void getOwnerBookings_AllState_ReturnsBookings() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findByItem_OwnerOrderByStartDesc(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        List<BookingResponse> result = bookingService.getOwnerBookings(2L, BookingState.ALL, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getOwnerBookings_CurrentState_ReturnsBookings() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findByItem_OwnerAndStartBeforeAndEndAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        List<BookingResponse> result = bookingService.getOwnerBookings(2L, BookingState.CURRENT, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getOwnerBookings_PastState_ReturnsBookings() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findByItem_OwnerAndEndBeforeOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        List<BookingResponse> result = bookingService.getOwnerBookings(2L, BookingState.PAST, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getOwnerBookings_FutureState_ReturnsBookings() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findByItem_OwnerAndStartAfterOrderByStartDesc(
                anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        List<BookingResponse> result = bookingService.getOwnerBookings(2L, BookingState.FUTURE, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getOwnerBookings_WaitingState_ReturnsBookings() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findByItem_OwnerAndStatusOrderByStartDesc(
                anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        List<BookingResponse> result = bookingService.getOwnerBookings(2L, BookingState.WAITING, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getOwnerBookings_RejectedState_ReturnsBookings() {
        when(userRepository.existsById(2L)).thenReturn(true);
        when(bookingRepository.findByItem_OwnerAndStatusOrderByStartDesc(
                anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(booking));
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        List<BookingResponse> result = bookingService.getOwnerBookings(2L, BookingState.REJECTED, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getOwnerBookings_UserNotFound_ThrowsNotFoundException() {
        when(userRepository.existsById(2L)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                bookingService.getOwnerBookings(2L, BookingState.ALL, Pageable.unpaged()));
    }

    @Test
    void getOwnerBookings_UnknownState_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                BookingState.valueOf("UNKNOWN"));
    }
}