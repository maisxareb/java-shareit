package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(NewUserRequest request);

    UserDto updateUser(UpdateUserRequest request, Long userId);

    UserDto getUserById(Long userId);

    void deleteUser(Long userId);

    List<UserDto> getAllUsers();
}