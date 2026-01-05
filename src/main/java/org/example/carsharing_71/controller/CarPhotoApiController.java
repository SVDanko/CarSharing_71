package org.example.carsharing_71.controller;

import org.example.carsharing_71.api.dto.CarPhotoDto;
import org.example.carsharing_71.api.dto.CarPhotoRequest;
import org.example.carsharing_71.domain.CarPhoto;
import org.example.carsharing_71.service.CarPhotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер для управления фотографиями автомобилей.
 * <p>
 * Эндпоинты:
 * - POST /api/cars/{carId}/photos — добавление новой фотографии к автомобилю
 * - GET  /api/cars/{carId}/photos — список фотографий автомобиля (новые сверху)
 * <p>
 * Ответы формируются в формате DTO ({@link CarPhotoDto}).
 */
@RestController
@RequestMapping("/api")
public class CarPhotoApiController {
    private final CarPhotoService carPhotoService;

    public CarPhotoApiController(CarPhotoService carPhotoService) {
        this.carPhotoService = carPhotoService;
    }

    /**
     * Добавляет фотографию к указанному автомобилю.
     *
     * @param carId   идентификатор автомобиля
     * @param request тело запроса с URL изображения
     * @return созданная запись в формате {@link CarPhotoDto} с HTTP статусом 201
     */
    @PostMapping("/cars/{cardId}/photos")
    public ResponseEntity<CarPhotoDto> add(
            @PathVariable Long carId,
            @RequestBody CarPhotoRequest request
    ) {
        // request = url("http://fotos_cars.org")
        CarPhoto carPhoto = carPhotoService.add(carId, request);
        return ResponseEntity.status(201).body(CarPhotoDto.fromEntity(carPhoto));
    }

    /**
     * Возвращает список фотографий автомобиля, отсортированных по дате создания (убывание).
     *
     * @param carId идентификатор автомобиля
     * @return список DTO фотографий
     */
    @GetMapping("/cars/{cardId}/photos")
    public List<CarPhotoDto> list(@PathVariable Long carId) {
        return carPhotoService.listByCar(carId).stream()
                .map(CarPhotoDto::fromEntity)
                .toList();
    }

}
