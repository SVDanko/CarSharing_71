package org.example.carsharing_71.service;

import org.example.carsharing_71.api.dto.ReservationCreateRequest;
import org.example.carsharing_71.domain.*;
import org.example.carsharing_71.repository.CarRepository;
import org.example.carsharing_71.repository.ReservationRepository;
import org.example.carsharing_71.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Сервис создания бронирований.
 * Проводит валидаторы бизнес‑правил: корректность интервала, существование пользователя/авто,
 * статус доступности автомобиля и отсутствие пересечений активных бронирований.
 * При успешном создании переводит автомобиль в статус RESERVED.
 */
@Service
public class ReservationService {
    /**
     * Репозиторий для работы с бронированиями
     */
    private final ReservationRepository reservationRepository;
    
    /**
     * Репозиторий для работы с пользователями
     */
    private final UserRepository userRepository;
    
    /**
     * Репозиторий для работы с автомобилями
     */
    private final CarRepository carRepository;

    /**
     * Конструктор сервиса бронирований.
     *
     * @param reservationRepository репозиторий для работы с бронированиями
     * @param userRepository репозиторий для работы с пользователями
     * @param carRepository репозиторий для работы с автомобилями
     * @throws IllegalArgumentException если любой из аргументов равен null
     */
    public ReservationService(
            ReservationRepository reservationRepository,
            UserRepository userRepository,
            CarRepository carRepository
    ) {
        if (reservationRepository == null || userRepository == null || carRepository == null) {
            throw new IllegalArgumentException("Repositories cannot be null");
        }
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.carRepository = carRepository;
    }

    /**
     * Создаёт новое бронирование автомобиля по запросу.
     * 
     * @param request DTO с данными для создания бронирования
     * @return созданное бронирование
     * @throws ResponseStatusException если запрос на бронирование невалиден
     * @throws ResponseStatusException если пользователь или автомобиль не найдены
     * @throws ResponseStatusException если автомобиль уже забронирован на указанный период
     */
    @Transactional
    public Reservation create(ReservationCreateRequest request) {
        if (!request.isTimeRangeValid()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "Invalid time range");
        }
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Car car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found"));

        if (car.getStatus() != CarStatus.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "Car is not available");
        }

        long overlaps = reservationRepository.countOverlaps(car.getId(), request.getStartAt(), request.getEndAt());

        if (overlaps > 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "Car has overlapping reservation");
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setCar(car);
        reservation.setStartAt(request.getStartAt());
        reservation.setEndAt(request.getEndAt());
        reservation.setStatus(ReservationStatus.ACTIVE);
        Reservation savedReservation = reservationRepository.save(reservation);
        car.setStatus(CarStatus.RESERVED);
        carRepository.save(car);
        return savedReservation;

    }
    
    /**
     * Отменяет существующее бронирование по идентификатору.
     * 
     * @param reservationId идентификатор бронирования для отмены
     * @param userId идентификатор пользователя, отменяющего бронирование
     * @throws EntityNotFoundException если бронирование не найдено
     * @throws AccessDeniedException если пользователь не является владельцем бронирования
     * @throws IllegalStateException если бронирование уже отменено или завершено
     */
    // TODO: Реализовать метод отмены бронирования
    
    /**
     * Получает список активных бронирований пользователя.
     * 
     * @param userId идентификатор пользователя
     * @return список активных бронирований
     * @throws IllegalArgumentException если userId равен null
     */
    // TODO: Реализовать метод получения активных бронирований

}
