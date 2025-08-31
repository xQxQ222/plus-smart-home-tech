package ru.yandex.practicum.delivery.exception;

public class IncorrectDeliveryStatusException extends RuntimeException {
    public IncorrectDeliveryStatusException(String message) {
        super(message);
    }
}
