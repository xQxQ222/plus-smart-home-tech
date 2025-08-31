package ru.yandex.practicum.feign.client.delivery;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.feign.api.DeliveryApi;
import ru.yandex.practicum.feign.client.config.FeignConfig;

@FeignClient(name = "delivery", path = "/api/v1/delivery", configuration = FeignConfig.class)
public interface DeliveryFeignClient extends DeliveryApi {
}
