package ru.yandex.practicum.store.util;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@AllArgsConstructor
public class ProductPageable {

    @Min(value = 0)
    @NotNull
    private Integer page;

    @Min(value = 1)
    @NotNull
    private Integer size;

    @NotNull
    private final Sort sort;
}
