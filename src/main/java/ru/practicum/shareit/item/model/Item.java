package ru.practicum.shareit.item.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "items")
public class Item {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String description;
    @Column(name = "is_available")
    private Boolean isAvailable;
    @Column(name = "owner_id")
    private Long owner;
    @Column(name = "request_id")
    private Long requestId;

    public Item(Long id, String name, String description, Boolean isAvailable, Long owner, Long requestId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
        this.owner = owner;
        this.requestId = requestId;
    }

    public Item() {
    }
}
