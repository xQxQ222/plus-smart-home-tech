package ru.yandex.practicum.feign.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.payment.dto.PaymentDto;

import java.util.UUID;

public interface PaymentApi {
    @PostMapping
    PaymentDto paymentFormation(@RequestBody OrderDto orderDto);

    @PostMapping("/totalCost")
    Double calculateTotalCost(@RequestBody OrderDto orderDto);

    @PostMapping("/refund")
    void refundPayment(@RequestBody UUID paymentId);

    @PostMapping("/productCost")
    Double calculateProductCost(@RequestBody OrderDto orderDto);

    @PostMapping("/failed")
    void paymentFailed(@RequestBody UUID paymentId);
}
