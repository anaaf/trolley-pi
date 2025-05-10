package com.example.trollyinterface.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.trollyinterface.model.CartItem;
import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends ViewModel {
    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Double> totalAmount = new MutableLiveData<>(0.0);

    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    public LiveData<Double> getTotalAmount() {
        return totalAmount;
    }

    public void addItem(CartItem item) {
        List<CartItem> currentItems = new ArrayList<>(cartItems.getValue());
        currentItems.add(item);
        cartItems.setValue(currentItems);
        updateTotalAmount();
    }

    public void removeItem(String itemId) {
        List<CartItem> currentItems = new ArrayList<>(cartItems.getValue());
        currentItems.removeIf(item -> item.getId().equals(itemId));
        cartItems.setValue(currentItems);
        updateTotalAmount();
    }

    private void updateTotalAmount() {
        double total = 0.0;
        for (CartItem item : cartItems.getValue()) {
            total += item.getPrice() * item.getQuantity();
        }
        totalAmount.setValue(total);
    }
} 