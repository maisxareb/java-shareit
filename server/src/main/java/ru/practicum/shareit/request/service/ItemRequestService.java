package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

import java.util.List;

public interface ItemRequestService {
    ItemRequestResponse createRequest(Long userId, ItemRequestRequest request);

    List<ItemRequestResponse> getUserRequests(Long userId);

    List<ItemRequestResponse> getOtherUsersRequests(Long userId, Integer from, Integer size);

    ItemRequestResponse getRequestById(Long userId, Long requestId);
}