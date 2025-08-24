package ru.yandex.practicum.warehouse.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class BookedProductsDto {

    @NotNull
    @Positive
    private Double deliveryWeight;

    @NotNull
    @Positive
    private Double deliveryVolume;

    @NotNull
    private Boolean fragile;
}
