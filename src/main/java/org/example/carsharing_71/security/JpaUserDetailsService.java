package org.example.carsharing_71.security;

import lombok.extern.slf4j.Slf4j;
import org.example.carsharing_71.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JpaUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Trying to load user by login: {}", username);

        // Ищем пользователя в нашей БД через репозиторий
        return userRepository.findByLogin(username)
                // Используем паттерн Builder
                .map(user -> {
                    log.info("User found: id = {}, login = {}, role = {}",
                            user.getId(), user.getLogin(), user.getRole().name());

                    return User.builder()
                            // Передаем логин
                            .username(user.getLogin())
                            // Передаем пароль
                            .password(user.getPassword())
                            // Передаем роль
                            .roles(user.getRole().name())
                            .build();
                })
                .orElseThrow(() -> {
                    log.warn("User not found by login: " + username);
                    // Это исключение будет перехвачено и превращено в "Bad Credentials" (401)
                    return new UsernameNotFoundException("User not found: " + username);
                });
    }
}
