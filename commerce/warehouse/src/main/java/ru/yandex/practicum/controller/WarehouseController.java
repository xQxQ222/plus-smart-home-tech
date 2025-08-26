package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.feign.api.WarehouseApi;
import ru.yandex.practicum.service.WarehouseService;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.request.NewProductInWarehouseRequest;

@RestController
@RequestMapping(path = "/api/v1/warehouse")
@RequiredArgsConstructor
@Slf4j
public class WarehouseController implements WarehouseApi {

    private final WarehouseService service;

    @PutMapping
    public void addNewProductToWarehouse(@RequestBody NewProductInWarehouseRequest request) {
        log.info("Пришел PUT запрос на /api/v1/warehouse с телом: {}", request);
        service.putNewProduct(request);
        log.info("Отправлен ответ на запрос PUT /api/v1/warehouse");
    }

    @PostMapping("/check")
    public BookedProductsDto checkShoppingCartProductsInWarehouse(@RequestBody ShoppingCartDto shoppingCart) {
        log.info("Пришел POST запрос на /api/v1/warehouse/check с телом: {}", shoppingCart);
        BookedProductsDto bookedProducts = service.checkProductsInWarehouse(shoppingCart);
        log.info("Отправлен ответ на запрос POST /api/v1/warehouse/check с телом: {}", bookedProducts);
        return bookedProducts;
    }

    @PostMapping("/add")
    public void acceptProductsToWarehouse(@RequestBody AddProductToWarehouseRequest request) {
        log.info("Пришел POST запрос на /api/v1/warehouse/add с телом: {}", request);
        service.acceptProduct(request);
        log.info("Отправлен ответ на запрос POST /api/v1/warehouse/add");
    }

    @GetMapping("/address")
    public AddressDto getWarehouseAddress() {
        log.info("Пришел GET запрос на /api/v1/warehouse/address");
        AddressDto address = service.getWarehouseAddress();
        log.info("Отправлен ответ на запрос GET /api/v1/warehouse/address");
        return address;
    }
}
