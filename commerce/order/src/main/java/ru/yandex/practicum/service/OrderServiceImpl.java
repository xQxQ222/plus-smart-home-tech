package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.cart.exception.NotAuthorizedUserException;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.delivery.exception.IncorrectDeliveryStatusException;
import ru.yandex.practicum.feign.client.delivery.DeliveryFeignClient;
import ru.yandex.practicum.feign.client.payment.PaymentFeignClient;
import ru.yandex.practicum.feign.client.warehouse.WarehouseFeignClient;
import ru.yandex.practicum.mapper.AddressMapper;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.DeliveryAddress;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.enums.OrderState;
import ru.yandex.practicum.order.exception.IncorrectOrderStatusException;
import ru.yandex.practicum.order.exception.NoOrderFoundException;
import ru.yandex.practicum.order.request.CreateNewOrderRequest;
import ru.yandex.practicum.order.request.ProductReturnRequest;
import ru.yandex.practicum.payment.dto.PaymentDto;
import ru.yandex.practicum.repository.AddressRepository;
import ru.yandex.practicum.repository.OrderRepository;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.request.AssemblyProductsForOrderRequest;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final AddressMapper addressMapper;
    private final AddressRepository addressRepository;
    private final WarehouseFeignClient warehouseFeign;
    private final PaymentFeignClient paymentFeignClient;
    private final DeliveryFeignClient deliveryFeignClient;

    @Override
    public Page<OrderDto> getUserOrders(String username, Pageable pageable) {
        checkUsername(username);
        Page<Order> userOrders = orderRepository.findByUsername(username, pageable);
        return userOrders.map(orderMapper::toOrderDto);
    }

    @Override
    public OrderDto addNewOrder(CreateNewOrderRequest request) {
        BookedProductsDto bookedProducts = warehouseFeign.checkShoppingCartProductsInWarehouse(request.getShoppingCart());
        DeliveryAddress address = addressRepository.save(addressMapper.toDomainAddress(request.getDeliveryAddress()));
        Order order = Order.builder()
                .address(address)
                .fragile(bookedProducts.getFragile())
                .deliveryWeight(bookedProducts.getDeliveryWeight())
                .deliveryVolume(bookedProducts.getDeliveryVolume())
                .state(OrderState.NEW)
                .shoppingCartId(request.getShoppingCart().getShoppingCartId())
                .products(request.getShoppingCart().getProducts())
                .username(request.getUsername())
                .build();
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto returnOrder(ProductReturnRequest request) {
        Order order = getOrderById(request.getOrderId());

        Map<UUID, Integer> products = request.getProducts();
        warehouseFeign.returnProductsToWarehouse(products);

        order.setState(OrderState.PRODUCT_RETURNED);
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto orderPayment(UUID orderId) {
        Order order = getOrderById(orderId);
        if (!order.getState().equals(OrderState.NEW)) {
            throw new IncorrectDeliveryStatusException("Для осуществления оплаты заказа, его статус должен быть NEW");
        }
        PaymentDto paymentDto = paymentFeignClient.paymentFormation(orderMapper.toOrderDto(order));
        order.setPaymentId(paymentDto.getPaymentId());
        order.setState(OrderState.PAID);
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto orderPaymentFailed(UUID orderId) {
        Order order = getOrderById(orderId);
        if (!order.getState().equals(OrderState.NEW)) {
            throw new IncorrectDeliveryStatusException("Для осуществления оплаты заказа, его статус должен быть NEW");
        }
        order.setState(OrderState.PAYMENT_FAILED);
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto orderDelivery(UUID orderId) {
        Order order = getOrderById(orderId);

        if (!order.getState().equals(OrderState.ASSEMBLED)) {
            throw new IncorrectDeliveryStatusException("Для осуществления доставки заказа, его статус должен быть ASSEMBLED");
        }

        DeliveryDto newDelivery = DeliveryDto.builder()
                .orderId(orderId)
                .fromAddress(warehouseFeign.getWarehouseAddress())
                .toAddress(addressMapper.toAddressDto(order.getAddress()))
                .build();

        DeliveryDto deliveryDto = deliveryFeignClient.createNewDelivery(newDelivery);
        order.setDeliveryId(deliveryDto.getDeliveryId());
        order.setState(OrderState.DELIVERED);
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto orderDeliveryFailed(UUID orderId) {
        Order order = getOrderById(orderId);

        if (!order.getState().equals(OrderState.ASSEMBLED)) {
            throw new IncorrectDeliveryStatusException("Для осуществления доставки заказа, его статус должен быть ASSEMBLED");
        }

        order.setState(OrderState.DELIVERY_FAILED);

        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto completeOrder(UUID orderId) {
        Order order = getOrderById(orderId);
        if (!order.getState().equals(OrderState.DELIVERED)) {
            throw new IncorrectDeliveryStatusException("Чтобы завершить заказ, его статус должен быть DELIVERED");
        }
        order.setState(OrderState.COMPLETED);
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto calculateTotalPrice(UUID orderId) {
        Order order = getOrderById(orderId);

        order.setTotalPrice(paymentFeignClient.calculateTotalCost(orderMapper.toOrderDto(order)));

        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto calculateDeliveryPrice(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setDeliveryPrice(deliveryFeignClient.calculateDeliveryCost(orderMapper.toOrderDto(order)));
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto orderAssembly(UUID orderId) {
        Order order = getOrderById(orderId);
        if (!order.getState().equals(OrderState.PAID)) {
            throw new IncorrectOrderStatusException("Для начала сборки заказ должен быть оплачен");
        }
        AssemblyProductsForOrderRequest request = new AssemblyProductsForOrderRequest(order.getProducts(), order.getOrderId());
        warehouseFeign.assemblyProducts(request);

        order.setState(OrderState.ASSEMBLED);
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    @Override
    public OrderDto orderAssemblyFailed(UUID orderId) {
        Order order = getOrderById(orderId);
        if (!order.getState().equals(OrderState.PAID)) {
            throw new IncorrectOrderStatusException("Для начала сборки заказ должен быть оплачен");
        }
        order.setState(OrderState.ASSEMBLY_FAILED);
        return orderMapper.toOrderDto(orderRepository.save(order));
    }

    private void checkUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Ошибка авторизации пользователя");
        }
    }

    private Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new NoOrderFoundException(orderId));
    }
}
