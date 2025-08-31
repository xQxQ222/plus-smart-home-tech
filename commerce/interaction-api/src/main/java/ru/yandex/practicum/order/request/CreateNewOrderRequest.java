package ru.yandex.practicum.order.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.warehouse.dto.AddressDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateNewOrderRequest {
    @NotNull
    private ShoppingCartDto shoppingCart;

    @NotNull
    private AddressDto deliveryAddress;

    @NotNull
    private String username;
}
