package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.order.dto.OrderDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {
    OrderDto toOrderDto(Order order);

    Order toDomainOrder(OrderDto orderDto);
}
