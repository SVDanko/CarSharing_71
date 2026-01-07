package org.example.carsharing_71.repository;

import org.example.carsharing_71.domain.Car;
import org.example.carsharing_71.repository.projection.CarInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Репозиторий автомобилей.
 *
 * Примечания:
 * - Метод {@link #existsByPlateNumber(String)} использует query‑method Spring Data JPA
 *   для проверки уникальности госномера при создании.
 * - Для выборок с связями (model/location) используйте {@code @EntityGraph} или проекции,
 *   чтобы избежать N+1 и лишних полей.
 */
public interface CarRepository extends JpaRepository<Car, Long> {
    boolean existsByPlateNumber(String plateNumber);

    // --- Урок 7: EntityGraph ---
    // Решает проблему N+1, загружая связи model и location в одном запросе (LEFT OUTER JOIN).
    @EntityGraph(attributePaths = {"carModel", "location"})
    List<Car> findAll();

    // --- Урок 7: Пагинация ---
    // Возвращает страницу (Page) автомобилей для заданной локации.
    // Pageable содержит номер страницы, размер и сортировку.
    Page<Car> findByLocation_Id(Long locationId, Pageable pageable);

    // --- Урок 7: Проекции ---
    // Возвращает облегченный список (только ID, номер и бренд/модель),
    // не извлекая лишние поля (status, fuelType и т.д.).
    List<CarInfo> findByLocation_Id(Long locationId);

    // --- Урок 7: JPQL (Поиск с условиями) ---
    // Пример кастомного запроса: найти доступные машины определенного бренда.
    @Query("select c from Car c where c.status = org.example.carsharing_71.domain.CarStatus.AVAILABLE " +
            "and c.carModel.brand = :brand")
    List<Car> findAvailableByBrand(@Param("brand") String brand);
}

