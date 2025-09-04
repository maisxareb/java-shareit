package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EliminatingConflict;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_shouldCreateUserSuccessfully() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Иван Иванов");
        request.setEmail("ivan@example.com");

        User user = User.builder().id(1L).build();
        UserDto response = UserDto.builder().id(1L).build();

        when(userRepository.existsByEmail("ivan@example.com")).thenReturn(false);
        when(userMapper.toUser(any())).thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toUserDto(any())).thenReturn(response);

        UserDto result = userService.createUser(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).save(any());
    }

    @Test
    void createUser_shouldThrowWhenEmailExists() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Иван Иванов");
        request.setEmail("ivan@example.com");

        when(userRepository.existsByEmail("ivan@example.com")).thenReturn(true);

        assertThrows(EliminatingConflict.class, () -> userService.createUser(request));
    }

    @Test
    void updateUser_shouldUpdateUserSuccessfully() {
        Long userId = 1L;
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Иван Петров");
        request.setEmail("ivan.petrov@example.com");

        User existingUser = User.builder()
                .id(userId)
                .name("Иван Иванов")
                .email("ivan@example.com")
                .build();

        UserDto response = UserDto.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("ivan.petrov@example.com")).thenReturn(false);
        when(userRepository.save(any())).thenReturn(existingUser);
        when(userMapper.toUserDto(any())).thenReturn(response);

        UserDto result = userService.updateUser(request, userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository).save(any());
    }

    @Test
    void getUserById_shouldReturnUser() {
        Long userId = 1L;
        User user = User.builder().id(userId).build();
        UserDto response = UserDto.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(any())).thenReturn(response);

        UserDto result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
    }

    @Test
    void getUserById_shouldThrowWhenUserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        User user = User.builder().id(1L).build();
        UserDto response = UserDto.builder().id(1L).build();

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toUserDto(any())).thenReturn(response);

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }
}