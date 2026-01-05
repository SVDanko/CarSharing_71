package org.example.carsharing_71.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;

//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
public class SecurityConfigurationBase {
/*
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth     // настройка правил доступа
                        .requestMatchers("/public/**").permitAll()        // доступ без аутентификации
                        .requestMatchers("/admin/**").hasRole("ADMIN")    // только для роли ADMIN
                        .anyRequest().authenticated()                       // все остальные запросы требуют аутентификацию
                )
                .formLogin(form -> form         // настройка формы входа
                        .loginPage("/login")                                // кастомная страница входа
                        .permitAll()                                            // доступ к странице входа для всех
                )
                .logout(LogoutConfigurer::permitAll);                         // вариант через ссылку


        return http.build();
    }

 */
}
