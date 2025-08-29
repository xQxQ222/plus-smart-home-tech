package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.model.DeliveryAddress;
import ru.yandex.practicum.warehouse.dto.AddressDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AddressMapper {
    DeliveryAddress toDomainAddress(AddressDto addressDto);

    AddressDto toAddressDto(DeliveryAddress address);
}
