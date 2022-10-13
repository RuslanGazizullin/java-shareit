package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query
    List<Booking> findAllByBookerId(Long bookerId);

    @Query
    List<Booking> findAllByItemIdAndAndBookerId(Long itemId, Long bookerId);
}
