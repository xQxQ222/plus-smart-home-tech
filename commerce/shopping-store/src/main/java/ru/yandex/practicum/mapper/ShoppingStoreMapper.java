package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.store.dto.ProductDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ShoppingStoreMapper {
    ProductDto toProductDto(Product productDomain);

    @Mapping(target = "price", source = "price")
    Product toProductDomain(ProductDto productDto);
}
