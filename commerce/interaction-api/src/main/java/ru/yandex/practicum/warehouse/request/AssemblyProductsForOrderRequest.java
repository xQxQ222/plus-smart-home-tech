package ru.yandex.practicum.warehouse.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssemblyProductsForOrderRequest {
    @NotNull
    private Map<UUID, Integer> products;

    @NotNull
    private UUID orderId;
}
