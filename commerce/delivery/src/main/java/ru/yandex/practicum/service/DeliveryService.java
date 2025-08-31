package ru.yandex.practicum.service;

import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.order.dto.OrderDto;

import java.util.UUID;

public interface DeliveryService {
    DeliveryDto createNewDelivery(DeliveryDto deliveryDto);

    void successfulDelivery(UUID deliveryId);

    void pickProductToDelivery(UUID deliveryId);

    void failedDelivery(UUID deliveryId);

    Double calculateDeliveryCost(OrderDto orderDto);
}
