package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.delivery.enums.DeliveryState;
import ru.yandex.practicum.delivery.exception.IncorrectDeliveryStatusException;
import ru.yandex.practicum.delivery.exception.NoDeliveryFoundException;
import ru.yandex.practicum.feign.client.order.OrderFeignClient;
import ru.yandex.practicum.feign.client.warehouse.WarehouseFeignClient;
import ru.yandex.practicum.mapper.AddressMapper;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.repository.DeliveryRepository;
import ru.yandex.practicum.warehouse.request.ShippedToDeliveryRequest;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private static final double BASE_DELIVERY_COST = 5;

    private final DeliveryMapper deliveryMapper;
    private final AddressMapper addressMapper;
    private final DeliveryRepository deliveryRepository;
    private final OrderFeignClient orderFeignClient;
    private final WarehouseFeignClient warehouseFeignClient;

    @Override
    public DeliveryDto createNewDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = deliveryMapper.toDomainDelivery(deliveryDto);
        delivery.setDeliveryState(DeliveryState.CREATED);
        delivery.setFromAddress(addressMapper.toDomainAddress(deliveryDto.getFromAddress()));
        delivery.setToAddress(addressMapper.toDomainAddress(deliveryDto.getToAddress()));
        return deliveryMapper.toDeliveryDto(deliveryRepository.save(delivery));
    }

    @Override
    public void successfulDelivery(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException(deliveryId));
        if (!delivery.getDeliveryState().equals(DeliveryState.IN_PROGRESS)) {
            throw new IncorrectDeliveryStatusException("Доставка должна находиться в статусе IN_PROGRESS");
        }
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);
        orderFeignClient.orderDelivery(delivery.getOrderId());
    }

    @Override
    public void pickProductToDelivery(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException(deliveryId));
        if (!delivery.getDeliveryState().equals(DeliveryState.CREATED)) {
            throw new IncorrectDeliveryStatusException("Для отправки товара доставка должна находиться в статусе CREATED");
        }
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        ShippedToDeliveryRequest request = new ShippedToDeliveryRequest(delivery.getOrderId(), deliveryId);
        warehouseFeignClient.shipOrderToDelivery(request);
    }

    @Override
    public void failedDelivery(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new NoDeliveryFoundException(deliveryId));
        if (!delivery.getDeliveryState().equals(DeliveryState.IN_PROGRESS)) {
            throw new IncorrectDeliveryStatusException("Доставка должна находиться в статусе IN_PROGRESS");
        }
        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);
        log.debug("Ошибка доставки {}", deliveryId);
        orderFeignClient.orderDeliveryFailed(delivery.getOrderId());
    }

    @Override
    public Double calculateDeliveryCost(OrderDto orderDto) {
        Delivery delivery = deliveryRepository.findById(orderDto.getDeliveryId())
                .orElseThrow(() -> new NoDeliveryFoundException(orderDto.getDeliveryId()));
        int addressCoef = delivery.getFromAddress().toString().contains("ADDRESS_1") ? 1 : 2;
        double deliveryCost = BASE_DELIVERY_COST * addressCoef;
        if (delivery.getFragile()) {
            deliveryCost *= 1.2;
        }
        deliveryCost += delivery.getTotalWeight() * 0.3;
        deliveryCost += delivery.getTotalVolume() * 0.2;

        if (!delivery.getFromAddress().getStreet().equals(delivery.getToAddress().getStreet())) {
            deliveryCost *= 1.2;
        }
        log.debug("Рассчитана стоимость доставки (id: {}): {}", orderDto.getDeliveryId(), deliveryCost);
        return deliveryCost;
    }
}
