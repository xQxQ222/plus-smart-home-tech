package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.feign.client.order.OrderFeignClient;
import ru.yandex.practicum.feign.client.store.StoreFeignClient;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.payment.dto.PaymentDto;
import ru.yandex.practicum.payment.enums.PaymentStatus;
import ru.yandex.practicum.payment.exception.IncorrectPaymentStateException;
import ru.yandex.practicum.payment.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.payment.exception.PaymentNotFoundException;
import ru.yandex.practicum.repository.PaymentRepository;
import ru.yandex.practicum.store.dto.ProductDto;

import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StoreFeignClient storeFeignClient;
    private final OrderFeignClient orderFeignClient;

    @Override
    public PaymentDto paymentFormationForOrder(OrderDto orderDto) {
        Payment payment = Payment.builder()
                .orderId(orderDto.getOrderId())
                .deliveryTotal(orderDto.getDeliveryPrice())
                .productTotal(calculateOrderProductsCost(orderDto))
                .totalPayment(calculateTotalOrderPrice(orderDto))
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        return paymentMapper.toPaymentDto(paymentRepository.save(payment));
    }

    @Override
    public Double calculateTotalOrderPrice(OrderDto orderDto) {
        if ((orderDto.getProductPrice() == null || orderDto.getProductPrice() == 0) &&
                (orderDto.getDeliveryPrice() == null || orderDto.getDeliveryPrice() == 0)) {
            throw new NotEnoughInfoInOrderToCalculateException(orderDto.getOrderId());
        }
        double fee = orderDto.getProductPrice() * 0.1;
        double totalOrderPrice = orderDto.getDeliveryPrice() + orderDto.getProductPrice() + fee;
        return totalOrderPrice;
    }

    @Override
    public void refundPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new IncorrectPaymentStateException("Для изменения статуса оплаты она должна находиться в статусе PENDING");
        }
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
        orderFeignClient.orderPayment(payment.getOrderId());
    }

    @Override
    public Double calculateOrderProductsCost(OrderDto orderDto) {
        Map<UUID, Integer> products = orderDto.getProducts();
        double totalCost = 0.00;

        for (UUID productId : products.keySet()) {
            ProductDto product = storeFeignClient.getProductById(productId);
            totalCost += product.getPrice() * products.get(productId);
        }
        return totalCost;
    }

    @Override
    public void paymentFailed(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new IncorrectPaymentStateException("Для изменения статуса оплаты она должна находиться в статусе PENDING");
        }
        payment.setPaymentStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
        orderFeignClient.orderPaymentFailed(payment.getOrderId());
    }
}
