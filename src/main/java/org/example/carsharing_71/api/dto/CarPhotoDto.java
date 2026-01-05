package org.example.carsharing_71.api.dto;

import org.example.carsharing_71.domain.CarPhoto;

import java.time.Instant;

/**
 * DTO представление фотографии автомобиля для REST-слоя.
 * <p>
 * Используется для передачи минимально необходимой информации клиенту:
 * идентификатор, идентификатор автомобиля, URL снимка и дата создания.
 * Преобразование из доменной сущности выполняется методом {@link #fromEntity(CarPhoto)}.
 */
public class CarPhotoDto {
    private Long id;
    private Long carId;
    private String url;
    private Instant createdAt;

    /**
     * Преобразует доменную сущность {@link CarPhoto} в DTO.
     *
     * @param p доменная сущность фотографии
     * @return заполненный {@link CarPhotoDto}
     */
    public static CarPhotoDto fromEntity(CarPhoto p) {
        CarPhotoDto dto = new CarPhotoDto();
        dto.setId(p.getId());
        dto.setCarId(p.getCar().getId());
        dto.setUrl(p.getUrl());
        dto.setCreatedAt(p.getCreatedAt());
        return dto;
    }

    /** Идентификатор записи фотографии. */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    /** Идентификатор автомобиля, к которому относится фотография. */
    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }
    /** Публичный URL снимка (например, ссылка на CDN/объектное хранилище). */
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    /** Время создания записи (UTC, {@link Instant}). */
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
