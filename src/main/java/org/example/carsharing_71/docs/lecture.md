# Продвинутый Spring Data JPA: Полный конспект

## 1. Введение: От простого к эффективному

### 1.1 Почему базовых методов недостаточно?

На начальных этапах разработки методы `findAll()` и `findById()` кажутся простыми и удобными. Однако по мере роста приложения возникают серьезные проблемы:

**Проблема производительности:**
```java
// Кажется безобидным, но...
List<Car> allCars = carRepository.findAll(); // Загружает ВСЕ машины в память!
```
Если в базе 10 000 записей, приложение попытается загрузить их все одновременно, что приведет к:
- Высокому потреблению памяти (возможен OutOfMemoryError)
- Долгому времени отклика (пользователь ждет секунды/минуты)
- Избыточной нагрузке на сеть между БД и приложением

**Проблема N+1 запросов:**
```java
List<Car> cars = carRepository.findAll(); // 1 запрос
for (Car car : cars) {
    // Каждое обращение к связанной сущности = новый запрос к БД!
    String brand = car.getModel().getBrand(); // +1 запрос
    String city = car.getLocation().getCity(); // +1 запрос
}
// Итого: 1 + (N * 2) запросов, где N - количество машин
```

**Проблема избыточности данных:**
```java
// Для выпадающего списка нужны только ID и название
// Но мы тянем всю сущность со всеми полями
List<Car> cars = carRepository.findAll(); 
// Загружены: id, plateNumber, vin, status, year, mileage, price, 
// createdAt, updatedAt, description, и все связанные объекты...
```

### 1.2 Принципы эффективного слоя данных

1. **Выбирать только необходимое** (проекции)
2. **Загружать связанные данные заранее** (EntityGraph)
3. **Работать порциями** (пагинация)
4. **Контролировать транзакции** (@Transactional)
5. **Использовать кастомные запросы** (JPQL/Native SQL)

---

## 2. Проблема N+1 и способы её решения

### 2.1 Детальный разбор проблемы

**Что происходит под капотом:**

```java
// Шаг 1: Получаем список машин
List<Car> cars = carRepository.findAll();
// SQL: SELECT * FROM cars
```

```java
// Шаг 2: Обращаемся к связанным сущностям
for (Car car : cars) {
    System.out.println(car.getModel().getBrand());
    // SQL: SELECT * FROM models WHERE id = ?  (для каждой машины!)
}
```

**Почему это происходит?**
По умолчанию JPA использует **Lazy Loading** для связей `@ManyToOne` и `@OneToOne`. Это значит, что связанный объект загружается только при первом обращении к нему.

**Последствия:**
- Для 100 машин: 1 + 100 = **101 запрос** к базе данных
- Для 1000 машин: **1001 запрос**
- Каждый запрос имеет накладные расходы (network latency, parsing)

### 2.2 Решение 1: @EntityGraph

**Базовое использование:**
```java
public interface CarRepository extends JpaRepository<Car, Long> {
    
    @EntityGraph(attributePaths = {"model"})
    List<Car> findAll();
    
    // Spring Data сгенерирует:
    // SELECT c.*, m.* FROM cars c 
    // LEFT OUTER JOIN models m ON c.model_id = m.id
}
```

**Загрузка нескольких связей:**
```java
@EntityGraph(attributePaths = {"model", "location"})
List<Car> findByStatus(CarStatus status);

// SQL: SELECT c.*, m.*, l.* FROM cars c
//      LEFT JOIN models m ON c.model_id = m.id
//      LEFT JOIN locations l ON c.location_id = l.id
//      WHERE c.status = ?
```

**Загрузка вложенных связей:**
```java
// У Model есть поле brand типа Brand
@EntityGraph(attributePaths = {"model.brand", "location"})
List<Car> findAvailableCars();

// Загрузит Car -> Model -> Brand и Car -> Location
```

