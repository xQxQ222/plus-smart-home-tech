package ru.yandex.practicum.model;

import jakarta.persistence.*;
import jakarta.ws.rs.DefaultValue;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "warehouse")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseElement {
    @Id
    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "fragile")
    private Boolean fragile;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "quantity")
    private Integer quantity = 0;

    @Embedded
    private Dimension dimension;
}
