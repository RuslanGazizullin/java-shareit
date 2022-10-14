package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query
    List<Booking> findAllByBookerId(Long bookerId);

    @Query
    List<Booking> findAllByItemIdAndAndBookerId(Long itemId, Long bookerId);

    @Query
    List<Booking> findAllByItemIdAndEndBefore(Long itemId, LocalDateTime localDateTime);

    @Query
    List<Booking> findAllByItemIdAndStartAfter(Long itemId, LocalDateTime localDateTime);

    @Query
    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime localDateTime);

    @Query
    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime localDateTime);

    @Query
    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime localDateTime1,
                                                             LocalDateTime localDateTime2);

    @Query
    List<Booking> findAllByStartAfter(LocalDateTime localDateTime);

    @Query
    List<Booking> findAllByEndBefore(LocalDateTime localDateTime);

    @Query
    List<Booking> findAllByStartBeforeAndEndAfter(LocalDateTime localDateTime1, LocalDateTime localDateTime2);
}