**Named EntityGraph (переиспользуемый):**
```java
@Entity
@NamedEntityGraph(
    name = "Car.fullDetails",
    attributeNodes = {
        @NamedAttributeNode("model"),
        @NamedAttributeNode("location"),
        @NamedAttributeNode("reservations")
    }
)
public class Car {
    // ...
}

// Использование в репозитории
@EntityGraph("Car.fullDetails")
List<Car> findAll();
```

### 2.3 Решение 2: JOIN FETCH в JPQL

**Явное указание JOIN:**
```java
@Query("SELECT c FROM Car c " +
       "JOIN FETCH c.model " +
       "JOIN FETCH c.location " +
       "WHERE c.status = :status")
List<Car> findByStatusWithDetails(@Param("status") CarStatus status);
```

**Разница между EntityGraph и JOIN FETCH:**
- `@EntityGraph` использует `LEFT OUTER JOIN` (вернет машины даже без модели)
- `JOIN FETCH` использует `INNER JOIN` (вернет только машины с моделью)
- `LEFT JOIN FETCH` - комбинация преимуществ обоих подходов

### 2.4 Когда НЕ использовать Eager Loading

**Декартово произведение:**
```java
// ОПАСНО! Если у машины 10 бронирований и 5 фото,
// вернется 50 строк для одной машины!
@EntityGraph(attributePaths = {"reservations", "photos"})
List<Car> findAll();
```

**Решение для множественных коллекций:**
```java
// Вариант 1: Загружать коллекции отдельно
Car car = carRepository.findById(id).orElseThrow();
List<Reservation> reservations = reservationRepository.findByCar(car);

// Вариант 2: Использовать два запроса с EntityGraph
@EntityGraph(attributePaths = {"reservations"})
Optional<Car> findWithReservationsById(Long id);

@EntityGraph(attributePaths = {"photos"})
Optional<Car> findWithPhotosById(Long id);
```

---

## 3. Пагинация и Сортировка

### 3.1 Основы пагинации

**Зачем нужна пагинация?**
- Ограничение объема данных за один запрос
- Улучшение времени отклика
- Снижение нагрузки на сеть и память
- Лучший UX (пользователь видит результаты быстрее)

**Интерфейс Pageable:**
```java
public interface Pageable {
    int getPageNumber();     // Номер страницы (0-based)
    int getPageSize();       // Размер страницы
    Sort getSort();          // Параметры сортировки
    long getOffset();        // Смещение = pageNumber * pageSize
}
```

### 3.2 Использование пагинации в репозитории

**Базовый пример:**
```java
public interface CarRepository extends JpaRepository<Car, Long> {
    
    // Возвращает Page<Car> вместо List<Car>
    Page<Car> findByStatus(CarStatus status, Pageable pageable);
    
    // Можно комбинировать с другими возможностями
    @EntityGraph(attributePaths = {"model", "location"})
    Page<Car> findByLocation_City(String city, Pageable pageable);
}
```

**Создание Pageable объекта:**
```java
// Страница 0, размер 10, сортировка по цене по убыванию
Pageable pageable = PageRequest.of(0, 10, Sort.by("price").descending());

// Множественная сортировка
Pageable pageable = PageRequest.of(0, 20, 
    Sort.by("status").ascending()
        .and(Sort.by("price").descending())
);

// Без сортировки
Pageable pageable = PageRequest.of(0, 10);

// Несортированная выборка (ещё быстрее)
Pageable pageable = Pageable.unpaged(); // Вернет все записи
```

### 3.3 Работа с результатами (Page)

**Структура Page:**
```java
Page<Car> page = carRepository.findByStatus(CarStatus.AVAILABLE, pageable);

// Основные данные
List<Car> cars = page.getContent();           // Список элементов
int totalPages = page.getTotalPages();        // Всего страниц
long totalElements = page.getTotalElements(); // Всего элементов

// Информация о текущей странице
int currentPage = page.getNumber();           // Номер текущей страницы
int pageSize = page.getSize();                // Размер страницы
int numberOfElements = page.getNumberOfElements(); // Элементов на странице

// Навигация
boolean hasNext = page.hasNext();             // Есть ли следующая страница
boolean hasPrevious = page.hasPrevious();     // Есть ли предыдущая
boolean isFirst = page.isFirst();             // Первая ли это страница
boolean isLast = page.isLast();               // Последняя ли
```

