package ru.yandex.practicum.controller.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.cart.exception.*;
import ru.yandex.practicum.response.ExceptionResponse;

@RestControllerAdvice
public class CartExceptionsHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionResponse handleDeactivatedException(final ShoppingCartDeactivatedException exception) {
        return new ExceptionResponse("Корзина деактивирована", exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleNonExistProductException(final NonExistProductException exception){
        return new ExceptionResponse("Такого товара в корзине нет", exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleNoProductsException(final NoProductsInShoppingCartException exception){
        return new ExceptionResponse("Корзина пуста", exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionResponse handleNonAuthorizedException(final NotAuthorizedUserException exception){
        return new ExceptionResponse("Пользователь не авторизирован", exception.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ExceptionResponse handleNotEnoughProductsException(final NotEnoughProductInWarehouseException exception){
        return new ExceptionResponse("Товара недостаточно на складе", exception.getMessage(), HttpStatus.BAD_GATEWAY);
    }
}
