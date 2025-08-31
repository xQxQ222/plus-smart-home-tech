package ru.yandex.practicum.feign.client.payment;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.feign.api.PaymentApi;
import ru.yandex.practicum.feign.client.config.FeignConfig;

@FeignClient(name = "payment", path = "/api/v1/payment", configuration = FeignConfig.class)
public interface PaymentFeignClient extends PaymentApi {
}
