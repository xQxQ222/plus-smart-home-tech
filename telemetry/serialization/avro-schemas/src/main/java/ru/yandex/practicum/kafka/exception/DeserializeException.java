package ru.yandex.practicum.kafka.exception;

public class DeserializeException extends RuntimeException {
    public DeserializeException(String message) {
        super(message);
    }
}
