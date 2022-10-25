package ru.practicum.shareit.requests.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String description;
    @Column
    private Long requesterId;

    public ItemRequest() {
    }

    public ItemRequest(Long id, String description, Long requesterId) {
        this.id = id;
        this.description = description;
        this.requesterId = requesterId;
    }
}
