package ru.yandex.practicum.order.exception;

public class IncorrectOrderStatusException extends RuntimeException {
    public IncorrectOrderStatusException(String message) {
        super(message);
    }
}
