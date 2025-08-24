package ru.yandex.practicum.service;

import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.request.NewProductInWarehouseRequest;

public interface WarehouseService {
    void putNewProduct(NewProductInWarehouseRequest request);

    BookedProductsDto checkProductsInWarehouse(ShoppingCartDto cartDto);

    void acceptProduct(AddProductToWarehouseRequest request);

    AddressDto getWarehouseAddress();
}
