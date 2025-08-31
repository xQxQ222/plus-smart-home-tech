package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.DeliveryAddress;

public interface AddressRepository extends JpaRepository<DeliveryAddress, Long> {
}
