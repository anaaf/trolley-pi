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
import androidx.lifecycle.ViewModelProvider;
import com.example.trollyinterface.viewmodel.CartViewModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.example.trollyinterface.config.AppConfig;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private TextView totalAmountText;
    private Button checkoutButton;
    private List<CartItem> cartItems;
    private CartViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        initializeViews();
        setupRecyclerView();
        setupViewModel();
        setupCheckoutButton();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.cartRecyclerView);
        totalAmountText = findViewById(R.id.totalAmountText);
        checkoutButton = findViewById(R.id.checkoutButton);
        cartItems = new ArrayList<>();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(cartItems, productName -> viewModel.updateItemQuantity(productName, 0));
        recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(CartViewModel.class);
        
        // Observe ViewModel
        viewModel.getCartItems().observe(this, this::updateCartItems);
        viewModel.getError().observe(this, this::showError);

        // Start polling for cart updates
        viewModel.startPolling();
    }

    private void setupCheckoutButton() {
        checkoutButton.setOnClickListener(v -> {
            if (!cartItems.isEmpty()) {
                Intent intent = new Intent(this, CheckoutActivity.class);
                intent.putExtra("total_amount", totalAmountText.getText().toString());
                intent.putParcelableArrayListExtra("cart_items", new ArrayList<>(cartItems));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewModel != null) {
            viewModel.stopPolling();
        }
    }

    private void updateCartItems(List<CartItem> items) {
        if (items != null && totalAmountText != null && checkoutButton != null) {
            cartItems.clear();
            cartItems.addAll(items);
            adapter.updateItems(cartItems);
            
            // Update total amount
            BigDecimal total = BigDecimal.ZERO;
            for (CartItem item : items) {
                // Each item's total already includes quantity, so we just add it directly
                total = total.add(item.getTotal());
            }
            totalAmountText.setText(String.format("Total: $%.2f", total.doubleValue()));
            
            // Enable/disable checkout button
            checkoutButton.setEnabled(!items.isEmpty());
        }
    }

    private void showError(String message) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }
} 