package org.example.carsharing_71.api;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для представления ошибки в API.
 * Единый формат JSON-ошибки для всех контроллеров (REST).
 *
 * <p>Содержит информацию об ошибке, включая:
 * - HTTP-статус
 * - Сообщение об ошибке
 * - Дополнительные детали</p>
 */
public class ApiError {
    /**
     * Текущее время
     */
    private Instant timestamp = Instant.now();
    /**
     * HTTP-статус
     */
    private int status;
    /**
     * Ошибка (код)
     */
    private String error;
    /**
     * Сообщение о ошибке в развернутом виде
     */
    private String message;
    /**
     * Путь (где возникла ошибка)
     */
    private String path;
    /**
     * Список элементов, непрошедших валидацию.
     */
    private List<Violation> violations = new ArrayList<>();

    public Instant getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

    public void setViolations(List<Violation> violations) {
        this.violations = violations;
    }
    public List<Violation> getViolations() {
        return violations;
    }

    /**
     * Элемент нарушени валидации.
     * Описывает конкретную проблему с полем/параметром: имя и человекочитаемое сообщение
     */
    public static class Violation {
        /**
         * Имя проблемного поля/параметра
         * (например, {@code startAt}, {@code seats}, {@code userId}, ...)
         */
        private String fields;
        /**
         * Сообщение для пользователя (например, "must be a future date", "must be positive")
         */
        private String message;

        public Violation() {
        }
        public Violation(String fields, String message) {
            this.fields = fields;
            this.message = message;
        }

        public String getFields() {
            return fields;
        }
        public String getMessage() {
            return message;
        }
        public void setFields(String fields) {
            this.fields = fields;
        }
        public void setMessage(String message) {
            this.message = message;
        }
    }

}
