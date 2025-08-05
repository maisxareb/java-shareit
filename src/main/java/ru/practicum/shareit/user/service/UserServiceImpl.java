package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EliminatingConflict;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Set<String> emailsInMemory = new HashSet<>();

    private static final String NOT_FOUND_MESSAGE = "Пользователь с ID %d не найден";

    public UserServiceImpl(@Qualifier("InMemoryStorage") UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(NewUserRequest request) {
        log.info("Создание пользователя: {}", request);
        validateEmail(request.getEmail());

        User user = userMapper.toUser(request);
        userRepository.save(user);
        emailsInMemory.add(user.getEmail());

        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(UpdateUserRequest request, Long userId) {
        log.info("Обновление пользователя ID {}: {}", userId, request);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, userId)));

        String oldEmail = existingUser.getEmail();

        if (request.getEmail() != null && !request.getEmail().isBlank()
                && !request.getEmail().equals(oldEmail)) {
            validateEmail(request.getEmail());
            emailsInMemory.remove(oldEmail);
            existingUser.setEmail(request.getEmail());
            emailsInMemory.add(request.getEmail());
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            existingUser.setName(request.getName());
        }

        userRepository.update(existingUser);
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
    public void deleteUser(Long userId) {
        log.info("Удаление пользователя с ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_MESSAGE, userId)));
        emailsInMemory.remove(user.getEmail());
        userRepository.remove(userId);
    }

    private void validateEmail(String email) {
        if (emailsInMemory.contains(email)) {
            log.warn("Попытка использовать занятый email: {}", email);
            throw new EliminatingConflict(String.format("Email %s уже занят", email));
        }
    }
}