**Пример использования в контроллере:**
```java
@RestController
@RequestMapping("/api/cars")
public class CarController {
    
    @GetMapping
    public ResponseEntity<PagedResponse<CarDto>> getCars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") 
            ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<Car> carPage = carRepository.findAll(pageable);
        
        PagedResponse<CarDto> response = new PagedResponse<>(
            carPage.getContent().stream().map(this::toDto).toList(),
            carPage.getNumber(),
            carPage.getSize(),
            carPage.getTotalElements(),
            carPage.getTotalPages(),
            carPage.isLast()
        );
        
        return ResponseEntity.ok(response);
    }
}
```

### 3.4 Slice вместо Page

**Когда использовать Slice:**
```java
// Slice не делает COUNT запрос - быстрее для больших таблиц
Slice<Car> findByStatus(CarStatus status, Pageable pageable);
```

**Разница между Page и Slice:**
- `Page`: делает дополнительный `SELECT COUNT(*)` - знает общее количество
- `Slice`: не считает общее количество - знает только есть ли следующая страница
- `Slice` быстрее на больших таблицах, где COUNT - дорогая операция

```java
Slice<Car> slice = carRepository.findByStatus(CarStatus.AVAILABLE, pageable);

// Доступны методы
List<Car> content = slice.getContent();
boolean hasNext = slice.hasNext();
// НЕ доступны getTotalPages(), getTotalElements()
```

---

## 4. Проекции (Projections)

### 4.1 Зачем нужны проекции?

**Проблема избыточности:**
```java
// Для выпадающего списка нужны только 2 поля
// Но загружается вся сущность целиком
List<Car> cars = carRepository.findAll();
// SELECT id, plate_number, vin, status, year, mileage, price, 
//        created_at, updated_at, description, model_id, location_id
// FROM cars
```

**Решение - проекция:**
```java
// Загрузим только нужные поля
List<CarDropdownView> cars = carRepository.findAllProjectedBy();
// SELECT id, plate_number FROM cars
```

### 4.2 Интерфейсные проекции (Interface-based)

**Простая проекция:**
```java
public interface CarDropdownView {
    Long getId();
    String getPlateNumber();
}

// В репозитории
public interface CarRepository extends JpaRepository<Car, Long> {
    List<CarDropdownView> findAllProjectedBy();
    List<CarDropdownView> findByStatus(CarStatus status);
}
```

**Вложенные проекции:**
```java
public interface CarWithModelView {
    Long getId();
    String getPlateNumber();
    ModelView getModel(); // Вложенная проекция
    
    interface ModelView {
        String getBrand();
        String getModelName();
    }
}

// SQL: SELECT c.id, c.plate_number, m.brand, m.model_name
//      FROM cars c
//      JOIN models m ON c.model_id = m.id
```

**SpEL выражения в проекциях:**
```java
public interface CarSummaryView {
    Long getId();
    
    // Конкатенация строк
    @Value("#{target.model.brand + ' ' + target.model.modelName}")
    String getFullName();
    
    // Вычисления
    @Value("#{target.price * 0.9}")
    BigDecimal getDiscountedPrice();
    
    // Условная логика
    @Value("#{target.year > 2020 ? 'New' : 'Used'}")
    String getCondition();
}
```

### 4.3 Классовые проекции (Class-based / DTO)

**DTO класс:**
```java
public class CarListDto {
    private Long id;
    private String plateNumber;
    private String modelName;
    private BigDecimal price;
    
    // Конструктор должен соответствовать запросу!
    public CarListDto(Long id, String plateNumber, String modelName, BigDecimal price) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.modelName = modelName;
        this.price = price;
    }
    
    // Геттеры
}
```

**Использование в JPQL:**
```java
@Query("SELECT new com.example.dto.CarListDto(" +
       "c.id, c.plateNumber, c.model.modelName, c.price) " +
       "FROM Car c " +
       "WHERE c.status = :status")
List<CarListDto> findCarListByStatus(@Param("status") CarStatus status);
```

