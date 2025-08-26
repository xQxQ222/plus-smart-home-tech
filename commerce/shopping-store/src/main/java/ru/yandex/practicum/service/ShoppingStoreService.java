package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.store.dto.ProductDto;
import ru.yandex.practicum.store.enums.ProductCategory;
import ru.yandex.practicum.store.request.SetProductQuantityStateRequest;

import java.util.UUID;

public interface ShoppingStoreService {
    Page<ProductDto> getProductsPageByCategory(ProductCategory productCategory, Pageable productPageable);

    ProductDto addNewProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    Boolean deleteProduct(UUID productId);

    Boolean setProductQuantity(SetProductQuantityStateRequest request);

    ProductDto getProductById(UUID productId);
}
