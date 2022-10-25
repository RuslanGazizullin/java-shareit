package ru.practicum.shareit.item.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String text;
    @Column
    private Long itemId;
    @Column
    private Long authorId;

    public Comment(Long id, String text, Long itemId, Long authorId) {
        this.id = id;
        this.text = text;
        this.itemId = itemId;
        this.authorId = authorId;
    }

    public Comment() {

    }
}
