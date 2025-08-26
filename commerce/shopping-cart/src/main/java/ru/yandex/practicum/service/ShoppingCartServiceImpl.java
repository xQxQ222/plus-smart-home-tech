package ru.yandex.practicum.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.cart.dto.ShoppingCartDto;
import ru.yandex.practicum.cart.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.cart.exception.NonExistProductException;
import ru.yandex.practicum.cart.exception.ShoppingCartDeactivatedException;
import ru.yandex.practicum.cart.request.ChangeProductQuantityRequest;
import ru.yandex.practicum.feign.client.warehouse.WarehouseFeignClient;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartMapper mapper;
    private final ShoppingCartRepository repository;
    private final WarehouseFeignClient warehouseFeignClient;

    @Override
    public ShoppingCartDto getUserCart(String username) {
        log.debug("Поиск корзины пользователя {}", username);
        return mapper.toCartDto(getCart(username));
    }

    @Override
    public ShoppingCartDto putProductIntoCart(String username, Map<UUID, Integer> products) {
        ShoppingCart shoppingCart = getCart(username);
        checkProductQuantityInWarehouse(shoppingCart.getShoppingCartId(), products);
        Map<UUID, Integer> cartProducts = shoppingCart.getProducts();
        for (UUID id : products.keySet()) {
            if (cartProducts.containsKey(id)) {
                int currentQuantity = cartProducts.get(id);
                currentQuantity += products.get(id);
                cartProducts.put(id, currentQuantity);
            } else {
                cartProducts.put(id, products.get(id));
            }
        }
        shoppingCart.setProducts(cartProducts);
        log.debug("Добавление продуктов в корзину пользователя {}: {}", username, products);
        return mapper.toCartDto(repository.save(shoppingCart));
    }

    @Override
    public void deactivateUserCart(String username) {
        ShoppingCart cart = getCart(username);
        if (!cart.isActive()) {
            throw new ShoppingCartDeactivatedException("Корзина пользователя " + username + " уже деактивирована");
        }
        log.debug("Деактивируем корзину пользователя {}", username);
        cart.setActive(false);
        repository.save(cart);
    }

    @Override
    public ShoppingCartDto removeProductsFromCart(String username, List<UUID> productsId) {
        ShoppingCart cart = getCart(username);
        checkCartStatus(cart);
        checkCartProductsQuantity(cart);
        checkProductsInCart(cart, productsId);
        for (UUID productId : productsId) {
            cart.getProducts().remove(productId);
        }
        return mapper.toCartDto(repository.save(cart));
    }

    @Override
    public ShoppingCartDto changeProductQuantityInCart(String username, ChangeProductQuantityRequest request) {
        ShoppingCart cart = getCart(username);
        checkCartStatus(cart);
        checkCartProductsQuantity(cart);
        UUID productId = request.getProductId();
        if (!cart.getProducts().containsKey(productId)) {
            throw new NonExistProductException("Продукта с id " + productId + " нет в корзине");
        }
        log.debug("Изменение количества товара (id {}) в корзине пользователя {} на {}", productId, username, request.getNewQuantity());
        cart.getProducts().put(productId, request.getNewQuantity());
        return mapper.toCartDto(repository.save(cart));
    }

    private ShoppingCart createNewShoppingCart(String username) {
        ShoppingCart newCart = ShoppingCart.builder()
                .username(username)
                .active(true)
                .products(new HashMap<>())
                .build();
        return repository.save(newCart);
    }

    private void checkUsername(String username) {
        if (username.isBlank()) {
            throw new ValidationException("Имя пользователя не должно быть пустым");
        }
    }

    private void checkCartStatus(ShoppingCart shoppingCart) {
        if (!shoppingCart.isActive()) {
            throw new ShoppingCartDeactivatedException("Корзина пользователя " + shoppingCart.getUsername() + " деактивирована");
        }
    }

    private void checkCartProductsQuantity(ShoppingCart shoppingCart) {
        if (shoppingCart.getProducts().isEmpty()) {
            throw new NoProductsInShoppingCartException("Корзина пользователя " + shoppingCart.getUsername() + " пуста");
        }
    }

    private void checkProductQuantityInWarehouse(UUID shoppingCartId, Map<UUID, Integer> products) {
        log.debug("Feign. Проверка доступности товаров на складе");
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto(shoppingCartId, products);
        warehouseFeignClient.checkShoppingCartProductsInWarehouse(shoppingCartDto);
    }

    private void checkProductsInCart(ShoppingCart cart, List<UUID> products) {
        for (UUID productId : products) {
            if (!cart.getProducts().containsKey(productId)) {
                throw new NonExistProductException("Товара с id " + productId + " нет в корзине пользователя");
            }
        }
    }

    private ShoppingCart getCart(String username) {
        checkUsername(username);
        if (repository.existsByUsername(username)) {
            log.debug("Корзина найдена");
            return repository.findByUsername(username).get();
        }
        log.debug("Корзина не найдена. Создается новая");
        return createNewShoppingCart(username);
    }
}
