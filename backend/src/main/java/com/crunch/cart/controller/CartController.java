package com.crunch.cart.controller;

import com.crunch.cart.service.CartService;
import com.crunch.user.api.CartApi;
import com.crunch.user.model.CartRequest;
import com.crunch.user.model.CartResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1")
public class CartController implements CartApi {

    @Autowired
    CartService cartService;

    @Override
    public ResponseEntity<Void> barcodeScanOnCart(CartRequest cartRequest) {
        cartService.barcodeScan(cartRequest);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<CartResponse> eventAtCard(CartRequest cartRequest) {
        return null;
    }

    @Override
    public ResponseEntity<CartResponse> getCart(String cartUuid) {
        return ResponseEntity.ok(cartService.getCartResponse(cartUuid));
    }
}
