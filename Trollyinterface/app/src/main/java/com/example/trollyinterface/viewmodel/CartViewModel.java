package com.example.trollyinterface.viewmodel;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.trollyinterface.model.CartItem;
import com.example.trollyinterface.model.CartResponse;
import com.example.trollyinterface.api.ApiClient;
import com.example.trollyinterface.ui.screens.CheckoutActivity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends AndroidViewModel {
    private static final long POLL_INTERVAL = 1000; // 1 second in milliseconds
    
    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<BigDecimal> totalAmount = new MutableLiveData<>(BigDecimal.ZERO);
    private final MutableLiveData<String> cartUuid = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable cartPollRunnable = new Runnable() {
        @Override
        public void run() {
            fetchCartItems();
            handler.postDelayed(this, POLL_INTERVAL);
        }
    };

    private final Runnable scanPollRunnable = new Runnable() {
        @Override
        public void run() {
            scanBarcode();
            handler.postDelayed(this, POLL_INTERVAL);
        }
    };

    public CartViewModel(Application application) {
        super(application);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopPolling();
    }

    public void startPolling() {
        handler.post(cartPollRunnable);
        handler.post(scanPollRunnable);
    }

    public void stopPolling() {
        handler.removeCallbacks(cartPollRunnable);
        handler.removeCallbacks(scanPollRunnable);
    }

    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    public LiveData<BigDecimal> getTotalAmount() {
        return totalAmount;
    }

    public LiveData<String> getCartUuid() {
        return cartUuid;
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
                if (response != null && response.getItems() != null) {
                    cartItems.postValue(response.getItems());
                    totalAmount.postValue(response.getTotalAmount());
                    cartUuid.postValue(response.getCartUuid());
                } else {
                    error.postValue("Invalid response from server");
                }
            }

            @Override
            public void onError(String errorMessage) {
                isLoading.postValue(false);
                error.postValue(errorMessage);
            }
        });
    }

    public void scanBarcode() {
        ApiClient.scanBarcode(new ApiClient.ApiCallback<CartResponse>() {
            @Override
            public void onSuccess(CartResponse response) {
                if (response != null && response.getItems() != null) {
                    cartItems.postValue(response.getItems());
                    totalAmount.postValue(response.getTotalAmount());
                    cartUuid.postValue(response.getCartUuid());
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Don't show error for scan failures as they're expected when no barcode is scanned
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
            updateTotalAmount(updatedItems);
        }
    }

    private void updateTotalAmount(List<CartItem> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            total = total.add(item.getTotal().multiply(new BigDecimal(item.getQuantity())));
        }
        totalAmount.setValue(total);
    }

    public void removeItem(String productName) {
        updateItemQuantity(productName, 0);
    }

    public void clearCart() {
        cartItems.setValue(new ArrayList<>());
        totalAmount.setValue(BigDecimal.ZERO);
        cartUuid.setValue(null);
    }

    public void checkout() {
        List<CartItem> currentItems = cartItems.getValue();
        if (currentItems == null || currentItems.isEmpty()) {
            error.setValue("Cart is empty");
            return;
        }

        // Navigate to checkout screen with current cart data
        Intent intent = CheckoutActivity.newIntent(
            getApplication(),
            totalAmount.getValue().toString(),
            currentItems
        );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(intent);
    }
} 