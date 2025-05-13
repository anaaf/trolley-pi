package com.example.trollyinterface.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.List;


public class CartResponse {
    @SerializedName("cartUuid")
    private String cartUuid;

    @SerializedName("totalAmount")
    private BigDecimal totalAmount;

    @SerializedName("cartItems")
    private List<CartItem> items;

    public List<CartItem> getItems() {
        return items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getCardUuid() {
        return cartUuid;
    }

} 