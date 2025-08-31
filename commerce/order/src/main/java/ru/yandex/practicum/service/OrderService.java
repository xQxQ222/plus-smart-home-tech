package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.request.CreateNewOrderRequest;
import ru.yandex.practicum.order.request.ProductReturnRequest;

import java.util.UUID;

public interface OrderService {
    Page<OrderDto> getUserOrders(String username, Pageable pageable);

    OrderDto addNewOrder(CreateNewOrderRequest request);

    OrderDto returnOrder(ProductReturnRequest request);

    OrderDto orderPayment(UUID orderId);

    OrderDto orderPaymentFailed(UUID orderId);

    OrderDto orderDelivery(UUID orderId);

    OrderDto orderDeliveryFailed(UUID orderId);

    OrderDto completeOrder(UUID orderId);

    OrderDto calculateTotalPrice(UUID orderId);

    OrderDto calculateDeliveryPrice(UUID orderId);

    OrderDto orderAssembly(UUID orderId);

    OrderDto orderAssemblyFailed(UUID orderId);
}