### 4.4 Динамические проекции

**Универсальный метод с выбором проекции:**
```java
public interface CarRepository extends JpaRepository<Car, Long> {
    
    // T может быть любой проекцией
    <T> List<T> findByStatus(CarStatus status, Class<T> type);
}

// Использование
List<CarDropdownView> dropdown = carRepository.findByStatus(
    CarStatus.AVAILABLE, 
    CarDropdownView.class
);

List<CarDetailView> details = carRepository.findByStatus(
    CarStatus.AVAILABLE, 
    CarDetailView.class
);
```

### 4.5 Closed vs Open проекции

**Closed projection (рекомендуется):**
```java
// Все методы соответствуют полям сущности
// Spring может оптимизировать запрос
public interface CarClosedProjection {
    Long getId();
    String getPlateNumber(); // Есть поле plateNumber в Car
}
```

**Open projection (медленнее):**
```java
// Использует SpEL, загружает всю сущность
public interface CarOpenProjection {
    @Value("#{target.model.brand + ' ' + target.year}")
    String getDescription(); // Вычисляемое поле
}
```

---

## 5. JPQL (Java Persistence Query Language)

### 5.1 Основы JPQL

**Ключевые отличия от SQL:**
- Работает с **классами Java**, а не таблицами БД
- Работает с **полями Java**, а не колонками
- Независим от конкретной СУБД
- Использует имена сущностей, не имена таблиц

**Пример сравнения:**
```sql
-- SQL
SELECT * FROM cars c
JOIN models m ON c.model_id = m.id
WHERE m.brand = 'Toyota'
```

```java
// JPQL
SELECT c FROM Car c
JOIN c.model m
WHERE m.brand = 'Toyota'
```

### 5.2 Базовый синтаксис

**SELECT запросы:**
```java
// Выбрать все машины
@Query("SELECT c FROM Car c")
List<Car> getAllCars();

// С условием WHERE
@Query("SELECT c FROM Car c WHERE c.status = :status")
List<Car> findByStatus(@Param("status") CarStatus status);

// Множественные условия
@Query("SELECT c FROM Car c " +
       "WHERE c.status = :status " +
       "AND c.price <= :maxPrice " +
       "AND c.year >= :minYear")
List<Car> findAvailableInPriceRange(
    @Param("status") CarStatus status,
    @Param("maxPrice") BigDecimal maxPrice,
    @Param("minYear") int minYear
);
```

**JOIN в JPQL:**
```java
// INNER JOIN (только машины с моделью)
@Query("SELECT c FROM Car c " +
       "JOIN c.model m " +
       "WHERE m.brand = :brand")
List<Car> findByBrand(@Param("brand") String brand);

// LEFT JOIN (машины даже без модели)
@Query("SELECT c FROM Car c " +
       "LEFT JOIN c.model m " +
       "WHERE m.brand = :brand OR m.brand IS NULL")
List<Car> findByBrandOrNull(@Param("brand") String brand);

// JOIN с дополнительным условием
@Query("SELECT c FROM Car c " +
       "JOIN c.location l " +
       "WHERE l.city = :city " +
       "AND l.isActive = true")
List<Car> findInActiveCity(@Param("city") String city);
```

### 5.3 Агрегатные функции

**COUNT, SUM, AVG, MIN, MAX:**
```java
// Подсчет количества
@Query("SELECT COUNT(c) FROM Car c WHERE c.status = :status")
long countByStatus(@Param("status") CarStatus status);

// Средняя цена
@Query("SELECT AVG(c.price) FROM Car c WHERE c.status = 'AVAILABLE'")
BigDecimal getAveragePrice();

// Группировка
@Query("SELECT c.model.brand, COUNT(c) FROM Car c " +
       "GROUP BY c.model.brand " +
       "ORDER BY COUNT(c) DESC")
List<Object[]> getCarCountByBrand();

// С HAVING
@Query("SELECT c.location, COUNT(c) FROM Car c " +
       "GROUP BY c.location " +
       "HAVING COUNT(c) > :minCount")
List<Object[]> getLocationsWithManyCars(@Param("minCount") long minCount);
```

