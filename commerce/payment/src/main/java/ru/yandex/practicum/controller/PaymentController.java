package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.feign.api.PaymentApi;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.payment.dto.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
@Slf4j
@RequiredArgsConstructor
public class PaymentController implements PaymentApi {
    private final PaymentService paymentService;

    @PostMapping
    public PaymentDto paymentFormation(@RequestBody OrderDto orderDto) {
        log.info("Пришел POST запрос на /api/v1/payment с телом: {}", orderDto);
        PaymentDto payment = paymentService.paymentFormationForOrder(orderDto);
        log.info("Отправлен ответ на запрос POST /api/v1/payment с телом: {}", orderDto);
        return payment;
    }

    @PostMapping("/totalCost")
    public Double calculateTotalCost(@RequestBody OrderDto orderDto) {
        log.info("Пришел POST запрос на /api/v1/payment/totalCost с телом: {}", orderDto);
        Double cost = paymentService.calculateTotalOrderPrice(orderDto);
        log.info("Отправлен ответ на запрос POST /api/v1/payment/totalCost с телом: {}", cost);
        return cost;
    }

    @PostMapping("/refund")
    public void refundPayment(@RequestBody UUID paymentId) {
        log.info("Пришел POST запрос на /api/v1/payment/refund с телом: {}", paymentId);
        paymentService.refundPayment(paymentId);
        log.info("Отправлен ответ на запрос POST /api/v1/payment/refund");
    }

    @PostMapping("/productCost")
    public Double calculateProductCost(@RequestBody OrderDto orderDto) {
        log.info("Пришел POST запрос на /api/v1/payment/productCost с телом: {}", orderDto);
        Double cost = paymentService.calculateOrderProductsCost(orderDto);
        log.info("Отправлен ответ на запрос POST /api/v1/payment/productCost с телом: {}", cost);
        return cost;
    }

    @PostMapping("/failed")
    public void paymentFailed(@RequestBody UUID paymentId) {
        log.info("Пришел POST запрос на /api/v1/payment/failed с телом: {}", paymentId);
        paymentService.paymentFailed(paymentId);
        log.info("Отправлен ответ на запрос POST /api/v1/payment/failed");
    }
}
