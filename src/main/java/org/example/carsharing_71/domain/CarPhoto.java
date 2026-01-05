package org.example.carsharing_71.domain;

import jakarta.persistence.*;

import java.time.Instant;

/**
 * Доменная сущность фотографии автомобиля.
 * <p>
 * Связана с таблицей {@code car_photos}.
 * Содержит ссылку на автомобиль, публичный URL снимка и время создания.
 */
@Entity
@Table(name = "car_photos")
public class CarPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Связь «многие к одному» с автомобилем. Обязательное поле. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    /** Публичный URL изображения. */
    @Column(nullable = false)
    private String url;

    /** Временная метка создания записи (UTC). */
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    /**
     * Заполняет поле {@link #createdAt} автоматически при сохранении,
     * если оно не было задано явно.
     */
    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
