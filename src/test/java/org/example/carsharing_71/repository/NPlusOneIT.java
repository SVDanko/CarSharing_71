package org.example.carsharing_71.repository;

import org.example.carsharing_71.domain.Car;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
public class NPlusOneIT {
    @Autowired
    private CarRepository carRepository;

    @Test
    void findAll_causesNPlusOne() {
        System.out.println("\n--- [START] Тест N+1 (Плохой вариант) ---");
        System.out.println("Вызываем carRepository.findAll() (стандартный метод)...");

        // 1. Делаем выборку (1 запрос: SELECT * FROM cars)
        List<Car> cars = carRepository.findAll();

        System.out.println("Найдено машин: " + cars.size());
        System.out.println("Начинаем перебор списка и обращение к связанным сущностям...");

        for (Car car : cars) {
            // Обращение к getCarModel() инициирует дополнительный запрос
            // если модель еще не загружена в контекст
            String modelName = car.getCarModel().getModelName();
            String locationName = car.getLocation().getName();
            System.out.println("Машина: " + car.getPlateNumber() + ", модель: " + modelName);
        }
        System.out.println("\n--- [END] Тест N+1 (Посчитайте кол-во запросов (SELECT) в консоли выше!) ---");
        assertThat(cars).isNotNull();

    }

    @Test
    void findAllWithGraph_solvesNPlusOne() {
        System.out.println("\n--- [START] Тест EntityGraph (Хороший вариант) ---");
        System.out.println("Вызываем кастомный carRepository.findAll() с @EntityGraph...");

        // 1. Делаем выборку (1 запрос с JOIN: SELECT * FROM cars LEFT JOIN car_models ...)
        List<Car> cars = carRepository.findAll();

        System.out.println("Найдено машин: " + cars.size());
        System.out.println("Начинаем перебор списка (не должно быть дополнительных запросов)...");

        for (Car car : cars) {
            // Данные уже загружены, Hibernate берет их из памяти
            String modelName = car.getCarModel().getModelName();
            String locationName = car.getLocation().getName();
            System.out.println("Машина: " + car.getPlateNumber() + ", модель: " + modelName);
        }
        System.out.println("\n--- [END] Тест EntityGraph (Должен быть только ОДИН больщой запрос) ---");
        assertThat(cars).isNotNull();
    }
}
