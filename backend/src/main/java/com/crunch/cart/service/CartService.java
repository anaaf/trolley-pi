package com.crunch.cart.service;

import com.crunch.barcode.entity.Barcode;
import com.crunch.barcode.repo.BarcodeRepository;
import com.crunch.barcode.repo.ProductRepository;
import com.crunch.barcode.service.BarcodeService;
import com.crunch.cart.entity.Cart;
import com.crunch.cart.entity.CartItem;
import com.crunch.cart.repository.CartItemRepository;
import com.crunch.cart.repository.CartRepository;
import com.crunch.common.enums.Status;
import com.crunch.common.exceptions.EntityNotFoundException;
import com.crunch.common.exceptions.GeneralException;
import com.crunch.user.model.CartItemResponse;
import com.crunch.user.model.CartRequest;
import com.crunch.user.model.CartResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.crunch.common.error.ErrorType.CART_NOT_FOUND;
import static com.crunch.common.error.ErrorType.REQUIRED_FIELD_MISSING;

@Service
@Transactional
public class CartService {

    @Autowired
    CartRepository cartRepository;
    @Autowired
    private BarcodeService barcodeService;
    @Autowired
    private BarcodeRepository barcodeRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ProductRepository productRepository;

    private Logger log = Logger.getLogger(this.getClass().getSimpleName());

    public CartResponse upsertCart(CartRequest cartRequest) {
        validate(cartRequest);
        Cart cart = cartRequest.getCartUuid() != null ? getCart(cartRequest.getCartUuid()) : cartRepository.save(transform(cartRequest));
        if (cart == null) {
            throw new EntityNotFoundException(CART_NOT_FOUND, cartRequest.getCartUuid().toString(), null);
        }
//        if (cartRequest.getBarcodeNumber() != null && !cartRequest.getBarcodeNumber().isEmpty()) {
//            return transform(cart.getUuid(), addCartItems(cartRequest.getBarcodeNumber(), cart.getUuid()));
//        }
        return new CartResponse();
    }


    public CartItem addCartItems(String barcodeNumber, String cartUuid) {
        Barcode barcode = barcodeRepository.findByBarcodeNumberAndEnabled(barcodeNumber, true);
        CartItem cartItem = transform(cartUuid, barcode.getProductUuid());
        return cartItemRepository.save(cartItem);
    }

    public void barcodeScan(CartRequest cartRequest) {
        validate(cartRequest);
        Cart cart = cartRepository.findByUuidAndEnabled(cartRequest.getCartUuid(), true);
        if(cart == null) {
            cart = new Cart();
            cart.setUuid(cartRequest.getCartUuid());
            cart.setStatus(Status.ACTIVE);
            cart.setTrolleyUuid(cartRequest.getTrolleyUuid());
            cartRepository.save(cart);
        }

        updateCart(cartRequest, cart);
    }

    public void updateCart(CartRequest cartRequest, Cart cart) {
        BigDecimal currentWeight = cart.getWeight();
        BigDecimal newWeight = cartRequest.getWeight();
        BigDecimal uncertainty = BigDecimal.valueOf(0.010);

        if(newWeight.compareTo(currentWeight.add(uncertainty)) > 0) {
            addProductToCart(cartRequest, cart);
        } else if(newWeight.compareTo(currentWeight.subtract(uncertainty))  < 0) {
            removeProductFromCart(cartRequest, cart);
        }
    }


    public void addProductToCart(CartRequest cartRequest, Cart cart) {
        Barcode barcode = barcodeRepository.findByBarcodeNumberAndEnabled(cartRequest.getBarcodeNumber(), true);
        if(barcode == null) {
            log.info("barcode is null");
            return;
        }
        CartItem cartItem = cartItemRepository.findByCartUuidAndProductUuidAndEnabled(cartRequest.getCartUuid(),
                barcode.getProductUuid(), true);
        if(cartItem == null) {
            cartItem = new CartItem();
            cartItem.setCartUuid(cartRequest.getCartUuid());
            cartItem.setProductUuid(barcode.getProductUuid());
            cartItem.setQuantity(1);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        }
        cart.setWeight(cartRequest.getWeight());
        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
    }

    public void removeProductFromCart(CartRequest cartRequest, Cart cart) {
        Barcode barcode = barcodeRepository.findByBarcodeNumberAndEnabled(cartRequest.getBarcodeNumber(), true);
        if(barcode == null) {
            log.info("barcode is null");
            return;
        }

        CartItem cartItem = cartItemRepository.findByCartUuidAndProductUuidAndEnabled(cartRequest.getCartUuid(),
                barcode.getProductUuid(), true);

        if(cartItem != null) {
            if(cartItem.getQuantity() <= 1) {
                cartItem.setEnabled(false);
            } else {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
            }

            cart.setWeight(cartRequest.getWeight());

            cartItemRepository.save(cartItem);
            cartRepository.save(cart);
        }
    }

    public void validate(CartRequest cartRequest) {
        if(cartRequest == null) {
            throw new GeneralException(REQUIRED_FIELD_MISSING, null, null);
        }
        if(cartRequest.getCartUuid() == null) {
            if(cartRequest.getTrolleyUuid() == null) {
                throw new GeneralException(REQUIRED_FIELD_MISSING, null, null);
            }
        }
        if(cartRequest.getBarcodeNumber() == null) {
            throw new GeneralException(REQUIRED_FIELD_MISSING, null, null);
        }
    }

    public Cart getCart(String cartUuid ) {
        return cartRepository.findByUuidAndEnabled(cartUuid, true);
    }

    public CartResponse getCartResponse(String cartUuid) {
        List<CartItem> cartItems = cartItemRepository.findByCartUuidAndEnabled(cartUuid, true);
        return transform(cartUuid, cartItems);
    }

    public CartItem transform(String cartUuid, String productUuid) {
        CartItem item = new CartItem();
        item.setCartUuid(cartUuid);
        item.setProductUuid(productUuid);
        return item;
    }

    public CartResponse transform(String cartUuid, List<CartItem> cartItems) {

        List<CartItemResponse> cartItemsResponse = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
         cartItems.forEach(cartItem -> {
             CartItemResponse responseItem = new CartItemResponse();
             responseItem.setQuantity(cartItem.getQuantity());
             responseItem.productName(cartItem.getProduct().getName());
             responseItem.setTotal(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
             cartItemsResponse.add(responseItem);
         });
            CartResponse response = new CartResponse();
            response.setCartUuid(cartUuid);
            response.setCartItems(cartItemsResponse);
            List<BigDecimal> totals = cartItemsResponse.stream().map(CartItemResponse::getTotal).toList();
            for(BigDecimal total : totals) {
                totalAmount = totalAmount.add(total);
            }
            response.setTotalAmount(totalAmount);
        return response;
    }

    public Cart transform(CartRequest cartRequest) {
        Cart cart = new Cart();
        cart.setStatus(Status.ACTIVE);
        cart.setTrolleyUuid(cartRequest.getTrolleyUuid());
        return cart;
    }

}
