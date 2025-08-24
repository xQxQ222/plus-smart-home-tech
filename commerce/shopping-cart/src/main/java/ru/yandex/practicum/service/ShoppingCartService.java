package ru.yandex.practicum.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.cart.request.ChangeProductQuantityRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {
    ShoppingCartDto getUserCart(String username);

    ShoppingCartDto putProductIntoCart(String username, Map<UUID, Integer> products);

    void deactivateUserCart(String username);

    ShoppingCartDto removeProductsFromCart(String username, List<UUID> productsId);

    ShoppingCartDto changeProductQuantityInCart(String username, ChangeProductQuantityRequest request);
}
