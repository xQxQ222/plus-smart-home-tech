package ru.yandex.practicum.controller;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.cart.request.ChangeProductQuantityRequest;
import ru.yandex.practicum.feign.api.CartApi;
import ru.yandex.practicum.feign.client.cart.CartClient;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/shopping-cart")
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartController implements CartApi {
    private final ShoppingCartService cartService;

    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam(name = "username") String username) {
        log.info("Пришел GET запрос /api/v1/shopping-cart от пользователя {}", username);
        ShoppingCartDto cart = cartService.getUserCart(username);
        log.info("Отправлен ответ на запрос GET /api/v1/shopping-cart пользователь {} с телом: {}", username, cart);
        return cart;
    }

    @PutMapping
    public ShoppingCartDto putProductsInCart(@RequestParam(name = "username") String username, @RequestBody @NotEmpty Map<UUID, Integer> products) {
        log.info("Пришел PUT запрос /api/v1/shopping-cart от пользователя {} с телом: {}", username, products);
        ShoppingCartDto cart = cartService.putProductIntoCart(username, products);
        log.info("Отправлен ответ на запрос PUT /api/v1/shopping-cart пользователь {} с телом: {}", username, cart);
        return cart;
    }

    @DeleteMapping
    public void deactivateUserCart(@RequestParam(name = "username") String username) {
        log.info("Пришел DELETE запрос /api/v1/shopping-cart от пользователя {}", username);
        cartService.deactivateUserCart(username);
        log.info("Отправлен ответ на запрос DELETE /api/v1/shopping-cart пользователю {}", username);
    }

    @PostMapping("/remove")
    public ShoppingCartDto removeProductsFromCart(@RequestParam(name = "username") String username, @RequestBody List<UUID> productIds) {
        log.info("Пришел POST запрос /api/v1/shopping-cart/remove от пользователя {} с телом: {}", username, productIds);
        ShoppingCartDto cart = cartService.removeProductsFromCart(username, productIds);
        log.info("Отправлен ответ на запрос POST /api/v1/shopping-cart/remove пользователь {} с телом: {}", username, cart);
        return cart;
    }

    @PostMapping("/change-quantity")
    public ShoppingCartDto changeProductQuantity(@RequestParam(name = "username") String username, @RequestBody ChangeProductQuantityRequest request) {
        log.info("Пришел POST запрос /api/v1/shopping-cart/change-quantity от пользователя {} с телом: {}", username, request);
        ShoppingCartDto cart = cartService.changeProductQuantityInCart(username, request);
        log.info("Отправлен ответ на запрос POST /api/v1/shopping-cart/change-quantity пользователь {} с телом: {}", username, cart);
        return cart;
    }
}
