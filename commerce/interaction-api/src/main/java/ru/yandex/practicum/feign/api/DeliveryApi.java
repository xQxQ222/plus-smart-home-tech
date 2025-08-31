package ru.yandex.practicum.feign.api;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.order.dto.OrderDto;

import java.util.UUID;

public interface DeliveryApi {
    @PutMapping
    DeliveryDto createNewDelivery(@RequestBody DeliveryDto deliveryDto);

    @PostMapping("/successful")
    void successfulDelivery(@RequestBody UUID deliveryId);

    @PostMapping("/picked")
    void pickProductToDelivery(@RequestBody UUID deliveryId);

    @PostMapping("/failed")
    void failedDelivery(@RequestBody UUID deliveryId);

    @PostMapping("/cost")
    Double calculateDeliveryCost(@RequestBody OrderDto orderDto);
}
