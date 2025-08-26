package ru.yandex.practicum.cart.exception;

public class NotEnoughProductInWarehouseException extends RuntimeException {
    public NotEnoughProductInWarehouseException(String message) {
        super(message);
    }
}
