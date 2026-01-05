package org.example.carsharing_71.repository;

import org.example.carsharing_71.domain.PromoCode;
import org.example.carsharing_71.domain.PromoCodeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

/**
 * Репозиторий для работы с промокодами в базе данных.
 * 
 * <p>Предоставляет стандартные CRUD-операции через наследование от JpaRepository.
 * Поддерживает создание кастомных запросов через соглашение об именовании методов.</p>
 * 
 * <p>Примеры использования:
 * - Поиск по коду промокода
 * - Получение активных промокодов на заданную дату
 * - Проверка уникальности кода</p>
 */
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    /**
     * Проверяет существование промокода с указанным значением.
     * 
     * @param code значение промокода для проверки
     * @return true если промокод с таким значением существует, иначе false
     */
//    public boolean existsByCode(String code);
    
    /**
     * Находит все промокоды с указанным статусом, активные в заданный период.
     * 
     * @param status статус промокода
     * @param startAt начало периода (должно быть до endAt промокода)
     * @param endAt конец периода (должно быть после startAt промокода)
     * @return список подходящих промокодов
     */
//    public List<PromoCode> findAllByStatusAndStartAtBeforeAndEndAtAfter(
//            PromoCodeStatus status, Instant startAt, Instant endAt);
}
