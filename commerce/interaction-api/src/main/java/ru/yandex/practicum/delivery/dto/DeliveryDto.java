package ru.yandex.practicum.delivery.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.delivery.enums.DeliveryState;
import ru.yandex.practicum.warehouse.dto.AddressDto;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryDto {
    @NotNull
    private UUID deliveryId;

    @NotNull
    private AddressDto fromAddress;

    @NotNull
    private AddressDto toAddress;

    @NotNull
    private UUID orderId;

    @NotNull
    private DeliveryState deliveryState;
}
