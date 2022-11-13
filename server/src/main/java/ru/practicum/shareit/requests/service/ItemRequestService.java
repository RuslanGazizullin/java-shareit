package ru.practicum.shareit.requests.service;

import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto add(ItemRequest itemRequest, Long requesterId);

    List<ItemRequestDto> findAllByRequester(Long requesterId);

    List<ItemRequestDto> findAll(Long userId, Integer from, Integer size);

    ItemRequestDto findById(Long requestId, Long requesterId);
}
