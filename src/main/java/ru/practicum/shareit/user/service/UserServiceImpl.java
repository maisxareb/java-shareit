package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EliminatingConflict;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private static final String NOT_FOUND_MESSAGE = "Пользователь с ID %d не найден";

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest request) {
        log.info("Создание пользователя: {}", request);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EliminatingConflict(String.format("Email %s уже занят", request.getEmail()));
        }

        User user = userMapper.toUser(request);
        user = userRepository.save(user);
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(UpdateUserRequest request, Long userId) {
        log.info("Обновление пользователя ID {}: {}", userId, request);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, userId)));

        if (request.getEmail() != null && !request.getEmail().isBlank()
                && !request.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new EliminatingConflict(String.format("Email %s уже занят", request.getEmail()));
            }
            existingUser.setEmail(request.getEmail());
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            existingUser.setName(request.getName());
        }

        userRepository.save(existingUser);
        return userMapper.toUserDto(existingUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        log.info("Получение пользователя с ID: {}", userId);
        return userRepository.findById(userId)
                .map(userMapper::toUserDto)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, userId)));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("Удаление пользователя с ID: {}", userId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(NOT_FOUND_MESSAGE, userId));
        }
        userRepository.deleteById(userId);
    }
}
