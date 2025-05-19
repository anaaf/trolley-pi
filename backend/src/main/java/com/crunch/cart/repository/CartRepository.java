package com.crunch.cart.repository;

import com.crunch.cart.entity.Cart;
import com.crunch.common.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByUuidAndEnabled(String uuid, boolean enabled);
    Cart findFirstByTrolleyUuidAndStatusAndEnabledOrderByCreationDateDesc(String trolleyUuid, Status status, boolean enabled);

}
