package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

@Component
public class CommentMapper {

    private final UserRepository userRepository;

    public CommentMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                userRepository.findById(comment.getAuthorId()).get().getName(),
                LocalDateTime.now().withNano(0)
        );
    }
}
