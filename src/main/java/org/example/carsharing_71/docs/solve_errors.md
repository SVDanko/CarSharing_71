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