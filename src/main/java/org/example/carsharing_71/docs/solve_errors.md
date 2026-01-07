Ошибка:
org.springframework.dao.InvalidDataAccessApiUsageException: Unable to locate Attribute with the given name [car_models] on this ManagedType [org.example.carsharing_71.domain.Car]

Данная ошибка связана с тем, что мы в нашем классе Car используем поле не "model", а "carModel".
Поэтому есть два решения:
```java
    @EntityGraph(attributePaths = {"carModel", "location"})
    List<Car> findAll();
```
или в Car название данного поля поменять на "model".
**Важное замечание:** в attributePaths должны быть названия полей соответствующего класса

Ошибка с аутентификацией была связана с тем, что у нас в enum Role было так:
```java
public enum Role {
    ROLE_ADMIN,
    ROLE_MANAGER,
    ROLE_USER,
    ROLE_STUDENT
}
```
но в нашем сервисе JpaUserDetailsService есть код:
```java
.roles(user.getRole().name()) // Spring Security сам добавляет "ROLE_"
```
В нашем YAML файле:

```YAML
- column: { name: role, value: "ROLE_ADMIN" }
```

Что произходит в таком случае:
- Spring загрузит из БД строку "ROLE_ADMIN".
- Метод .roles() добавит префикс, и получится право доступа: ROLE_ROLE_ADMIN.
- В конфиге безопасности проверка .hasRole("ADMIN") ищет ROLE_ADMIN.
Результат: Несовпадение. Доступ запрещен (403 Forbidden).

✅ Исправленный YAML (ChangeSet)
Измените значение роли админа с "ROLE_ADMIN" на "ADMIN", чтобы оно соответствовало остальным пользователям и логике вашего кода.

```YAML
databaseChangeLog:
- changeSet:
  id: 002-seed-users
  author: DevDanko
  changes:
  - insert:
  tableName: users
  columns:
  # user "admin"
  - column: { name: login, value: "admin" }
  # Пароль для admin (обычно 'admin' или что вы там зашифровали)
  - column: { name: password, value: "$2a$10$depV2yiIideoUNx0wUk74OqUqyGGHpkzHA7AZSWYXPFrIHt.Ft6py" }
  - column: { name: email, value: "admin@example.com" }
  - column: { name: full_name, value: "Admin" }
  # ИСПРАВЛЕНО: убрали префикс ROLE_, теперь просто ADMIN
  - column: { name: role, value: "ADMIN" }
  - column: { name: created_at, value: "2025-01-10 09:00:00" }

            # user "user"
            - column: { name: login, value: "user" }
            # Пароль для user
            - column: { name: password, value: "$2a$10$JtmLrROanmzS1hjAzEeiDueDrN8280Hf73ho.m0eOI2nCMThaDIT." }
            - column: { name: email, value: "user@example.com" }
            - column: { name: full_name, value: "Test User" }
            - column: { name: role, value: "USER" }
            - column: { name: created_at, value: "2025-01-10 09:00:00" }
            
            # user "student"
            - column: { name: login, value: "student" }
            - column: { name: password, value: "$2a$10$JtmLrROanmzS1hjAzEeiDueDrN8280Hf73ho.m0eOI2nCMThaDIT." }
            - column: { name: email, value: "student@example.com" }
            - column: { name: full_name, value: "Student" }
            - column: { name: role, value: "STUDENT" } # Или USER, если нет такой роли STUDENT в Enum
            - column: { name: created_at, value: "2025-01-10 09:00:00" }
```
Так же исправьте enum Role:
```java
public enum Role {
    ADMIN,
    MANAGER,
    USER,
    STUDENT
}
```

Так же я добавил файлы для импорта большего количества данных в таблицы:
- 004-add-bulk-data.yaml;
- 005-add-missing-tables.yaml.

