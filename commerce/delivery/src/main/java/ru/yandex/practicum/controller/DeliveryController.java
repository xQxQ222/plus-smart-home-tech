package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.feign.api.DeliveryApi;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
@Slf4j
public class DeliveryController implements DeliveryApi {

    private final DeliveryService deliveryService;

    @PutMapping
    public DeliveryDto createNewDelivery(@RequestBody DeliveryDto deliveryDto) {
        log.info("Пришел PUT запрос на /api/v1/delivery с телом: {}", deliveryDto);
        DeliveryDto delivery = deliveryService.createNewDelivery(deliveryDto);
        log.info("Отправлен ответ на запрос PUT /api/v1/delivery с телом: {}", delivery);
        return delivery;
    }

    @PostMapping("/successful")
    public void successfulDelivery(@RequestBody UUID deliveryId) {
        log.info("Пришел POST запрос на /api/v1/delivery/successful с телом: {}", deliveryId);
        deliveryService.successfulDelivery(deliveryId);
        log.info("Отправлен ответ на запрос POST /api/v1/delivery/successful");
    }

    @PostMapping("/picked")
    public void pickProductToDelivery(@RequestBody UUID deliveryId) {
        log.info("Пришел POST запрос на /api/v1/delivery/picked с телом: {}", deliveryId);
        deliveryService.pickProductToDelivery(deliveryId);
        log.info("Отправлен ответ на запрос POST /api/v1/picked");
    }

    @PostMapping("/failed")
    public void failedDelivery(@RequestBody UUID deliveryId) {
        log.info("Пришел POST запрос на /api/v1/delivery/failed с телом: {}", deliveryId);
        deliveryService.failedDelivery(deliveryId);
        log.info("Отправлен ответ на запрос POST /api/v1/delivery/failed");
    }

    @PostMapping("/cost")
    public Double calculateDeliveryCost(@RequestBody OrderDto orderDto) {
        log.info("Пришел POST запрос на /api/v1/delivery/cost с телом: {}", orderDto);
        Double cost = deliveryService.calculateDeliveryCost(orderDto);
        log.info("Отправлен ответ на запрос POST /api/v1/delivery/cost с телом: {}", cost);
        return cost;
    }
}
