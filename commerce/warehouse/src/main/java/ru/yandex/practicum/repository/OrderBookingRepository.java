package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.OrderBooked;

import java.util.UUID;

public interface OrderBookingRepository extends JpaRepository<OrderBooked, UUID> {
}
