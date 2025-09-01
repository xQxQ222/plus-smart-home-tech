package ru.yandex.practicum.warehouse.exception;

public class NoBookedOrderFoundException extends RuntimeException {
    public NoBookedOrderFoundException(String message) {
        super(message);
    }
}
