package ru.yandex.practicum.service;

import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.warehouse.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.request.ShippedToDeliveryRequest;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {
    void putNewProduct(NewProductInWarehouseRequest request);

    BookedProductsDto checkProductsInWarehouse(ShoppingCartDto cartDto);

    void acceptProduct(AddProductToWarehouseRequest request);

    AddressDto getWarehouseAddress();

    void shipOrderToDelivery(ShippedToDeliveryRequest request);

    void returnProductsToWarehouse(Map<UUID, Integer> products);

    BookedProductsDto assemblyProducts(AssemblyProductsForOrderRequest request);
}
