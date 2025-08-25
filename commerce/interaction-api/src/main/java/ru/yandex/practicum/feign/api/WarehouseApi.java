package ru.yandex.practicum.feign.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.request.NewProductInWarehouseRequest;

public interface WarehouseApi {
    @PutMapping
    void addNewProductToWarehouse(@RequestBody NewProductInWarehouseRequest request);

    @PostMapping("/check")
    BookedProductsDto checkShoppingCartProductsInWarehouse(@RequestBody ShoppingCartDto shoppingCart);

    @PostMapping("/add")
    void acceptProductsToWarehouse(@RequestBody AddProductToWarehouseRequest request);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();
}
