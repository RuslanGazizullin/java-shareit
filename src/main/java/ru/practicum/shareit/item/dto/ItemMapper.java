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
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                item.getOwner(),
                item.getRequestId()
        );
    }

    public Item fromItemDto(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner(),
                itemDto.getRequestId()
        );
    }

    public ItemWithBookingDto toItemWithBookingDto(Item item, Long userId) {
        if (item.getOwner().equals(userId)) {
            if (lastBooking(item.getId()).size() > 0 && nextBooking(item.getId()).size() > 0) {
                return new ItemWithBookingDto(
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getIsAvailable(),
                        lastBooking(item.getId()).get(0),
                        nextBooking(item.getId()).get(0),
                        commentRepository.findAllByItemId(item.getId())
                                .stream()
                                .map(commentMapper::toCommentDto)
                                .collect(Collectors.toList())
                );
            } else {
                return new ItemWithBookingDto(
                        item.getId(),
                        item.getName(),
                        item.getDescription(),
                        item.getIsAvailable(),
                        null,
                        null,
                        commentRepository.findAllByItemId(item.getId())
                                .stream()
                                .map(commentMapper::toCommentDto)
                                .collect(Collectors.toList())
                );
            }
        } else {
            return new ItemWithBookingDto(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getIsAvailable(),
                    null,
                    null,
                    commentRepository.findAllByItemId(item.getId())
                            .stream()
                            .map(commentMapper::toCommentDto)
                            .collect(Collectors.toList())
            );
        }
    }

    private List<Booking> lastBooking(Long itemId) {
        return bookingRepository.findAll()
                .stream()
                .filter(booking -> booking.getItemId().equals(itemId))
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .sorted(Comparator.comparing(Booking::getEnd).reversed())
                .collect(Collectors.toList());
    }

    private List<Booking> nextBooking(Long itemId) {
        return bookingRepository.findAll()
                .stream()
                .filter(booking -> booking.getItemId().equals(itemId))
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(Booking::getStart))
                .collect(Collectors.toList());
    }
}
