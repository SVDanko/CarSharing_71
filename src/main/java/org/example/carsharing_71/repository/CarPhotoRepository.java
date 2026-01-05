package org.example.carsharing_71.repository;

import org.example.carsharing_71.domain.CarPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * JPA-репозиторий для работы с фотографиями автомобилей.
 * <p>
 * Содержит метод для получения всех фото конкретного автомобиля
 * с сортировкой по времени создания (новые сверху).
 */
public interface CarPhotoRepository extends JpaRepository<CarPhoto, Long> {
    /**
     * Возвращает фотографии по идентификатору автомобиля, отсортированные по дате создания (DESC).
     *
     * @param carId идентификатор автомобиля
     * @return список фотографий
     */
    List<CarPhoto> findByCar_IdOrderByCreatedAtDesc(Long carId);
}
