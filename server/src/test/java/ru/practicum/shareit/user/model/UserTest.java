package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserBuilder() {
        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@email.com")
                .build();

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("Test User", user.getName());
        assertEquals("test@email.com", user.getEmail());
    }

    @Test
    void testEquals_SameId_ShouldReturnTrue() {
        User user1 = User.builder().id(1L).build();
        User user2 = User.builder().id(1L).build();

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void testEquals_DifferentId_ShouldReturnFalse() {
        User user1 = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();

        assertNotEquals(user1, user2);
    }

    @Test
    void testEquals_Null_ShouldReturnFalse() {
        User user = User.builder().id(1L).build();

        assertNotEquals(null, user);
    }

    @Test
    void testEquals_DifferentClass_ShouldReturnFalse() {
        User user = User.builder().id(1L).build();
        Object other = new Object();

        assertNotEquals(user, other);
    }

    @Test
    void testSetters() {
        User user = new User();

        user.setId(1L);
        user.setName("New Name");
        user.setEmail("new@email.com");

        assertEquals(1L, user.getId());
        assertEquals("New Name", user.getName());
        assertEquals("new@email.com", user.getEmail());
    }

    @Test
    void testNoArgsConstructor() {
        User user = new User();

        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User(1L, "Test User", "test@email.com");

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("Test User", user.getName());
        assertEquals("test@email.com", user.getEmail());
    }
}