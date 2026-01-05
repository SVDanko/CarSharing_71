package org.example.carsharing_71.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Отключаем CSRF для чистого REST (иначе POST/PUT/PATCH/DELETE без CSRF‑токена дадут 403)
        http.csrf(AbstractHttpConfigurer::disable);
        // Правила доступа к маршрутам
        http.authorizeHttpRequests(auth -> auth
                // Документация и спецификация должны быть доступны без входа,
                // иначе тест/фронт не смогут зайти в Swagger UI
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // Публичный health‑чек для проверки живости сервиса
                .requestMatchers(HttpMethod.GET, "/api/health").permitAll()
                // Диагностика текущего пользователя/ролей (для отладки входа)
                .requestMatchers(HttpMethod.GET, "/api/whoami").permitAll()
                // Админ‑маршруты доступны только пользователям с ролью ADMIN
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // Остальные эндпоинты требуют аутентификации
                .anyRequest().authenticated()
        );
        // Включаем HTTP Basic аутентификацию с настройками по умолчанию:
        // браузер/клиент присылает заголовок Authorization: Basic <base64(user:pass)>,
        // фильтры Spring Security валидируют креды (учетные данные) и создают SecurityContext
        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    UserDetailsService users() {
        // Тестовые пользователи в памяти: удобно для демонстрации и интеграционных тестов
        // {noop} означает «без шифрования» (не использовать в продакшене)
        UserDetails user = User.withUsername("user").password("{noop}pass").roles("USER").build();
        UserDetails admin = User.withUsername("admin").password("{noop}admin").roles("ADMIN").build();
        return new InMemoryUserDetailsManager(user, admin);
    }
}
