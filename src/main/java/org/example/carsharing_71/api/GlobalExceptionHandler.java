package org.example.carsharing_71.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

/**
 * Обработчик глобальных исключений REST
 *
 * <p>Обрабатывает стандартные и пользовательские исключения,
 * преобразуя их в ApiError с корректными HTTP-статусами.</p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        // Создаем объект для хранения информации об ошибке
        ApiError error = new ApiError();
        error.setStatus(HttpStatus.UNPROCESSABLE_CONTENT.value());
        error.setError(HttpStatus.UNPROCESSABLE_CONTENT.getReasonPhrase());
        error.setMessage("Validation failed");
        error.setPath(request.getRequestURI());
//        FieldError fe = ex.getBindingResult().getFieldError();

        // Сокращенный вариант
        ex.getBindingResult().getFieldErrors().forEach(fe ->
                error.getViolations().add(new ApiError.Violation(fe.getField(), fe.getDefaultMessage())));

        /*
        // Полный вариант
        List<FieldError> fe = ex.getBindingResult().getFieldErrors();

        for (FieldError fieldError : fe) {
            error.getViolations().add(new ApiError.Violation(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        */
        return ResponseEntity.unprocessableContent().body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        ApiError error = new ApiError();
        error.setStatus(HttpStatus.UNPROCESSABLE_CONTENT.value());
        error.setError(HttpStatus.UNPROCESSABLE_CONTENT.getReasonPhrase());
        error.setMessage("Validation failed");
        error.setPath(request.getRequestURI());

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String field = violation.getPropertyPath().toString();
            error.getViolations().add(new ApiError.Violation(field, violation.getMessage()));
        }

        return ResponseEntity.unprocessableContent().body(error);
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<ApiError> handleServletExceptions(
            ServletException ex,
            HttpServletRequest request
    ) {
        Throwable cause = ex.getCause();
        if (cause instanceof ConstraintViolationException cve) {
            ApiError error = new ApiError();
            error.setStatus(HttpStatus.UNPROCESSABLE_CONTENT.value());
            error.setError(HttpStatus.UNPROCESSABLE_CONTENT.getReasonPhrase());
            error.setMessage("Validation failed");
            error.setPath(request.getRequestURI());
            for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
                String field = violation.getPropertyPath().toString();
                error.getViolations().add(new ApiError.Violation(field, violation.getMessage()));
            }
            return ResponseEntity.unprocessableContent().body(error);
        }
        ApiError error = new ApiError();
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        error.setMessage(ex.getMessage());
        error.setPath(request.getRequestURI());
        return ResponseEntity.internalServerError().body(error);
    }
}
