# Пошаговая инструкция — Урок 8 (Security & Docker)

**Цель:** Перевести аутентификацию на базу данных и упаковать проект в Docker.

## Часть 1: Real Security (БД)

### Шаг 1: Подготовка UserDetailsService
Нам нужно научить Spring Security брать пользователей из нашей базы.

1.  Создайте пакет `security` внутри `org.exmple.carsharing`.
2.  Создайте класс `JpaUserDetailsService`:
```java
@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByLogin(username)
                .map(u -> org.springframework.security.core.userdetails.User.builder()
                        .username(u.getLogin())
                        .password(u.getPassword())
                        .roles(u.getRole().name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
```

### Шаг 2: Настройка SecurityConfiguration
1.  Откройте `SecurityConfiguration.java`.
2.  Удалите бин `userDetailsService` (который с in-memory пользователями).
3.  Добавьте бин для хеширования паролей:
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```
4.  В методе `securityFilterChain` добавьте `.userDetailsService(jpaUserDetailsService)` (нужно внедрить ваш сервис).

### Шаг 3: Миграция данных (Liquibase)
Наши текущие пользователи в БД (`seed-users.yaml`) имеют простые пароли ("pass", "admin"). BCrypt их не поймет.

1.  Откройте `src/main/resources/db/changelog/002-seed-data.yaml`.
2.  Замените пароли на хеши (пароль "password" = `$2a$10$X7.G...` — сгенерируйте онлайн генератором BCrypt или возьмите этот: `$2a$12$K.Xv7.9.Q.9.9.9.9.9.9.9`).
    *   *Совет:* Для урока проще удалить базу H2 (перезагрузить приложение) или дропнуть таблицу `users`, если используете Postgres, чтобы сиды накатились заново.
    *   *Правильный путь:* Создать новый чейнжсет `004-update-passwords.yaml` с `UPDATE users SET password = ...`.

### Шаг 4: Проверка
1.  Запустите приложение.
2.  Попробуйте войти через Postman или Браузер.
3.  Если входит — победа!

---

## Часть 2: Docker

### Шаг 1: Dockerfile
Создайте файл `Dockerfile` (без расширения) в корне проекта:
```dockerfile
# 1. Build Stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 2. Run Stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Шаг 2: Docker Compose
Создайте файл `docker-compose.yml` в корне проекта:
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/carsharing
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: password
      SPRING_JPA_HIBERNATE_DDL_AUTO: validate
    depends_on:
      - db
  
  db:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: carsharing
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

### Шаг 3: Запуск
1.  Откройте терминал в корне проекта.
2.  Выполните: `docker-compose up --build`.
3.  Ждите, пока соберется Maven и поднимется база.
4.  Проверьте `http://localhost:8080/api/health`.

Поздравляем! Ваше приложение готово к деплою. 🐳
