package ru.yandex.practicum.feign.client.warehouse;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.feign.api.WarehouseApi;
import ru.yandex.practicum.feign.client.config.FeignConfig;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse", fallback = WarehouseFallback.class, configuration = FeignConfig.class)
public interface WarehouseFeignClient extends WarehouseApi {
}
