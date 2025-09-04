package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookingIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void createBooking_IntegrationTest() {
        User owner = createUser("owner@email.com");
        User booker = createUser("booker@email.com");

        userRepository.save(owner);
        userRepository.save(booker);

        Item item = createItem(owner.getId(), true);
        itemRepository.save(item);

        BookingRequest request = new BookingRequest();
        request.setItemId(item.getId());
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));

        BookingResponse response = bookingService.createBooking(booker.getId(), request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(booker.getId(), response.getBooker().getId());
        assertEquals(item.getId(), response.getItem().getId());
    }

    private User createUser(String email) {
        return User.builder()
                .name("Test User")
                .email(email)
                .build();
    }

    private Item createItem(Long ownerId, Boolean available) {
        Item item = Item.builder()
                .name("Test Item")
                .description("Test Description")
                .available(available)
                .build();
        item.setOwner(ownerId);
        return item;
    }
}