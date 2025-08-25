package ru.yandex.practicum.feign.client.cart;

import feign.FeignException;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.cart.request.ChangeProductQuantityRequest;
import ru.yandex.practicum.feign.client.config.FeignConfig;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart", configuration = FeignConfig.class)
public interface CartFeignClient {

    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam(name = "username") String username) throws FeignException;

    @PutMapping
    ShoppingCartDto putProductsInCart(@RequestParam(name = "username") String username, @RequestBody @NotEmpty Map<UUID, Integer> products) throws FeignException;

    @DeleteMapping
    void deactivateUserCart(@RequestParam(name = "username") String username) throws FeignException;

    @PostMapping("/remove")
    ShoppingCartDto removeProductsFromCart(@RequestParam(name = "username") String username, @RequestBody List<UUID> productIds) throws FeignException;

    @PostMapping("/change-quantity")
    ShoppingCartDto changeProductQuantity(@RequestParam(name = "username") String username, @RequestBody ChangeProductQuantityRequest request) throws FeignException;
}
