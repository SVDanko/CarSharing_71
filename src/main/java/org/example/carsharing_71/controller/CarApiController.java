package org.example.carsharing_71.controller;

import org.example.carsharing_71.api.dto.CarDto;
import org.example.carsharing_71.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * http://localhost:8080/api/cars
 * REST-контроллер каталога и доступности автомобилей.
 * - GET /api/cars - каталог с фильтрами
 * - GET /api/cars/availablity - доступные автомобили по временному интервалу
 */
@RestController
@RequestMapping("/api")
@Validated
@Tag(name = "Cars", description = "Каталог и доступность автомобилей")
public class CarApiController {
    private final CarService carService;

    public CarApiController(CarService carService) {
        this.carService = carService;
    }

    /**
     * Возвращает каталог автомобилей с опциональными фильтрами.
     *
     * @param locationId идентификатор локации (опционально)
     * @param modelId    идентификатор модели автомобиля (опционально)
     * @param seats      минимальное количество мест (опционально, должно быть > 0)
     * @return список автомобилей в формате {@link CarDto}
     */
    @GetMapping("/cars")
    @Operation(summary = "Каталог автомобилей", description = "Фильтры: locationId, modelId, seats")
    @ApiResponse(responseCode = "200", description = "Все гаразд!")
    @Parameter(name = "Location ID", description = "Идентификатор локации (опционально)")
    public List<CarDto> cars(
            @RequestParam(required = false) Long locationId,
            @RequestParam(required = false) Long modelId,
            @RequestParam(required = false) @Positive Integer seats
    ) {
        return carService.list(locationId, modelId, seats).stream()
                .map(CarDto::fromEntity)
                .collect(Collectors.toList());
//                .toList();
    }

    @GetMapping("/cars/availablity")
    @Operation(summary = "Доступность автомобилей", description = "Интервал времени ISO-8601 и опциональные фильтры: locationId, seats")
    @ApiResponse(responseCode = "200", description = "OK")
    public List<CarDto> availablity(
            @RequestParam(required = false) Long locationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) Instant startAt,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) Instant endAt,
            @RequestParam(required = true) @Positive Integer seats
    ) {
        return carService.available(locationId, startAt, endAt, seats).stream()
                .map(CarDto::fromEntity)
                .toList();      // "синтаксический сахар"
    }

}
