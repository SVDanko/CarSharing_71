package org.example.carsharing_71.service;

import org.example.carsharing_71.api.dto.CarPhotoRequest;
import org.example.carsharing_71.domain.Car;
import org.example.carsharing_71.domain.CarPhoto;
import org.example.carsharing_71.repository.CarPhotoRepository;
import org.example.carsharing_71.repository.CarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервис-слой для работы с фотографиями автомобилей.
 * <p>
 * Отвечает за добавление новой фотографии к автомобилю и получение списка фото.
 * Инкапсулирует взаимодействие с репозиториями и доменной логикой.
 */
@Service
public class CarPhotoService {
    private final CarPhotoRepository carPhotoRepository;
    private final CarRepository carRepository;

    public CarPhotoService(CarPhotoRepository carPhotoRepository, CarRepository carRepository) {
        this.carPhotoRepository = carPhotoRepository;
        this.carRepository = carRepository;
    }

    /**
     * Добавляет фотографию для указанного автомобиля.
     *
     * @param carId   идентификатор автомобиля
     * @param request DTO с URL изображения
     * @return сохранённая сущность {@link CarPhoto}
     */
    @Transactional
    public CarPhoto add(Long carId, CarPhotoRequest request) {
        Car car = carRepository.findById(carId)
                .orElseThrow(); // Бросает NoSuchElementException, если автомобиль не найден

        CarPhoto carPhoto = new CarPhoto();
        carPhoto.setCar(car);
        carPhoto.setUrl(request.getUrl());
        return carPhotoRepository.save(carPhoto);
    }

    /**
     * Возвращает список фотографий автомобиля, отсортированный по времени создания (DESC).
     *
     * @param carId идентификатор автомобиля
     * @return список фотографий
     */
    public List<CarPhoto> listByCar(Long carId) {
        return carPhotoRepository.findByCar_IdOrderByCreatedAtDesc(carId);
    }
}
