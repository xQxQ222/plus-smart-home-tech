package ru.yandex.practicum.feign.client.warehouse;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.feign.client.exception.ServiceUnavailableException;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.request.NewProductInWarehouseRequest;

@Component
public class WarehouseFallback implements WarehouseFeignClient {
    @Override
    public void addNewProductToWarehouse(NewProductInWarehouseRequest request) {
        throw new ServiceUnavailableException("Сервис Warehouse сейчас недоступен. Повторите запрос позже");
    }

    @Override
    public BookedProductsDto checkShoppingCartProductsInWarehouse(ShoppingCartDto shoppingCart) {
        throw new ServiceUnavailableException("Сервис Warehouse сейчас недоступен. Повторите запрос позже");
    }

    @Override
    public void acceptProductsToWarehouse(AddProductToWarehouseRequest request) {
        throw new ServiceUnavailableException("Сервис Warehouse сейчас недоступен. Повторите запрос позже");
    }

    @Override
    public AddressDto getWarehouseAddress() {
        throw new ServiceUnavailableException("Сервис Warehouse сейчас недоступен. Повторите запрос позже");
    }
}
