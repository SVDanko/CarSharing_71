package org.example.carsharing_71.api.dto;

import org.example.carsharing_71.domain.Reservation;

import java.time.Instant;

/**
 * DTO бронирования для REST‑ответов.
 */
public class ReservationDto {
    /**
     * Уникальный идентификатор бронирования
     */
    private Long id;
    
    /**
     * Идентификатор пользователя, создавшего бронирование
     */
    private Long userId;
    
    /**
     * Идентификатор забронированного автомобиля
     */
    private Long carId;
    
    /**
     * Дата и время начала бронирования
     */
    private Instant startAt;
    
    /**
     * Дата и время окончания бронирования
     */
    private Instant endAt;
    
    /**
     * Текущий статус бронирования (например, ACTIVE, CANCELLED, COMPLETED)
     */
    private String status;

    /**
     * Преобразует сущность Reservation в объект DTO.
     * 
     * @param reservation сущность бронирования для конвертации
     * @return новый объект ReservationDto, заполненный данными из сущности
     * @throws IllegalArgumentException если переданный параметр reservation равен null
     */
    public static ReservationDto fromEntity(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        ReservationDto dto = new ReservationDto();
        dto.setId(reservation.getId());
        dto.setUserId(reservation.getUser().getId());
        dto.setCarId(reservation.getCar().getId());
        dto.setStartAt(reservation.getStartAt());
        dto.setEndAt(reservation.getEndAt());
        dto.setStatus(reservation.getStatus().name());

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Instant getStartAt() {
        return startAt;
    }

    public void setStartAt(Instant startAt) {
        this.startAt = startAt;
    }

    public Instant getEndAt() {
        return endAt;
    }

    public void setEndAt(Instant endAt) {
        this.endAt = endAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
