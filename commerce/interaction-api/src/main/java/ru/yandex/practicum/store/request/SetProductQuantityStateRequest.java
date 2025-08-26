package ru.yandex.practicum.store.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.store.enums.QuantityState;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SetProductQuantityStateRequest {
    @NotNull
    private UUID productId;

    @NotNull
    private QuantityState quantityState;
}
