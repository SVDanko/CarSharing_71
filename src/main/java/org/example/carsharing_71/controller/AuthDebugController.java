package org.example.carsharing_71.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api")
public class AuthDebugController {

    @GetMapping("/whoami")
    public Map<String, Object> whoami(Authentication auth) {
        String name = auth != null ? auth.getName() : "anonymous";
        List<String> roles = auth != null
                ? auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())
                : List.of();

        return Map.of(
                "username", name,
                "roles", roles,
                "isAuthenticated", auth != null && auth.isAuthenticated()
        );
    }
}

