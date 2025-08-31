package ru.yandex.practicum.payment.exception;

import java.util.UUID;

public class NotEnoughInfoInOrderToCalculateException extends RuntimeException {
    public NotEnoughInfoInOrderToCalculateException(UUID orderId) {
        super("Недостаточно информации о заказе с id" + orderId + " для расчета стоимости");
    }
}