### 5.4 Подзапросы (Subqueries)

**EXISTS:**
```java
@Query("SELECT c FROM Car c " +
       "WHERE EXISTS (" +
       "  SELECT r FROM Reservation r " +
       "  WHERE r.car = c " +
       "  AND r.status = 'ACTIVE'" +
       ")")
List<Car> findCarsWithActiveReservations();
```

**IN с подзапросом:**
```java
@Query("SELECT c FROM Car c " +
       "WHERE c.model IN (" +
       "  SELECT m FROM Model m " +
       "  WHERE m.brand = :brand " +
       "  AND m.year >= :year" +
       ")")
List<Car> findByBrandAndRecentModel(
    @Param("brand") String brand,
    @Param("year") int year
);
```

### 5.5 Специальные функции

**LIKE и паттерны:**
```java
// Поиск по частичному совпадению
@Query("SELECT c FROM Car c " +
       "WHERE LOWER(c.plateNumber) LIKE LOWER(CONCAT('%', :search, '%'))")
List<Car> searchByPlateNumber(@Param("search") String search);
```

**Функции для дат:**
```java
@Query("SELECT c FROM Car c " +
       "WHERE YEAR(c.createdAt) = :year " +
       "AND MONTH(c.createdAt) = :month")
List<Car> findCreatedInMonth(
    @Param("year") int year,
    @Param("month") int month
);

// Машины, добавленные за последние 30 дней
@Query("SELECT c FROM Car c " +
       "WHERE c.createdAt >= CURRENT_DATE - 30")
List<Car> findRecentlyAdded();
```

**CASE выражения:**
```java
@Query("SELECT c.id, " +
       "CASE " +
       "  WHEN c.year >= 2022 THEN 'Brand New' " +
       "  WHEN c.year >= 2019 THEN 'Recent' " +
       "  ELSE 'Older' " +
       "END " +
       "FROM Car c")
List<Object[]> getCarsWithAgeCategory();
```

### 5.6 UPDATE и DELETE запросы

**@Modifying для изменяющих запросов:**
```java
@Modifying
@Transactional
@Query("UPDATE Car c SET c.status = :newStatus " +
       "WHERE c.status = :oldStatus " +
       "AND c.lastServiceDate < :date")
int bulkUpdateOldCarStatus(
    @Param("oldStatus") CarStatus oldStatus,
    @Param("newStatus") CarStatus newStatus,
    @Param("date") LocalDate date
);

@Modifying
@Transactional
@Query("DELETE FROM Reservation r " +
       "WHERE r.status = 'CANCELLED' " +
       "AND r.createdAt < :cutoffDate")
int deleteOldCancelledReservations(@Param("cutoffDate") LocalDateTime cutoffDate);
```

**Важно:**
- `@Modifying` обязательна для UPDATE/DELETE
- Метод должен быть `@Transactional`
- Возвращаемый `int` - количество затронутых строк
- Не обновляет persistence context (кеш Hibernate)

---

## 6. Native SQL запросы

### 6.1 Когда использовать Native SQL

**Случаи применения:**
- Специфичные функции конкретной СУБД (PostgreSQL, MySQL)
- Оптимизированные запросы с хинтами
- Сложные аналитические запросы
- Работа с legacy базами данных

**Базовый синтаксис:**
```java
@Query(value = "SELECT * FROM cars WHERE status = ?1", 
       nativeQuery = true)
List<Car> findByStatusNative(String status);
```

### 6.2 Проекции с Native SQL

**Возврат DTO:**
```java
// Интерфейсная проекция
public interface CarStatsProjection {
    String getBrand();
    Long getCarCount();
    Double getAvgPrice();
}

@Query(value = """
    SELECT m.brand as brand,
           COUNT(*) as carCount,
           AVG(c.price) as avgPrice
    FROM cars c
    JOIN models m ON c.model_id = m.id
    WHERE c.status = 'AVAILABLE'
    GROUP BY m.brand
    ORDER BY carCount DESC
    """, 
    nativeQuery = true)
List<CarStatsProjection> getCarStatsByBrand();
```

