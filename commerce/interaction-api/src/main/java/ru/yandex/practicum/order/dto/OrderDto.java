package ru.yandex.practicum.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.order.enums.OrderState;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    @NotNull
    private UUID orderId;

    private UUID shoppingCartId;

    @NotEmpty
    private Map<UUID, Integer> products;

    private UUID paymentId;

    private UUID deliveryId;

    @NotNull
    private OrderState state;

    private Double deliveryWeight;

    private Double deliveryVolume;

    private Boolean fragile;

    private Double totalPrice;

    private Double deliveryPrice;

    private Double productPrice;
}
