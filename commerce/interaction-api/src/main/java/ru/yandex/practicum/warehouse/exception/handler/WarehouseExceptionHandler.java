package ru.yandex.practicum.warehouse.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.response.ExceptionResponse;
import ru.yandex.practicum.warehouse.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.warehouse.exception.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.yandex.practicum.warehouse.exception.SpecifiedProductAlreadyInWarehouseException;

@RestControllerAdvice
public class WarehouseExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleNoSpecifiedProductInWarehouseException(final NoSpecifiedProductInWarehouseException exception) {
        return new ExceptionResponse("Данного товара нет на складе", exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleLowProductQuantityInWarehouseException(final ProductInShoppingCartLowQuantityInWarehouseException exception) {
        return new ExceptionResponse("Данного товара на складе недостаточно", exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleAlreadyInWarehouseException(final SpecifiedProductAlreadyInWarehouseException exception) {
        return new ExceptionResponse("Данный товар уже есть на складе", exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
