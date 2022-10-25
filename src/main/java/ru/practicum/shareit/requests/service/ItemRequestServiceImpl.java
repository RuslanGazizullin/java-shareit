package ru.practicum.shareit.requests.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.requests.validation.ItemRequestValidation;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRequestValidation itemRequestValidation;

    public ItemRequestServiceImpl(ItemRequestRepository itemRequestRepository, ItemRequestMapper itemRequestMapper,
                                  ItemRequestValidation itemRequestValidation) {
        this.itemRequestRepository = itemRequestRepository;
        this.itemRequestMapper = itemRequestMapper;
        this.itemRequestValidation = itemRequestValidation;
    }

    @Override
    public ItemRequestDto add(ItemRequest itemRequest, Long requesterId) {
        itemRequestValidation.userValidation(requesterId);
        itemRequestValidation.descriptionValidation(itemRequest);
        itemRequest.setRequesterId(requesterId);
        log.info("Запрос успешно создан");
        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> findAllByRequester(Long requesterId) {
        itemRequestValidation.userValidation(requesterId);
        log.info("Список запросов успешно получен");
        return itemRequestRepository.findAllByRequesterId(requesterId)
                .stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> findAll(Long userId, Integer from, Integer size) {
        log.info("Список запросов успешно получен");
        return itemRequestRepository.findAll(PageRequest.of(from / size, size))
                .stream()
                .filter(itemRequest -> !itemRequest.getRequesterId().equals(userId))
                .map(itemRequestMapper::toItemRequestDto)
                .sorted(Comparator.comparing(ItemRequestDto::getCreated).reversed())
                .collect(Collectors.toList());

    }

    @Override
    public ItemRequestDto findById(Long requestId, Long requesterId) {
        itemRequestValidation.userValidation(requesterId);
        itemRequestValidation.requestIdValidation(requestId);
        log.info("Данные о запросе успешно получены");
        return itemRequestMapper.toItemRequestDto(itemRequestRepository.findById(requestId).get());
    }
}
