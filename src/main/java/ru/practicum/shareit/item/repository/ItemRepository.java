package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i.id from Item i where i.owner = :ownerId ")
    List<Long> findAllIdByOwner(Long ownerId);

    @Query("select i from Item i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))")
    Page<Item> findByText(String text, Pageable pageable);

    List<Item> findAllByRequestId(Long requestId);

    Page<Item> findAllByOwner(Long ownerId, Pageable pageable);

    List<Item> findAllByOwner(Long ownerId);
}
