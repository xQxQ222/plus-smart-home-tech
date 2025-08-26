package ru.yandex.practicum.feign.api;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.cart.request.ChangeProductQuantityRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CartApi {
    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam(name = "username") String username);

    @PutMapping
    ShoppingCartDto putProductsInCart(@RequestParam(name = "username") String username, @RequestBody @NotEmpty Map<UUID, Integer> products);

    @DeleteMapping
    void deactivateUserCart(@RequestParam(name = "username") String username);

    @PostMapping("/remove")
    ShoppingCartDto removeProductsFromCart(@RequestParam(name = "username") String username, @RequestBody List<UUID> productIds);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeProductQuantity(@RequestParam(name = "username") String username, @RequestBody ChangeProductQuantityRequest request);
}
