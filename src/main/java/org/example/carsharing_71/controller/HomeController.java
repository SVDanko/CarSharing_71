package org.example.carsharing_71.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * MVC‑контроллер главной страницы.
 *
 * Возвращает имя шаблона {@code index}, который рендерится тонким серверным
 * шаблонизатором Thymeleaf. В учебном проекте MVC используется для простых
 * демонстрационных страниц и админки, а основной клиентский доступ будет через REST.
 */

@Controller
public class HomeController {
    /**
     * Маршрут корня сайта. Добавляет базовые данные в модель и отдаёт шаблон.
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("appName", "Car-Sharing");
        model.addAttribute("healthUrl", "/api/health");
        return "index";     // имя шаблона без расширения
    }
}
