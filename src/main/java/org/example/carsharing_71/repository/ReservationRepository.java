package org.example.carsharing_71.repository;

import org.example.carsharing_71.domain.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;

/**
 * Репозиторий бронирований.
 * Содержит JPQL‑метод для подсчёта пересечений активных бронирований по интервалу.
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    /**
     * Считает количество активных бронирований для автомобиля, пересекающих указанный интервал.
     *
     * @param carId   идентификатор автомобиля
     * @param startAt начало интервала
     * @param endAt   конец интервала
     * @return количество пересечений
     */
    @Query("select count(r) from Reservation r " +
            "where r.car.id = :carId and r.status = org.example.carsharing_71.domain.ReservationStatus.ACTIVE " +
            "and r.startAt < :endAt and r.endAt > :startAt")
    long countOverlaps(
            @Param("carId") Long carId,
            @Param("startAt") Instant startAt,
            @Param("endAt") Instant endAt
    );

    List<Reservation> findByUser_Id(Long userId);

    // Новый метод с пагинацией
    Page<Reservation> findByUser_Id(Long userId, Pageable pageable);
}