### 6.3 PostgreSQL специфичные возможности

**Full-text search:**
```java
@Query(value = """
    SELECT * FROM cars c
    WHERE to_tsvector('english', c.description) @@ to_tsquery('english', :searchQuery)
    """,
    nativeQuery = true)
List<Car> fullTextSearch(@Param("searchQuery") String searchQuery);
```

**JSON операции:**
```java
@Query(value = """
    SELECT * FROM cars c
    WHERE c.specifications->>'transmission' = :transmission
    AND (c.specifications->'engine'->>'power')::int >= :minPower
    """,
    nativeQuery = true)
List<Car> findByJsonSpecs(
    @Param("transmission") String transmission,
    @Param("minPower") int minPower
);
```

---

## 7. Транзакции (@Transactional)

### 7.1 Основы транзакций

**Что такое транзакция?**
Транзакция - это группа операций с БД, которые выполняются как единое целое:
- Либо выполняются ВСЕ операции (commit)
- Либо НИ ОДНА не выполняется (rollback)

**ACID свойства:**
- **Atomicity** (Атомарность): всё или ничего
- **Consistency** (Согласованность): БД переходит из одного валидного состояния в другое
- **Isolation** (Изолированность): транзакции не мешают друг другу
- **Durability** (Долговечность): результат сохраняется даже при сбое

### 7.2 Где ставить @Transactional

**Правило:** Аннотируйте методы **Сервисного слоя**, а не Репозитория или Контроллера.

**Правильно:**
```java
@Service
public class BookingService {
    
    private final CarRepository carRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentService paymentService;
    
    @Transactional
    public Reservation bookCar(Long carId, Long userId, BookingRequest request) {
        // 1. Проверяем доступность машины
        Car car = carRepository.findById(carId)
            .orElseThrow(() -> new CarNotFoundException(carId));
        
        if (car.getStatus() != CarStatus.AVAILABLE) {
            throw new CarNotAvailableException();
        }
        
        // 2. Создаем бронирование
        Reservation reservation = new Reservation();
        reservation.setCar(car);
        reservation.setUserId(userId);
        reservation.setStartDate(request.getStartDate());
        reservation.setEndDate(request.getEndDate());
        reservation.setStatus(ReservationStatus.PENDING);
        reservation = reservationRepository.save(reservation);
        
        // 3. Обновляем статус машины
        car.setStatus(CarStatus.RESERVED);
        carRepository.save(car);
        
        // 4. Обрабатываем платеж
        paymentService.processPayment(reservation, request.getPaymentDetails());
        
        // Если где-то произойдет ошибка, ВСЁ откатится
        return reservation;
    }
}
```

**Неправильно:**
```java
@RestController
public class BookingController {
    
    @Transactional // ❌ НЕТ! Транзакции в контроллере - плохая практика
    @PostMapping("/bookings")
    public ResponseEntity<BookingResponse> bookCar(@RequestBody BookingRequest request) {
        // ...
    }
}
```

### 7.3 Настройки @Transactional

**Propagation (распространение):**
```java
// REQUIRED (по умолчанию) - использует существующую или создает новую
@Transactional(propagation = Propagation.REQUIRED)
public void methodA() { }

// REQUIRES_NEW - всегда создает новую транзакцию
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void methodB() { }

// SUPPORTS - использует транзакцию если есть, иначе работает без неё
@Transactional(propagation = Propagation.SUPPORTS)
public void methodC() { }

// MANDATORY - требует существующую транзакцию
@Transactional(propagation = Propagation.MANDATORY)
public void methodD() { }

// NEVER - не должно быть транзакции
@Transactional(propagation = Propagation.NEVER)
public void methodE() { }
```

**Isolation (изолированность):**
```java
// READ_UNCOMMITTED - грязное чтение
@Transactional(isolation = Isolation.READ_UNCOMMITTED)

// READ_COMMITTED - предотвращает грязное чтение
@Transactional(isolation = Isolation.READ_COMMITTED)

// REPEATABLE_READ - предотвращает неповторяющееся чтение
@Transactional(isolation = Isolation.REPEATABLE_READ)

// SERIALIZABLE - полная изоляция (медленнее)
@Transactional(isolation = Isolation.SERIALIZABLE)
```

