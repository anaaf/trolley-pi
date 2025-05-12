package com.example.trollyinterface.ui.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.trollyinterface.R;
import com.example.trollyinterface.model.CartItem;
import com.example.trollyinterface.ui.adapters.CartAdapter;
import com.example.trollyinterface.api.ApiClient;
import com.example.trollyinterface.model.CartResponse;
import java.util.ArrayList;
import java.util.List;
import com.example.trollyinterface.config.AppConfig;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private TextView totalAmountText;
    private Button checkoutButton;
    private ProgressBar progressBar;
    private List<CartItem> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initializeViews();
        setupRecyclerView();
        fetchCartData();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.cartRecyclerView);
        totalAmountText = findViewById(R.id.totalAmountText);
        checkoutButton = findViewById(R.id.checkoutButton);
        progressBar = findViewById(R.id.progressBar);
        cartItems = new ArrayList<>();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(cartItems, this::handleItemDelete);
        recyclerView.setAdapter(adapter);
    }

    private void handleItemDelete(String itemId) {
        Toast.makeText(this, "Please scan item to remove this item", Toast.LENGTH_SHORT).show();
    }

    private void fetchCartData() {
        showLoading(true);
        
        ApiClient.getCartItems(AppConfig.DEFAULT_USER_ID, new ApiClient.ApiCallback<CartResponse>() {
            @Override
            public void onSuccess(CartResponse response) {
                runOnUiThread(() -> {
                    showLoading(false);
                    if (response.isSuccess()) {
                        updateCartItems(response.getItems());
                    } else {
                        showError(response.getMessage());
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showError(error);
                });
            }
        });
    }

    private void updateCartItems(List<CartItem> items) {
        final List<CartItem> finalItems = items != null ? new ArrayList<>(items) : new ArrayList<>();
        
        cartItems.clear();
        cartItems.addAll(finalItems);
        adapter.updateItems(cartItems);

        // Calculate and update total amount
        double totalAmount = finalItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        totalAmountText.setText(String.format("Total: $%.2f", totalAmount));

        // Enable checkout button if there are items
        checkoutButton.setEnabled(!finalItems.isEmpty());
        checkoutButton.setOnClickListener(v -> {
            Intent intent = CheckoutActivity.newIntent(CartActivity.this, totalAmount, finalItems);
            startActivity(intent);
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            checkoutButton.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            checkoutButton.setEnabled(!cartItems.isEmpty());
        }
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
} 