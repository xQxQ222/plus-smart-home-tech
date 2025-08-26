package ru.yandex.practicum.feign.client.store;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.feign.api.StoreApi;
import ru.yandex.practicum.feign.client.config.FeignConfig;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store", configuration = FeignConfig.class)
public interface StoreFeignClient extends StoreApi {

}
