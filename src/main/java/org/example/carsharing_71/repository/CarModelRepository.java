package org.example.carsharing_71.repository;

import org.example.carsharing_71.domain.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий доступа к моделям автомобилей
 */
public interface CarModelRepository extends JpaRepository<CarModel, Long> {
}
