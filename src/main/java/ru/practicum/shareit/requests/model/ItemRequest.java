package ru.practicum.shareit.requests.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @Column
    private Long id;
    @Column
    private String description;
    @Column
    private Long requesterId;
}
