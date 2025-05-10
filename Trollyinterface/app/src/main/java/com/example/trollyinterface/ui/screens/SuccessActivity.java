package com.example.trollyinterface.ui.screens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.trollyinterface.R;

public class SuccessActivity extends AppCompatActivity {
    private static final String EXTRA_PAYMENT_METHOD = "extra_payment_method";

    public static Intent newIntent(Context context, String paymentMethod) {
        Intent intent = new Intent(context, SuccessActivity.class);
        intent.putExtra(EXTRA_PAYMENT_METHOD, paymentMethod);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        String paymentMethod = getIntent().getStringExtra(EXTRA_PAYMENT_METHOD);
        
        TextView successMessage = findViewById(R.id.successMessage);
        Button backToCartButton = findViewById(R.id.backToCartButton);

        successMessage.setText(String.format("Your payment via %s has been processed successfully.", 
            paymentMethod));

        backToCartButton.setOnClickListener(v -> {
            // Clear the back stack and return to cart
            Intent intent = new Intent(this, CartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
} 