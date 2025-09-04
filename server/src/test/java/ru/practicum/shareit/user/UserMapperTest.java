package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void testToUser_FromNewUserRequest() {
        NewUserRequest request = new NewUserRequest();
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");

        User user = userMapper.toUser(request);

        assertNotNull(user);
        assertNull(user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@example.com", user.getEmail());
    }

    @Test
    void testToUser_FromNewUserRequestWithNullValues() {
        NewUserRequest request = new NewUserRequest();
        request.setName(null);
        request.setEmail(null);

        User user = userMapper.toUser(request);

        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
    }

    @Test
    void testToUserDto_FromUser() {
        User user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();

        UserDto userDto = userMapper.toUserDto(user);

        assertNotNull(userDto);
        assertEquals(1L, userDto.getId());
        assertEquals("John Doe", userDto.getName());
        assertEquals("john.doe@example.com", userDto.getEmail());
    }

    @Test
    void testToUserDto_FromUserWithNullValues() {
        User user = User.builder()
                .id(1L)
                .name(null)
                .email(null)
                .build();

        UserDto userDto = userMapper.toUserDto(user);

        assertNotNull(userDto);
        assertEquals(1L, userDto.getId());
        assertNull(userDto.getName());
        assertNull(userDto.getEmail());
    }

    @Test
    void testUpdateUserFromRequest_WithAllFields() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Updated Name");
        request.setEmail("updated.email@example.com");

        User existingUser = User.builder()
                .id(1L)
                .name("Original Name")
                .email("original.email@example.com")
                .build();

        userMapper.updateUserFromRequest(request, existingUser);

        assertEquals(1L, existingUser.getId());
        assertEquals("Updated Name", existingUser.getName());
        assertEquals("updated.email@example.com", existingUser.getEmail());
    }

    @Test
    void testUpdateUserFromRequest_WithPartialFields() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Updated Name Only");

        User existingUser = User.builder()
                .id(1L)
                .name("Original Name")
                .email("original.email@example.com")
                .build();

        userMapper.updateUserFromRequest(request, existingUser);

        assertEquals(1L, existingUser.getId());
        assertEquals("Updated Name Only", existingUser.getName());
        assertEquals("original.email@example.com", existingUser.getEmail()); // email должен остаться прежним
    }

    @Test
    void testUpdateUserFromRequest_WithNullFields() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName(null);
        request.setEmail(null);

        User existingUser = User.builder()
                .id(1L)
                .name("Original Name")
                .email("original.email@example.com")
                .build();

        userMapper.updateUserFromRequest(request, existingUser);

        assertEquals(1L, existingUser.getId());
        assertEquals("Original Name", existingUser.getName()); // имя должно остаться прежним
        assertEquals("original.email@example.com", existingUser.getEmail()); // email должен остаться прежним
    }

    @Test
    void testUpdateUserFromRequest_WithEmptyRequest() {
        UpdateUserRequest request = new UpdateUserRequest();

        User existingUser = User.builder()
                .id(1L)
                .name("Original Name")
                .email("original.email@example.com")
                .build();

        userMapper.updateUserFromRequest(request, existingUser);

        assertEquals(1L, existingUser.getId());
        assertEquals("Original Name", existingUser.getName());
        assertEquals("original.email@example.com", existingUser.getEmail());
    }

    @Test
    void testUpdateUserFromRequest_WithNullRequest() {
        UpdateUserRequest request = null;

        User existingUser = User.builder()
                .id(1L)
                .name("Original Name")
                .email("original.email@example.com")
                .build();

        userMapper.updateUserFromRequest(request, existingUser);

        assertEquals(1L, existingUser.getId());
        assertEquals("Original Name", existingUser.getName());
        assertEquals("original.email@example.com", existingUser.getEmail());
    }

    @Test
    void testToUser_WithNullRequest() {
        NewUserRequest request = null;

        User user = userMapper.toUser(request);

        assertNull(user);
    }

    @Test
    void testToUserDto_WithNullUser() {
        User user = null;

        UserDto userDto = userMapper.toUserDto(user);

        assertNull(userDto);
    }
}