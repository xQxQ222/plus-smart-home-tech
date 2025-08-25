package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.service.ShoppingStoreService;
import ru.yandex.practicum.store.dto.ProductDto;
import ru.yandex.practicum.store.enums.ProductCategory;
import ru.yandex.practicum.store.enums.QuantityState;
import ru.yandex.practicum.store.request.SetProductQuantityStateRequest;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/shopping-store")
@RequiredArgsConstructor
@Slf4j
public class ShoppingStoreController {

    private final ShoppingStoreService storeService;

    @GetMapping
    public Page<ProductDto> GetProductsByCategory(@RequestParam(name = "category") ProductCategory productCategory, @PageableDefault(sort = "productName") Pageable pageable) {
        log.info("Пришел GET запрос /api/v1/shopping-store с параметрами: Product category - {}, Pageable - {}", productCategory, pageable);
        Page<ProductDto> products = storeService.getProductsPageByCategory(productCategory, pageable);
        log.info("Отправлен ответ на GET запрос /api/v1/shopping-store с телом: {}", products);
        return products;
    }

    @PutMapping
    public ProductDto addNewProduct(@RequestBody @Valid ProductDto productDto) {
        log.info("Пришел PUT запрос /api/v1/shopping-store с телом: {}", productDto);
        ProductDto product = storeService.addNewProduct(productDto);
        log.info("Отправлен ответ на запрос PUT /api/v1/shopping-store с телом: {}", product);
        return product;
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody ProductDto productDto) {
        log.info("Пришел POST запрос /api/v1/shopping-store с телом: {}", productDto);
        ProductDto product = storeService.updateProduct(productDto);
        log.info("Отправлен ответ на запрос POST /api/v1/shopping-store с телом: {}", product);
        return product;
    }

    @PostMapping("/removeProductFromStore")
    public Boolean removeProduct(@RequestBody UUID productId) {
        log.info("Пришел POST запрос /api/v1/shopping-store/removeProductFromStore с телом: {}", productId);
        Boolean isRemoved = storeService.deleteProduct(productId);
        log.info("Отправлен ответ на запрос POST /api/v1/shopping-store/removeProductFromStore. Успешно: {}", isRemoved);
        return isRemoved;
    }

    @PostMapping("/quantityState")
    public Boolean updateQuantityState(@ModelAttribute SetProductQuantityStateRequest request) {
        log.info("Пришел POST запрос /api/v1/shopping-store/quantityState?productId={}&quantityState={}",request.getProductId(), request.getQuantityState());
        Boolean isUpdated = storeService.setProductQuantity(request);
        log.info("Отправлен ответ на запрос POST /api/v1/shopping-store/quantityState?quantityState?productId={}&quantityState={}. Успешно: {}",request.getProductId(), request.getQuantityState(), isUpdated);
        return isUpdated;
    }

    @GetMapping("/{productId}")
    public ProductDto getProductById(@PathVariable(name = "productId") UUID productId) {
        log.info("Пришел GET запрос /api/v1/shopping-store/{}", productId);
        ProductDto product = storeService.getProductById(productId);
        log.info("Отправлен ответ на запрос GET /api/v1/shopping-store/{} с телом: {}", productId, product);
        return product;
    }

}
