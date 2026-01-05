package org.example.carsharing_71.repository;

import org.example.carsharing_71.domain.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
    boolean existsByPlateNumber(String plateNumber);
}
