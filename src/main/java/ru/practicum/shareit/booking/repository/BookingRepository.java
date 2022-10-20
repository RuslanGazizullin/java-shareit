package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long bookerId);

    Page<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

    List<Booking> findAllByItemIdAndBookerId(Long itemId, Long bookerId);

    List<Booking> findAllByItemIdAndEndBefore(Long itemId, LocalDateTime localDateTime);

    List<Booking> findAllByItemIdAndStartAfter(Long itemId, LocalDateTime localDateTime);

    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime localDateTime);

    Page<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime localDateTime);

    Page<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime localDateTime1,
                                                             LocalDateTime localDateTime2);

    Page<Booking> findAllByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime localDateTime1,
                                                             LocalDateTime localDateTime2, Pageable pageable);

    List<Booking> findAllByStartAfter(LocalDateTime localDateTime);

    Page<Booking> findAllByStartAfter(LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findAllByEndBefore(LocalDateTime localDateTime);

    Page<Booking> findAllByEndBefore(LocalDateTime localDateTime, Pageable pageable);

    List<Booking> findAllByStartBeforeAndEndAfter(LocalDateTime localDateTime1, LocalDateTime localDateTime2);

    Page<Booking> findAllByStartBeforeAndEndAfter(LocalDateTime localDateTime1, LocalDateTime localDateTime2,
                                                  Pageable pageable);

    Page<Booking> findAll(Pageable pageable);
}
