package com.example.trollyinterface.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CartResponse {
    @SerializedName("cartUuid")
    private boolean cartUuid;

    @SerializedName("totalAmount")
    private BigDecimal totalAmount;

    @SerializedName("cartItems")
    private List<CartItem> items;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<CartItem> getItems() {
        return items;
    }
} 