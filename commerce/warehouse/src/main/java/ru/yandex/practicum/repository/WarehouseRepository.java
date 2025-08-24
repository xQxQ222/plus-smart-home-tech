package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.WarehouseElement;

import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<WarehouseElement, UUID> {
}
