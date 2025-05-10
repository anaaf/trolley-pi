package com.example.trollyinterface.ui.screens;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.trollyinterface.R;
import com.example.trollyinterface.adapters.OrderSummaryAdapter;
import com.example.trollyinterface.model.CartItem;
import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {
    private static final String EXTRA_TOTAL_AMOUNT = "total_amount";
    private static final String EXTRA_CART_ITEMS = "cart_items";

    private RecyclerView orderSummaryRecyclerView;
    private TextView totalAmountText;
    private RadioGroup paymentMethodGroup;
    private EditText cardNumberInput;
    private EditText expiryDateInput;
    private EditText cvvInput;
    private Button completePaymentButton;
    private List<CartItem> cartItems;

    public static Intent newIntent(Context context, double totalAmount, List<CartItem> items) {
        Intent intent = new Intent(context, CheckoutActivity.class);
        intent.putExtra(EXTRA_TOTAL_AMOUNT, totalAmount);
        intent.putParcelableArrayListExtra(EXTRA_CART_ITEMS, new ArrayList<>(items));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize views
        initializeViews();
        
        // Get data from intent
        double totalAmount = getIntent().getDoubleExtra(EXTRA_TOTAL_AMOUNT, 0.0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            cartItems = getIntent().getParcelableArrayListExtra(EXTRA_CART_ITEMS, CartItem.class);
        } else {
            cartItems = getIntent().getParcelableArrayListExtra(EXTRA_CART_ITEMS);
        }
        
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }

        // Setup RecyclerView
        setupRecyclerView();
        
        // Setup payment method selection
        setupPaymentMethodSelection();
        
        // Setup complete payment button
        setupCompletePaymentButton();
        
        // Display total amount
        totalAmountText.setText(String.format("Total: $%.2f", totalAmount));
    }

    private void initializeViews() {
        orderSummaryRecyclerView = findViewById(R.id.orderSummaryRecyclerView);
        totalAmountText = findViewById(R.id.totalAmountText);
        paymentMethodGroup = findViewById(R.id.paymentMethodGroup);
        cardNumberInput = findViewById(R.id.cardNumberInput);
        expiryDateInput = findViewById(R.id.expiryDateInput);
        cvvInput = findViewById(R.id.cvvInput);
        completePaymentButton = findViewById(R.id.completePaymentButton);
    }

    private void setupRecyclerView() {
        orderSummaryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        OrderSummaryAdapter adapter = new OrderSummaryAdapter(cartItems);
        orderSummaryRecyclerView.setAdapter(adapter);
    }

    private void setupPaymentMethodSelection() {
        paymentMethodGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isCardPayment = checkedId == R.id.cardPaymentRadio;
            cardNumberInput.setVisibility(isCardPayment ? View.VISIBLE : View.GONE);
            expiryDateInput.setVisibility(isCardPayment ? View.VISIBLE : View.GONE);
            cvvInput.setVisibility(isCardPayment ? View.VISIBLE : View.GONE);
        });
    }

    private void setupCompletePaymentButton() {
        completePaymentButton.setOnClickListener(v -> onCompletePayment());
    }

    private void onCompletePayment() {
        int selectedPaymentId = paymentMethodGroup.getCheckedRadioButtonId();
        if (selectedPaymentId == -1) {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            return;
        }

        String paymentMethod = ((RadioButton) findViewById(selectedPaymentId)).getText().toString();

        if (paymentMethod.equals("Card")) {
            if (!validateCardInputs()) {
                return;
            }
        }

        // Process payment and navigate to success screen
        startActivity(SuccessActivity.newIntent(this, paymentMethod));
        finish();
    }

    private boolean validateCardInputs() {
        String cardNumber = cardNumberInput.getText().toString().trim();
        String expiryDate = expiryDateInput.getText().toString().trim();
        String cvv = cvvInput.getText().toString().trim();

        if (cardNumber.isEmpty()) {
            cardNumberInput.setError("Card number is required");
            return false;
        }
        if (!cardNumber.matches("\\d{16}")) {
            cardNumberInput.setError("Invalid card number (must be 16 digits)");
            return false;
        }

        if (expiryDate.isEmpty()) {
            expiryDateInput.setError("Expiry date is required");
            return false;
        }
        if (!expiryDate.matches("\\d{2}/\\d{2}")) {
            expiryDateInput.setError("Invalid expiry date (MM/YY)");
            return false;
        }

        if (cvv.isEmpty()) {
            cvvInput.setError("CVV is required");
            return false;
        }
        if (!cvv.matches("\\d{3}")) {
            cvvInput.setError("Invalid CVV (must be 3 digits)");
            return false;
        }

        return true;
    }
} 