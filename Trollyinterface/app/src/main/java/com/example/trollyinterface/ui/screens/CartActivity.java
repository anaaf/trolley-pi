package com.example.trollyinterface.ui.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.trollyinterface.R;
import com.example.trollyinterface.model.CartItem;
import com.example.trollyinterface.ui.adapters.CartAdapter;
import java.util.Arrays;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Hardcoded cart items
        List<CartItem> hardcodedItems = Arrays.asList(
            new CartItem("1", "Apple", 5.0, 2),
            new CartItem("2", "Banana", 3.0, 3),
            new CartItem("3", "Orange", 4.0, 1)
        );

        // Set up RecyclerView and Adapter
        RecyclerView recyclerView = findViewById(R.id.cartRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CartAdapter adapter = new CartAdapter(hardcodedItems, (itemId) -> {
            Toast.makeText(this, "Please scan item to remove this item", Toast.LENGTH_SHORT).show();
        });
        recyclerView.setAdapter(adapter);

        // Calculate total amount
        double totalAmount = hardcodedItems.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        TextView totalAmountText = findViewById(R.id.totalAmountText);
        totalAmountText.setText(String.format("Total: $%.2f", totalAmount));

        // Explicitly enable the checkout button and set click listener
        Button checkoutButton = findViewById(R.id.checkoutButton);
        checkoutButton.setEnabled(true);
        checkoutButton.setOnClickListener(v -> {
            Intent intent = CheckoutActivity.newIntent(CartActivity.this, totalAmount, hardcodedItems);
            startActivity(intent);
        });
    }
} 