package com.crunch.cart.repository;

import com.crunch.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    CartItem findByCartUuidAndProductUuidAndEnabled(String cartUuid, String productUuid, boolean enabled);
    List<CartItem> findByCartUuidAndEnabled(String cartUuid, boolean enabled);
}
