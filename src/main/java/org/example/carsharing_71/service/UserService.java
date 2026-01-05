package org.example.carsharing_71.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
    // 1. Объявление логгера.
    // Обычно делают static final, чтобы он был один на весь класс.
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public void createUser(String username, int age) {
        // 2. Использование уровней (INFO) и плейсхолдеров ({})
        // Вместо конкатенации ("User " + username + " created" ) используем {}
        logger.info("Запрос на создание пользователя: {}", username);

        if (age < 18) {
            // Уровень предупреждения (WARN)
            logger.warn("Пользователь {} несовершеннолетний ({})", username, age);
        }

        try {
            saveToDatabase(username);
            logger.debug("Пользователь {} успешно сохранен в БД", username);
        } catch (Exception e) {
            // 3. Логгирование ошибки (ERROR) с передачей самого исключения
            // Stack trace запишется автоматически
            logger.error("Ошибка при сохранении пользователя {}", username, e);
        }
    }

    // Метод имитации сохранения в БД
    private void saveToDatabase(String user) {
        // Последовательность для сохранения данных в бд
        // иммитация
        throw new RuntimeException("База данных недоступна");
    }
}
