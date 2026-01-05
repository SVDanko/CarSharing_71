package org.example.carsharing_71.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация OpenAPI/Swagger для генерации документации REST API.
 * <p>
 * Регистрирует бин {@link OpenAPI} c базовой информацией о сервисе:
 * название, версия и описание. Дальнейшие аннотации в контроллерах
 * дополняют схему конкретными эндпоинтами.
 */
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Car-Sharing API")
                        .version("v1")
                        .description("Документация REST-эндпоинтов проекта Car-Sharing")
                );
    }
}
