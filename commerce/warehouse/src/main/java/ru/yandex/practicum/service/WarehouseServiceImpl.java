package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.delivery.dto.DeliveryDto;
import ru.yandex.practicum.delivery.enums.DeliveryState;
import ru.yandex.practicum.feign.client.delivery.DeliveryFeignClient;
import ru.yandex.practicum.feign.client.order.OrderFeignClient;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Dimension;
import ru.yandex.practicum.model.OrderBooked;
import ru.yandex.practicum.model.WarehouseElement;
import ru.yandex.practicum.repository.OrderBookingRepository;
import ru.yandex.practicum.repository.WarehouseRepository;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.exception.NoBookedOrderFoundException;
import ru.yandex.practicum.warehouse.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.warehouse.exception.ProductInShoppingCartLowQuantityInWarehouseException;
import ru.yandex.practicum.warehouse.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.warehouse.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.warehouse.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.request.ShippedToDeliveryRequest;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final OrderBookingRepository orderBookingRepository;
    private final DeliveryFeignClient deliveryFeignClient;

    @Override
    public void putNewProduct(NewProductInWarehouseRequest request) {
        if (warehouseRepository.existsById(request.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException("Товар с id " + request.getProductId() + " уже есть на складе");
        }

        log.debug("Добавление нового продукта на склад: {}", request);

        Dimension dimension = new Dimension(request.getDimension().getWidth(),
                request.getDimension().getHeight(),
                request.getDimension().getDepth());

        WarehouseElement newElement = WarehouseElement.builder()
                .productId(request.getProductId())
                .weight(request.getWeight())
                .fragile(request.getFragile())
                .dimension(dimension)
                .quantity(0)
                .build();
        warehouseRepository.save(newElement);
    }

    @Override
    public BookedProductsDto checkProductsInWarehouse(ShoppingCartDto cartDto) {
        Map<UUID, Integer> products = cartDto.getProducts();
        List<WarehouseElement> warehouseElements = new ArrayList<>();
        for (UUID productId : products.keySet()) {
            WarehouseElement warehouseElement = warehouseRepository.findById(productId)
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Товар с id " + productId + " не найден на складе"));
            if (products.get(productId) > warehouseElement.getQuantity()) {
                throw new ProductInShoppingCartLowQuantityInWarehouseException("Товара с id " + productId + " на складе меньше, чем в корзине пользователя");
            }
            warehouseElements.add(warehouseElement);
        }
        BookedProductsDto bookedProducts = BookedProductsDto.builder()
                .deliveryVolume(calculateTotalVolume(warehouseElements))
                .deliveryWeight(calculateTotalWeight(warehouseElements))
                .fragile(warehouseElements.stream().anyMatch(WarehouseElement::getFragile))
                .build();
        log.debug("Проверка доступности товара на складе. Оформление заказа: {}", bookedProducts);
        return bookedProducts;
    }

    @Override
    public void acceptProduct(AddProductToWarehouseRequest request) {
        log.debug("Прием товара на склад: {}", request);
        WarehouseElement element = warehouseRepository.findById(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Товар с id " + request.getProductId() + " не найден на складе"));
        element.setQuantity(element.getQuantity() + request.getQuantity());
        warehouseRepository.save(element);
        log.debug("Новое количество товара: {}", element.getQuantity());
    }

    @Override
    public AddressDto getWarehouseAddress() {
        String currentAddress = Address.CURRENT_ADDRESS;
        log.debug("Получение адреса {}", currentAddress);
        return AddressDto.builder()
                .country(currentAddress)
                .city(currentAddress)
                .street(currentAddress)
                .house(currentAddress)
                .flat(currentAddress)
                .build();
    }

    @Override
    public void shipOrderToDelivery(ShippedToDeliveryRequest request) {
        OrderBooked orderBooked = orderBookingRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NoBookedOrderFoundException("Бронирование товара по id заказа " + request.getOrderId() + " не найдено"));
        log.debug("Отправка товара в доставку. Заявка: {}", request);
        orderBooked.setDeliveryId(request.getDeliveryId());
        orderBookingRepository.save(orderBooked);
    }

    @Override
    public void returnProductsToWarehouse(Map<UUID, Integer> products) {
        log.debug("Возврат продуктов: {}", products);
        for (UUID productId : products.keySet()) {
            WarehouseElement product = warehouseRepository.findById(productId)
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Товара с id " + productId + " нет на складе"));
            Integer currentProductQuantity = product.getQuantity();
            product.setQuantity(currentProductQuantity + products.get(productId));
            warehouseRepository.save(product);
        }
    }

    @Override
    public BookedProductsDto assemblyProducts(AssemblyProductsForOrderRequest request) {
        Map<UUID, Integer> products = request.getProducts();
        List<WarehouseElement> bookedProducts = new ArrayList<>();
        for (UUID productId : products.keySet()) {
            WarehouseElement product = warehouseRepository.findById(productId)
                    .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Товар с id " + productId + " не найден на складе"));
            bookedProducts.add(product);
            Integer currentProductQuantity = product.getQuantity();
            product.setQuantity(currentProductQuantity - products.get(productId));
            warehouseRepository.save(product);
        }

        OrderBooked booked = OrderBooked.builder()
                .products(products)
                .orderId(request.getOrderId())
                .build();
        orderBookingRepository.save(booked);

        return BookedProductsDto.builder()
                .deliveryVolume(calculateTotalVolume(bookedProducts))
                .deliveryWeight(calculateTotalWeight(bookedProducts))
                .fragile(bookedProducts.stream().anyMatch(WarehouseElement::getFragile))
                .build();
    }

    private Double calculateTotalVolume(List<WarehouseElement> warehouseElements) {
        double totalVolume = 0.00;
        for (WarehouseElement el : warehouseElements) {
            Dimension dimension = el.getDimension();
            totalVolume += dimension.getDepth() * dimension.getHeight() * dimension.getWidth();
        }
        return totalVolume;
    }

    private Double calculateTotalWeight(List<WarehouseElement> warehouseElements) {
        double totalWeight = 0.00;
        for (WarehouseElement el : warehouseElements) {
            totalWeight += el.getWeight();
        }
        return totalWeight;
    }
}
