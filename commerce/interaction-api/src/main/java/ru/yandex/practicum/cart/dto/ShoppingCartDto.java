package ru.yandex.practicum.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ShoppingCartDto {

    @NotNull
    private UUID shoppingCartId;

    private Map<@NotNull UUID, @Positive Integer> products;
}
