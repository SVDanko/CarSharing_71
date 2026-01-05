package org.example.carsharing_71.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {
    public void login(String username, String password) {
        log.info("Попытка входа пользователя: {}", username);
        // НЕ логируем пароль!!!

        boolean success = authenticate(username, password);

        if (success) {
            log.info("Успешный вход пользователя: {}", username);
        } else {
            log.warn("Неудачная попытка входа для пользователя: {}", username);
        }
    }

    public void processCreditCard(String cardNumber) {
        String maskedCard = maskCardNumber(cardNumber);
        log.info("Обработка карты: {}", maskedCard);
    }


    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }

    private boolean authenticate(String name, String password) {
        // В реальности более сложная логика проверки
        if (name.equals("admin") && password.equals("admin")) {
            return true;
        }
        return false;
    }
}
