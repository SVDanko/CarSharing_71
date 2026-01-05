package org.example.carsharing_71.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

/**
 * DTO запроса на создание бронирования.
 * Содержит базовые аннотации валидации и проверку диапазона времени.
 */
public class ReservationCreateRequest {
    /**
     * Идентификатор пользователя, который создаёт бронирование
     * Не может быть null
     */
    @NotNull @Positive
    private Long userId;
    
    /**
     * Идентификатор автомобиля, который бронируется
     * Не может быть null
     */
    @NotNull @Positive
    private Long carId;
    
    /**
     * Дата и время начала бронирования
     * Должно быть раньше endAt
     */
    @NotNull @Future
    private Instant startAt;
    
    /**
     * Дата и время окончания бронирования
     * Должно быть позже startAt
     */
    @NotNull @Future
    private Instant endAt;

    /**
     * Проверяет, что начало интервала строго раньше конца.
     */
    /**
     * Проверяет валидность временного интервала бронирования.
     * 
     * @return true если startAt и endAt не null и startAt строго раньше endAt,
     *         иначе false
     */
    public boolean isTimeRangeValid() {
        return startAt != null && endAt != null && startAt.isBefore(endAt);
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
}
