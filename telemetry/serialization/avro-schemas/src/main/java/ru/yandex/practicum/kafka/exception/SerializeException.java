package ru.yandex.practicum.kafka.exception;

public class SerializeException extends RuntimeException {
    public SerializeException(String message) {
        super(message);
    }
}
