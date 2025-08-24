package ru.yandex.practicum.store.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.response.ExceptionResponse;
import ru.yandex.practicum.store.exception.ProductNotFoundException;

@RestControllerAdvice
public class StoreExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleProductNotFoundException(final ProductNotFoundException exception) {
        return new ExceptionResponse("Продукт не найден на витрине", exception.getMessage(), HttpStatus.NOT_FOUND);
    }
}