**ReadOnly optimization:**
```java
// Для запросов без изменений (Hibernate оптимизирует)
@Transactional(readOnly = true)
public List<Car> getAllAvailableCars() {
    return carRepository.findByStatus(CarStatus.AVAILABLE);
}
```

**Timeout:**
```java
// Откат если транзакция длится больше 30 секунд
@Transactional(timeout = 30)
public void longRunningOperation() {
    // ...
}
```

**Rollback rules:**
```java
// Откатываться только на checked исключениях
@Transactional(rollbackFor = Exception.class)
public void methodA() { }

// НЕ откатываться на определенном исключении
@Transactional(noRollbackFor = BusinessException.class)
public void methodB() { }
```

### 7.4 Распространенные ошибки

**Ошибка 1: Вызов @Transactional метода из того же класса**
```java
@Service
public class CarService {
    
    public void publicMethod() {
        this.transactionalMethod(); // ❌ Транзакция НЕ сработает!
    }
    
    @Transactional
    private void transactionalMethod() {
        // Транзакция не откроется, т.к. вызов идет напрямую, а не через прокси
    }
}
```

**Решение:**
```java
@Service
public class CarService {
    
    @Transactional
    public void publicTransactionalMethod() {
        // Вызывается извне через прокси - транзакция работает ✓
    }
}
```

**Ошибка 2: Обработка исключений без пробрасывания**
```java
@Transactional
public void methodWithWrongExceptionHandling() {
    try {
        carRepository.save(car);
        throw new RuntimeException("Error!");
    } catch (Exception e) {
        log.error("Error", e);
        // ❌ Исключение поглощено - транзакция не откатится!
    }
}
```

**Решение:**
```java
@Transactional
public void methodWithCorrectExceptionHandling() {
    try {
        carRepository.save(car);
    } catch (DataIntegrityViolationException e) {
        log.error("Database constraint violated", e);
        throw new BusinessException("Cannot save car", e); // ✓ Пробросили
    }
}
```

---

## Словарь терминов

**DAO (Data Access Object)** - паттерн проектирования для инкапсуляции логики доступа к данным.

**N+1 Problem** - антипаттерн, когда для N записей выполняется 1 + N запросов к БД.

**Lazy Loading** - отложенная загрузка связанных данных при первом обращении.

**Eager Loading** - немедленная загрузка связанных данных вместе с основной сущностью.

**JPQL** - объектно-ориентированный язык запросов JPA (оперирует классами, а не таблицами).

**Projection** - выборка подмножества полей сущности вместо всего объекта.

**EntityGraph** - граф сущностей для указания, какие связи загружать вместе с основной сущностью.

**Pageable** - объект для задания параметров пагинации (номер страницы, размер, сортировка).

**Page** - результат пагинированного запроса, содержит данные + метаинформацию (всего страниц, элементов).

**Slice** - облегченная версия Page без подсчета общего количества элементов.

**Specification** - предикат для динамического построения запросов (Criteria API).

**Transactional Boundary** - граница транзакции (начало и конец).

**Propagation** - поведение транзакции при вызове одного @Transactional метода из другого.

**Isolation Level** - уровень изоляции транзакций (READ_COMMITTED, SERIALIZABLE и т.д.).

**Optimistic Locking** - оптимистичная блокировка через версионирование (@Version).

**Pessimistic Locking** - пессимистичная блокировка через SELECT FOR UPDATE.

**Second Level Cache** - кэш второго уровня Hibernate (разделяется между сессиями).

**Query Cache** - кэш результатов запросов Hibernate.

**Dirty Checking** - механизм автоматического обнаружения изменений в сущностях.

**Persistence Context** - контекст персистентности, "карта" управляемых Hibernate объектов.

**Managed Entity** - сущность, находящаяся под управлением Hibernate.

**Detached Entity** - сущность, которая была под управлением, но сессия закрылась.

---
