package org.example.carsharing_71.repository;

import org.example.carsharing_71.domain.Car;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    boolean existsByPlateNumber(String plateNumber);

    // Решает проблему N+1, загружая связи model и location в одном запросе (LEFT OUTER JOIN)
    @EntityGraph(attributePaths = {"carModel", "location"})
    List<Car> findAll();
}
