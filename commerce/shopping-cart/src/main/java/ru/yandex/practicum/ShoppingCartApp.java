package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.feign.client.cart.CartFeignClient;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableFeignClients(clients = CartFeignClient.class)
public class ShoppingCartApp {
    public static void main(String[] args) {
        SpringApplication.run(ShoppingCartApp.class, args);
    }
}