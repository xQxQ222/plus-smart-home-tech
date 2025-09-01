package ru.yandex.practicum.feign.client.cart;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.feign.api.CartApi;
import ru.yandex.practicum.feign.client.config.FeignConfig;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart", configuration = FeignConfig.class)
public interface CartFeignClient extends CartApi {

}
