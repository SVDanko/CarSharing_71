package org.example.carsharing_71.service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j  // 1. Вешаем аннотацию на класс.
// Она позволяет сгенерировать: private static final Logger log
@NoArgsConstructor
@AllArgsConstructor
@Service
public class ExampleLoggerService {
    private String status;

    public void doWork() {
        // 2. Используем переменную 'log' (она создается автоматически при использовании аннотации)
        log.info("Метод doWork запущен");

         status = "Active";
        log.debug("Текущий статус: {}", status);
    }
}
