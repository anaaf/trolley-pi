package com.example.trollyinterface.ui.screens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.trollyinterface.R;
import com.example.trollyinterface.model.CartItem;
import com.example.trollyinterface.ui.adapters.OrderSummaryAdapter;
import com.example.trollyinterface.viewmodel.CartViewModel;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {
    private CartViewModel viewModel;
    private RecyclerView orderSummaryRecyclerView;
    private TextView totalAmountText;
    private RadioGroup paymentMethodGroup;
    private EditText cardNumberInput;
    private EditText expiryDateInput;
    private EditText cvvInput;
    private Button completePaymentButton;

    public static Intent newIntent(Context context) {
        return new Intent(context, CheckoutActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        viewModel = new CartViewModel();
        
        // Initialize views
        orderSummaryRecyclerView = findViewById(R.id.orderSummaryRecyclerView);
        totalAmountText = findViewById(R.id.totalAmountText);
        paymentMethodGroup = findViewById(R.id.paymentMethodGroup);
        cardNumberInput = findViewById(R.id.cardNumberInput);
        expiryDateInput = findViewById(R.id.expiryDateInput);
        cvvInput = findViewById(R.id.cvvInput);
        completePaymentButton = findViewById(R.id.completePaymentButton);

        // Setup RecyclerView
        orderSummaryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        OrderSummaryAdapter adapter = new OrderSummaryAdapter();
        orderSummaryRecyclerView.setAdapter(adapter);

        // Setup payment method selection
        paymentMethodGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isCardPayment = checkedId == R.id.cardPaymentRadio;
            cardNumberInput.setVisibility(isCardPayment ? View.VISIBLE : View.GONE);
            expiryDateInput.setVisibility(isCardPayment ? View.VISIBLE : View.GONE);
            cvvInput.setVisibility(isCardPayment ? View.VISIBLE : View.GONE);
        });

        // Setup complete payment button
        completePaymentButton.setOnClickListener(v -> onCompletePayment());

        // Observe cart items and total
        viewModel.getCartItems().observe(this, adapter::updateItems);
        viewModel.getTotalAmount().observe(this, total -> 
            totalAmountText.setText(String.format("Total: $%.2f", total)));
    }

    private void onCompletePayment() {
        String paymentMethod = ((RadioButton) findViewById(paymentMethodGroup.getCheckedRadioButtonId()))
            .getText().toString();

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
        String cardNumber = cardNumberInput.getText().toString();
        String expiryDate = expiryDateInput.getText().toString();
        String cvv = cvvInput.getText().toString();

        if (cardNumber.length() != 16) {
            cardNumberInput.setError("Invalid card number");
            return false;
        }
        if (!expiryDate.matches("\\d{2}/\\d{2}")) {
            expiryDateInput.setError("Invalid expiry date (MM/YY)");
            return false;
        }
        if (cvv.length() != 3) {
            cvvInput.setError("Invalid CVV");
            return false;
        }
        return true;
    }
} 