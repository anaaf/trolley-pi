package com.example.trollyinterface.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.trollyinterface.model.CartItem;
import com.example.trollyinterface.model.CartResponse;
import com.example.trollyinterface.api.ApiClient;
import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends AndroidViewModel {
    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public CartViewModel(Application application) {
        super(application);
    }

    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void fetchCartItems() {
        isLoading.setValue(true);
        error.setValue(null);

        ApiClient.getCartItems(new ApiClient.ApiCallback<CartResponse>() {
            @Override
            public void onSuccess(CartResponse response) {
                isLoading.postValue(false);
                if (response.isSuccess()) {
                    cartItems.postValue(response.getItems());
                } else {
                    error.postValue(response.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                isLoading.postValue(false);
                error.postValue(errorMessage);
            }
        });
    }

    public void updateItemQuantity(String productName, int newQuantity) {
        List<CartItem> currentItems = cartItems.getValue();
        if (currentItems != null) {
            List<CartItem> updatedItems = new ArrayList<>(currentItems);
            for (int i = 0; i < updatedItems.size(); i++) {
                CartItem item = updatedItems.get(i);
                if (item.getProductName().equals(productName)) {
                    if (newQuantity <= 0) {
                        updatedItems.remove(i);
                    } else {
                        updatedItems.set(i, new CartItem(
                            item.getProductName(),
                            item.getTotal(),
                            newQuantity
                        ));
                    }
                    break;
                }
            }
            cartItems.setValue(updatedItems);
        }
    }

    public void removeItem(String productName) {
        updateItemQuantity(productName, 0);
    }

    public void clearCart() {
        cartItems.setValue(new ArrayList<>());
    }
} 