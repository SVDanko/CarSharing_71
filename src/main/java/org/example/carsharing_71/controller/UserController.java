package org.example.carsharing_71.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
//@EnableMethodSecurity
public class UserController {
    @GetMapping
    // Простой эндпоинт для демонстрации ролей:
    // доступен только для пользователей с ролью USER (см. SecurityConfiguration)
//    @PreAuthorize("hasRole('USER')")
    public String info() {
        return "user-ok";
    }
}
