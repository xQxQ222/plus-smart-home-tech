package ru.yandex.practicum.feign.client.order;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.feign.api.OrderApi;
import ru.yandex.practicum.feign.client.config.FeignConfig;

@FeignClient(name = "order", path = "/api/v1/order", configuration = FeignConfig.class)
public interface OrderFeignClient extends OrderApi {
}
