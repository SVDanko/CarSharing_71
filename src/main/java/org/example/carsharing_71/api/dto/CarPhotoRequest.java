package org.example.carsharing_71.api.dto;

/**
 * DTO для запроса на добавление фотографии автомобиля.
 * <p>
 * Содержит только URL изображения, который будет сохранён для указанного автомобиля.
 */
public class CarPhotoRequest {
    private String url;

    /**
     * Публичный URL изображения.
     */
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
