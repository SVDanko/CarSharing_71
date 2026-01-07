package org.example.carsharing_71.service;

import org.example.carsharing_71.domain.Car;
import org.example.carsharing_71.domain.CarStatus;
import org.example.carsharing_71.repository.CarRepository;
import org.example.carsharing_71.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис работы с каталогом и доступностью автомобилей.
 * list(...) возвращает список автомобилей, отфильтрованный по локации, модели и количеству мест.
 * available(...) возвращает только доступные автомобили без пересечений активных бронирований в заданном интервале.
 */
@Service
public class CarService {
    private final CarRepository carRepository;
    private final ReservationRepository reservationRepository;

    public CarService(CarRepository carRepository, ReservationRepository reservationRepository) {
        this.carRepository = carRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Возвращает каталог автомобилей с опциональными фильтрами.
     *
     * @param locationId идентификатор локации (опционально)
     * @param modelId    идентификатор модели (опционально)
     * @param seats      минимальное число мест (опционально)
     * @return список автомобилей, удовлетворяющих фильтрам
     */
    public List<Car> list(Long locationId, Long modelId, Integer seats) {
        return carRepository.findAll().stream()
                .filter(car -> locationId == null || car.getLocation().getId().equals(locationId))
                .filter(car -> modelId == null || car.getCarModel().getModelName().equals(modelId))
                .filter(car -> seats == null || car.getCarModel().getSeats() >= seats)
                .collect(Collectors.toList());

    }

    /**
     * Возвращает доступные для бронирования автомобили в заданном интервале времени.
     * Учитываются только автомобили со статусом AVAILABLE, удовлетворяющие фильтрам,
     * и не имеющие пересечений с активными бронированиями.
     *
     * @param locationId идентификатор локации (опционально)
     * @param startAt    начало интервала (ISO‑8601)
     * @param endAt      конец интервала (ISO‑8601)
     * @param seats      минимальное число мест (опционально)
     * @return список доступных автомобилей
     */
    public List<Car> available(Long locationId, Instant startAt, Instant endAt, Integer seats) {
        return  carRepository.findAll().stream()
                .filter(car -> car.getStatus() == CarStatus.AVAILABLE)
                .filter(car -> locationId == null || car.getLocation().getId().equals(locationId))
                .filter(car -> seats == null || car.getCarModel().getSeats() >= seats)
                .filter(car -> reservationRepository.countOverlaps(car.getId(), startAt, endAt) == 0)
                .collect(Collectors.toList());
    }

    public Optional<Car> findById(Long id) {
        return carRepository.findById(id);
    }
}
