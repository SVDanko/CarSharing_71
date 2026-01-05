package org.example.carsharing_71.controller;

import org.example.carsharing_71.api.dto.ReservationCreateRequest;
import org.example.carsharing_71.api.dto.ReservationDto;
import org.example.carsharing_71.domain.Reservation;
import org.example.carsharing_71.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для обработки HTTP-запросов, связанных с бронированием автомобилей.
 * Обрабатывает запросы по пути /api.
 * 
 * <p>Основные функции:
 * - Создание нового бронирования
 * - Валидация входных данных
 * - Преобразование DTO в сущности и обратно</p>
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Reservations", description = "Создание бронирования автомобиля")
// При значении параметров по умолчанию
// будет в заголовке описания контроллера - reservation-api-controller
public class ReservationApiController {
    // Сервис для работы с бизнес-логикой бронирований
    private final ReservationService reservationService;

    /**
     * Конструктор контроллера.
     * 
     * @param reservationService сервис для работы с бронированиями
     */
    public ReservationApiController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * Обрабатывает POST запрос на создание нового бронирования.
     * 
     * @param request DTO с данными для создания бронирования
     * @return ResponseEntity с созданным бронированием и статусом 201 (Created)
     * 
     * <p>Параметры запроса валидируются с помощью аннотации @Valid.
     * В случае успеха возвращает HTTP 201 с данными созданного бронирования.</p>
     */
    @PostMapping("/reservations")
    @Operation(summary = "Создать бронирование", description = "Проверяет доступность и создает запись о бронировании")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "404", description = "User/Car not found")
    @ApiResponse(responseCode = "422", description = "Validation/Business error")
    @Parameter(name = "Request", description = "DTO с данными для создания бронирования")
    public ResponseEntity<ReservationDto> create(
            @Valid @RequestBody ReservationCreateRequest request
    ) {
        Reservation reservation = reservationService.create(request);
        return ResponseEntity.status(201).body(ReservationDto.fromEntity(reservation));
    }
}
