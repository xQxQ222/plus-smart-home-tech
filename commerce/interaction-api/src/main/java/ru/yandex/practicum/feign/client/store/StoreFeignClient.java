package ru.yandex.practicum.feign.client.store;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.feign.client.config.FeignConfig;
import ru.yandex.practicum.store.dto.ProductDto;
import ru.yandex.practicum.store.enums.ProductCategory;
import ru.yandex.practicum.store.request.SetProductQuantityStateRequest;

import java.util.UUID;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store", configuration = FeignConfig.class)
public interface StoreFeignClient {
    @GetMapping
    Page<ProductDto> GetProductsByCategory(@RequestParam ProductCategory productCategory, @PageableDefault(sort = "productName") Pageable pageable) throws FeignException;

    @PutMapping
    ProductDto addNewProduct(@RequestBody @Valid ProductDto productDto) throws FeignException;

    @PostMapping
    ProductDto updateProduct(@RequestBody ProductDto productDto) throws FeignException;

    @PostMapping("/removeProductFromStore")
    Boolean removeProduct(@RequestBody UUID productId) throws FeignException;

    @PostMapping("/quantityState")
    Boolean updateQuantityState(@RequestBody @Valid SetProductQuantityStateRequest request) throws FeignException;

    @GetMapping("/{productId}")
    ProductDto getProductById(@PathVariable(name = "productId") UUID productId) throws FeignException;
}
