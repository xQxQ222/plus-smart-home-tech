package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.feign.api.OrderApi;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.request.CreateNewOrderRequest;
import ru.yandex.practicum.order.request.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@Slf4j
@RequiredArgsConstructor
public class OrderController implements OrderApi {

    private final OrderService orderService;

    @GetMapping
    public Page<OrderDto> getUserOrders(@RequestParam(name = "username") String username, @PageableDefault(direction = Direction.ASC, size = 10, page = 0) Pageable pageable) {
        log.info("Пришел GET запрос на /api/v1/order?username={}", username);
        Page<OrderDto> orders = orderService.getUserOrders(username, pageable);
        log.info("Отправлен ответ на запрос GET /api/v1/order?username={} с телом: {}", username, orders);
        return orders;
    }

    @PutMapping
    public OrderDto addNewOrder(@RequestBody CreateNewOrderRequest request) {
        log.info("Пришел PUT запрос на /api/v1/order с телом: {}", request);
        OrderDto order = orderService.addNewOrder(request);
        log.info("Отправлен ответ на запрос PUT /api/v1/order с телом: {}", order);
        return order;
    }

    @PostMapping("/return")
    public OrderDto returnOrder(@RequestBody ProductReturnRequest request) {
        log.info("Пришел POST запрос на /api/v1/order/return с телом: {}", request);
        OrderDto order = orderService.returnOrder(request);
        log.info("Отправлен ответ на запрос POST /api/v1/order/return с телом: {}", order);
        return order;
    }

    @PostMapping("/payment")
    public OrderDto orderPayment(@RequestBody UUID orderId) {
        log.info("Пришел POST запрос на /api/v1/order/payment с телом: {}", orderId);
        OrderDto order = orderService.orderPayment(orderId);
        log.info("Отправлен ответ на запрос POST /api/v1/order/payment с телом: {}", order);
        return order;
    }

    @PostMapping("/payment/failed")
    public OrderDto orderPaymentFailed(@RequestBody UUID orderId) {
        log.info("Пришел POST запрос на /api/v1/order/payment/failed с телом: {}", orderId);
        OrderDto order = orderService.orderPaymentFailed(orderId);
        log.info("Отправлен ответ на запрос POST /api/v1/order/payment/failed с телом: {}", order);
        return order;
    }

    @PostMapping("/delivery")
    public OrderDto orderDelivery(@RequestBody UUID orderId) {
        log.info("Пришел POST запрос на /api/v1/order/delivery с телом: {}", orderId);
        OrderDto order = orderService.orderDelivery(orderId);
        log.info("Отправлен ответ на запрос POST /api/v1/order/delivery с телом: {}", order);
        return order;
    }

    @PostMapping("/delivery/failed")
    public OrderDto orderDeliveryFailed(@RequestBody UUID orderId) {
        log.info("Пришел POST запрос на /api/v1/order/delivery/failed с телом: {}", orderId);
        OrderDto order = orderService.orderDeliveryFailed(orderId);
        log.info("Отправлен ответ на запрос POST /api/v1/order/delivery/failed с телом: {}", order);
        return order;
    }

    @PostMapping("/completed")
    public OrderDto orderComplete(@RequestBody UUID orderId) {
        log.info("Пришел POST запрос на /api/v1/order/completed с телом: {}", orderId);
        OrderDto order = orderService.completeOrder(orderId);
        log.info("Отправлен ответ на запрос POST /api/v1/order/completed с телом: {}", order);
        return order;
    }

    @PostMapping("/calculate/total")
    public OrderDto calculateOrderTotalPrice(@RequestBody UUID orderId) {
        log.info("Пришел POST запрос на /api/v1/order/calculate/total с телом: {}", orderId);
        OrderDto order = orderService.calculateTotalPrice(orderId);
        log.info("Отправлен ответ на запрос POST /api/v1/order/calculate/total с телом: {}", order);
        return order;
    }

    @PostMapping("/calculate/delivery")
    public OrderDto calculateOrderDeliveryPrice(@RequestBody UUID orderId) {
        log.info("Пришел POST запрос на /api/v1/order/calculate/delivery с телом: {}", orderId);
        OrderDto order = orderService.calculateDeliveryPrice(orderId);
        log.info("Отправлен ответ на запрос POST /api/v1/order/calculate/delivery с телом: {}", order);
        return order;
    }

    @PostMapping("/assembly")
    public OrderDto assemblyOrder(@RequestBody UUID orderId) {
        log.info("Пришел POST запрос на /api/v1/order/assembly с телом: {}", orderId);
        OrderDto order = orderService.orderAssembly(orderId);
        log.info("Отправлен ответ на запрос POST /api/v1/order/assembly с телом: {}", order);
        return order;
    }

    @PostMapping("/assembly/failed")
    public OrderDto assemblyOrderFailed(@RequestBody UUID orderId) {
        log.info("Пришел POST запрос на /api/v1/order/assembly/failed с телом: {}", orderId);
        OrderDto order = orderService.orderAssemblyFailed(orderId);
        log.info("Отправлен ответ на запрос POST /api/v1/order/assembly/failed с телом: {}", order);
        return order;
    }
}
