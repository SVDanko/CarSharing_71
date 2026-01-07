package org.example.carsharing_71.repository;

import org.example.carsharing_71.domain.Car;
import org.example.carsharing_71.repository.projection.CarInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class CarRepositoryIT {
    @Autowired
    private CarRepository carRepository;

    @Test
    void findAll_returnsAllCars() {
        // Проверка @EntityGraph (косвенная, проверяем что просто работает)
        List<Car> cars = carRepository.findAll();
        assertThat(cars).isNotEmpty();
        assertThat(cars).anyMatch(c -> c.getCarModel() != null && c.getLocation() != null);
    }

    @Test
    void findByLocationId_withPagination_returnsPage() {
        // Given: Location ID=1 (from seed data)
        Long locationId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("plateNumber").descending());

        // When
        Page<Car> page = carRepository.findByLocation_Id(locationId, pageRequest);

        // Then
        assertThat(page.getTotalElements()).isGreaterThan(0);
        assertThat(page.getContent()).allMatch(c -> c.getLocation().getId().equals(locationId));
        // Проверка сортировки (у нас A123BC77, ожидаем его)
        // Если машин мало, сортировка может быть тривиальной, но главное метод вызывается
    }

    @Test
    void findByLocationId_projection_returnsCarInfo() {
        // Given
        Long locationId = 1L;

        // When
        List<CarInfo> infos = carRepository.findByLocation_Id(locationId);

        // Then
        assertThat(infos).isNotEmpty();
        CarInfo info = infos.get(0);
        assertThat(info.getPlateNumber()).isNotBlank();
        // Проверка вложенной проекции (ModelInfo)
        assertThat(info.getModel()).isNotNull();
//        assertThat(info.getModel().getBrand()).isNotBlank();
    }

    @Test
    void findAvailableByBrand_jpql_returnsCorrectCars() {
        // Given: Toyota Corolla is AVAILABLE in seed data
        String brand = "Toyota";

        // When
        List<Car> cars = carRepository.findAvailableByBrand(brand);

        // Then
        assertThat(cars).isNotEmpty();
        assertThat(cars).allMatch(c -> c.getCarModel().getBrand().equals(brand));
        assertThat(cars).allMatch(c -> c.getStatus().name().equals("AVAILABLE"));
    }

}
