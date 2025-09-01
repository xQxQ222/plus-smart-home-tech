package ru.yandex.practicum.feign.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.request.CreateNewOrderRequest;
import ru.yandex.practicum.order.request.ProductReturnRequest;

import java.util.UUID;

public interface OrderApi {
    @GetMapping
    Page<OrderDto> getUserOrders(@RequestParam(name = "username") String username, @PageableDefault(direction = Sort.Direction.ASC, size = 10, page = 0) Pageable pageable);

    @PutMapping
    OrderDto addNewOrder(@RequestBody CreateNewOrderRequest request);

    @PostMapping("/return")
    OrderDto returnOrder(@RequestBody ProductReturnRequest request);

    @PostMapping("/payment")
    OrderDto orderPayment(@RequestBody UUID orderId);

    @PostMapping("/payment/failed")
    OrderDto orderPaymentFailed(@RequestBody UUID orderId);

    @PostMapping("/delivery")
    OrderDto orderDelivery(@RequestBody UUID orderId);

    @PostMapping("/delivery/failed")
    OrderDto orderDeliveryFailed(@RequestBody UUID orderId);

    @PostMapping("/completed")
    OrderDto orderComplete(@RequestBody UUID orderId);

    @PostMapping("/calculate/total")
    OrderDto calculateOrderTotalPrice(@RequestBody UUID orderId);

    @PostMapping("/calculate/delivery")
    OrderDto calculateOrderDeliveryPrice(@RequestBody UUID orderId);

    @PostMapping("/assembly")
    OrderDto assemblyOrder(@RequestBody UUID orderId);

    @PostMapping("/assembly/failed")
    OrderDto assemblyOrderFailed(@RequestBody UUID orderId);
}
