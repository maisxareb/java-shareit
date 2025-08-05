package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody NewUserRequest request) {
        log.info("Создание пользователя: {}", request);
        return userService.createUser(request);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto updateUser(
            @Valid @RequestBody UpdateUserRequest request,
            @PathVariable("userId") Long userId) {
        log.info("Обновление пользователя с ID: {}", userId);
        return userService.updateUser(request, userId);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@PathVariable("userId") Long userId) {
        log.info("Получение пользователя с ID: {}", userId);
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("userId") Long userId) {
        log.info("Удаление пользователя с ID: {}", userId);
        userService.deleteUser(userId);
    }
}
