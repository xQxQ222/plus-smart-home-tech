package ru.yandex.practicum.service;

import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.payment.dto.PaymentDto;

import java.util.UUID;

public interface PaymentService {
    PaymentDto paymentFormationForOrder(OrderDto orderDto);

    Double calculateTotalOrderPrice(OrderDto orderDto);

    void refundPayment(UUID paymentId);

    Double calculateOrderProductsCost(OrderDto orderDto);

    void paymentFailed(UUID paymentId);
}
