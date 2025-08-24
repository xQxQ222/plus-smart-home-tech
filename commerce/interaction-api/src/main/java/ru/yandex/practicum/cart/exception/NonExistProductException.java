package ru.yandex.practicum.cart.exception;

public class NonExistProductException extends RuntimeException {
    public NonExistProductException(String message) {
        super(message);
    }
}
