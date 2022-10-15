package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long bookerId);

    List<Booking> findAllByItemIdAndBookerId(Long itemId, Long bookerId);

    List<Booking> findAllByItemIdAndEndBefore(Long itemId, LocalDateTime localDateTime);

    List<Booking> findAllByItemIdAndStartAfter(Long itemId, LocalDateTime localDateTime);

    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime localDateTime);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime localDateTime);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime localDateTime1,
                                                             LocalDateTime localDateTime2);

    List<Booking> findAllByStartAfter(LocalDateTime localDateTime);

    List<Booking> findAllByEndBefore(LocalDateTime localDateTime);

    List<Booking> findAllByStartBeforeAndEndAfter(LocalDateTime localDateTime1, LocalDateTime localDateTime2);
}
