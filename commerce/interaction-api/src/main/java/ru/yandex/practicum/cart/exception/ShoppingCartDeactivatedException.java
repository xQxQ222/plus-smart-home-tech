package ru.yandex.practicum.cart.exception;

public class ShoppingCartDeactivatedException extends RuntimeException {
    public ShoppingCartDeactivatedException(String message) {
        super(message);
    }
}
