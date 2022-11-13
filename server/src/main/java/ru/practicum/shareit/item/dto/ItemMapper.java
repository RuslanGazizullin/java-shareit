package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public ItemMapper(BookingRepository bookingRepository, CommentRepository commentRepository,
                      CommentMapper commentMapper) {
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .requestId(item.getRequestId())
                .build();
    }

    public Item fromItemDto(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwner())
                .requestId(itemDto.getRequestId())
                .build();
    }

    public ItemWithBookingDto toItemWithBookingDto(Item item, Long userId) {
        if (item.getOwner().equals(userId)) {
            if (lastBooking(item.getId()).size() > 0 && nextBooking(item.getId()).size() > 0) {
                return ItemWithBookingDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .lastBooking(lastBooking(item.getId()).get(0))
                        .nextBooking(nextBooking(item.getId()).get(0))
                        .comments(commentRepository.findAllByItemId(item.getId())
                                .stream()
                                .map(commentMapper::toCommentDto)
                                .collect(Collectors.toList()))
                        .requestId(item.getRequestId())
                        .build();
            } else {
                return ItemWithBookingDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .lastBooking(null)
                        .nextBooking(null)
                        .comments(commentRepository.findAllByItemId(item.getId())
                                .stream()
                                .map(commentMapper::toCommentDto)
                                .collect(Collectors.toList()))
                        .requestId(item.getRequestId())
                        .build();
            }
        } else {
            return ItemWithBookingDto.builder()
                    .id(item.getId())
                    .name(item.getName())
                    .description(item.getDescription())
                    .available(item.getAvailable())
                    .lastBooking(null)
                    .nextBooking(null)
                    .comments(commentRepository.findAllByItemId(item.getId())
                            .stream()
                            .map(commentMapper::toCommentDto)
                            .collect(Collectors.toList()))
                    .requestId(item.getRequestId())
                    .build();
        }
    }

    private List<Booking> lastBooking(Long itemId) {
        return bookingRepository.findAllByItemIdAndEndBefore(itemId, LocalDateTime.now())
                .stream()
                .sorted(Comparator.comparing(Booking::getEnd).reversed())
                .collect(Collectors.toList());
    }

    private List<Booking> nextBooking(Long itemId) {
        return bookingRepository.findAllByItemIdAndStartAfter(itemId, LocalDateTime.now())
                .stream()
                .sorted(Comparator.comparing(Booking::getStart))
                .collect(Collectors.toList());
    }
}
