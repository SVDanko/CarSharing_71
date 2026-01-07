package org.example.carsharing_71.config;

import org.example.carsharing_71.security.JpaUserDetailsService;
import org.springframework.boot.CommandLineRunner;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JpaUserDetailsService userDetailsService;

    public SecurityConfiguration(JpaUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Отключаем CSRF для чистого REST (иначе POST/PUT/PATCH/DELETE без CSRF‑токена дадут 403)
        http.csrf(AbstractHttpConfigurer::disable);
        // Правила доступа к маршрутам
        http.authorizeHttpRequests(auth -> auth
                // Документация и спецификация должны быть доступны без входа,
                // иначе тест/фронт не смогут зайти в Swagger UI
                .requestMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**"
                ).permitAll()

                // Публичный health‑чек для проверки живости сервиса
                .requestMatchers(HttpMethod.GET, "/api/health").permitAll()

                // Диагностика текущего пользователя/ролей (для отладки входа)
                .requestMatchers(HttpMethod.GET, "/api/whoami").permitAll()

                // Админ‑маршруты доступны только пользователям с ролью ADMIN
                // ВАЖНО: hasRole("ADMIN") ожидает, что в БД роль записана или
                // мапится в Authority как "ROLE_ADMIN".
                // Если у вас в БД просто "ADMIN", убедитесь, что JpaUserDetailsService добавляет префикс "ROLE_".
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // Остальные эндпоинты требуют аутентификации
                .anyRequest().authenticated()
        );
        // Подключаем наш UserDetailsService
        // Явно указываем использовать наш сервис для БД
        http.userDetailsService(userDetailsService);

        // Включаем HTTP Basic аутентификацию с настройками по умолчанию:
        // браузер/клиент присылает заголовок Authorization: Basic <base64(user:pass)>,
        // фильтры Spring Security валидируют креды (учетные данные) и создают SecurityContext
        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }

//    @Bean
//    UserDetailsService users() {
//        // Тестовые пользователи в памяти: удобно для демонстрации и интеграционных тестов
//        // {noop} означает «без шифрования» (не использовать в продакшене)
//        UserDetails user = User.withUsername("user").password("{noop}pass").roles("USER").build();
//        UserDetails admin = User.withUsername("admin").password("{noop}admin").roles("ADMIN").build();
//        UserDetails student = User.withUsername("student").password("{noop}student").roles("STUDENT").build();
//        return new InMemoryUserDetailsManager(user, admin, student);
//    }

    // ОБЯЗАТЕЛЬНО: Если мы используем БД, нам нужен энкодер паролей.
    // Без него Spring выдаст ошибку "There is no PasswordEncoder mapped for the id null"
    // при попытке входа, если пароли в БД захешированы.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
