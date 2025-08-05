package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository {
    void save(User user);

    void update(User user);

    Optional<User> findById(Long userId);

    void remove(Long id);
}
