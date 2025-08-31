package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.feign.api.WarehouseApi;
import ru.yandex.practicum.service.WarehouseService;
import ru.yandex.practicum.warehouse.dto.AddressDto;
import ru.yandex.practicum.warehouse.dto.BookedProductsDto;
import ru.yandex.practicum.warehouse.request.AddProductToWarehouseRequest;
import ru.yandex.practicum.warehouse.request.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.warehouse.request.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.request.ShippedToDeliveryRequest;

import java.util.Map;
import java.util.UUID;

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

    @PostMapping("/shipped")
    public void shipOrderToDelivery(@Valid @RequestBody ShippedToDeliveryRequest request) {
        log.info("Пришел POST запрос на /api/v1/warehouse/shipped с телом: {}", request);
        service.shipOrderToDelivery(request);
        log.info("Отправлен ответ на запрос POST /api/v1/warehouse/shipped");
    }

    @PostMapping("/return")
    public void returnProductsToWarehouse(@RequestBody @NotEmpty Map<UUID, Integer> products) {
        log.info("Пришел POST запрос на /api/v1/warehouse/return");
        service.returnProductsToWarehouse(products);
        log.info("Отправлен ответ на запрос POST /api/v1/warehouse/return");
    }

    @PostMapping("/assembly")
    public BookedProductsDto assemblyProducts(@Valid @RequestBody AssemblyProductsForOrderRequest request) {
        log.info("Пришел POST запрос на /api/v1/warehouse/assembly с телом: {}", request);
        BookedProductsDto bookedProducts = service.assemblyProducts(request);
        log.info("Отправлен ответ на запрос POST /api/v1/warehouse/assembly с телом: {}", bookedProducts);
        return bookedProducts;
    }
}
