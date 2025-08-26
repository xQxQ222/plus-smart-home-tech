package ru.yandex.practicum.warehouse.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.warehouse.dto.DimensionDto;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class NewProductInWarehouseRequest {

    @NotNull
    private UUID productId;

    @NotNull
    private Boolean fragile;

    @NotNull
    private DimensionDto dimension;

    @NotNull
    @Min(value = 1)
    private Double weight;
}
