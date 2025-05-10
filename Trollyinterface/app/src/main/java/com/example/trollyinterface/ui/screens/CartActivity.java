package com.example.trollyinterface.ui.screens;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.trollyinterface.R;
import com.example.trollyinterface.model.CartItem;
import com.example.trollyinterface.ui.adapters.CartAdapter;
import com.example.trollyinterface.viewmodel.CartViewModel;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private CartViewModel viewModel;
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private TextView totalAmountText;
    private Button checkoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        viewModel = new CartViewModel();
        
        // Initialize views
        recyclerView = findViewById(R.id.cartRecyclerView);
        totalAmountText = findViewById(R.id.totalAmountText);
        checkoutButton = findViewById(R.id.checkoutButton);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(new ArrayList<>(), this::onItemDeleted);
        recyclerView.setAdapter(adapter);

        // Setup checkout button
        checkoutButton.setOnClickListener(v -> onCheckoutClick());

        // Observe cart items
        viewModel.getCartItems().observe(this, this::updateCartItems);
        viewModel.getTotalAmount().observe(this, this::updateTotalAmount);
    }

    private void updateCartItems(List<CartItem> items) {
        adapter.updateItems(items);
        checkoutButton.setEnabled(!items.isEmpty());
    }

    private void updateTotalAmount(Double total) {
        totalAmountText.setText(String.format("Total: $%.2f", total));
    }

    private void onItemDeleted(String itemId) {
        viewModel.removeItem(itemId);
    }

    private void onCheckoutClick() {
        if (!viewModel.getCartItems().getValue().isEmpty()) {
            startActivity(CheckoutActivity.newIntent(this));
        } else {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
        }
    }
} 