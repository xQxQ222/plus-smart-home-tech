package ru.yandex.practicum.payment.exception;

import java.util.UUID;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(UUID paymentId) {
        super("Платеж с id " + paymentId + " не найден");
    }
}
