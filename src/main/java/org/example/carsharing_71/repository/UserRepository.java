package org.example.carsharing_71.repository;

import org.example.carsharing_71.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для работы с пользователями в базе данных.
 * 
 * <p>Предоставляет стандартные CRUD-операции через наследование от JpaRepository.
 * Поддерживает создание кастомных запросов через соглашение об именовании методов.</p>
 * 
 * <p>Примеры использования:
 * - Поиск пользователя по email
 * - Проверка существования пользователя с определенным email
 * - Получение пользователей по различным критериям</p>
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Проверяет существование пользователя с указанным email.
     * 
     * @param email email для проверки
     * @return true если пользователь с таким email существует, иначе false
     * 
     * @note В текущей реализации есть опечатка в имени метода (exist вместо exists).
     *       Для корректной работы следует использовать existsByEmail.
     */
//    boolean existsByEmail(String email);
}
