package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mapper.ShoppingStoreMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;
import ru.yandex.practicum.store.dto.ProductDto;
import ru.yandex.practicum.store.enums.ProductCategory;
import ru.yandex.practicum.store.enums.ProductState;
import ru.yandex.practicum.store.exception.ProductNotFoundException;
import ru.yandex.practicum.store.request.SetProductQuantityStateRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingStoreServiceImpl implements ShoppingStoreService {

    private final ProductRepository productRepository;
    private final ShoppingStoreMapper mapper;

    @Override
    public Page<ProductDto> getProductsPageByCategory(ProductCategory productCategory, Pageable productPageable) {
        Page<ProductDto> products = productRepository.findByProductCategory(productCategory, productPageable)
                .map(mapper::toProductDto);
        log.debug("Поиск продуктов по категории {} с параметрами {}. Результат: {}", productCategory, productPageable, products);
        return products;
    }

    @Override
    public ProductDto addNewProduct(ProductDto productDto) {
        Product domainProduct = mapper.toProductDomain(productDto);
        Product addedProduct = productRepository.save(domainProduct);
        log.debug("Создание продукта: {}", addedProduct);
        return mapper.toProductDto(addedProduct);
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        ProductDto productFromDb = getProductById(productDto.getProductId());
        Product newProduct = mapper.toProductDomain(productDto);
        log.debug("Обновление информации о товаре. Старая информация: {}. Новая информация: {}", productFromDb, productDto);
        Product addedProduct = productRepository.save(newProduct);
        return mapper.toProductDto(addedProduct);
    }

    @Override
    public Boolean deleteProduct(UUID productId) {
        ProductDto productFromDb = getProductById(productId);
        if (productFromDb.getProductState().equals(ProductState.DEACTIVATE)) {
            return false;
        }
        productFromDb.setProductState(ProductState.DEACTIVATE);
        log.debug("Удаление товара. Товар с id {} стал недоступным", productId);
        return true;
    }

    @Override
    public Boolean setProductQuantity(SetProductQuantityStateRequest request) {
        ProductDto productFromDb = getProductById(request.getProductId());
        productFromDb.setQuantityState(request.getQuantityState());
        productRepository.save(mapper.toProductDomain(productFromDb));
        log.debug("Изменена информация по количеству товара с id {}. Статус: {}", request.getProductId(), request.getQuantityState());
        return true;
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("Товар с id " + productId + " не найден");
        }
        log.debug("Поиск продукта по id. Id продукта: {}", productId);
        return productRepository.findById(productId).stream().map(mapper::toProductDto).findFirst().get();
    }
}
