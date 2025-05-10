package com.example.trollyinterface.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import com.example.trollyinterface.model.CartItem;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.util.List;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class CartViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private CartViewModel viewModel;

    @Before
    public void setup() {
        viewModel = new CartViewModel();
    }

    @Test
    public void addItem_ShouldIncreaseCartSize() {
        // Given
        CartItem item = new CartItem("1", "Test Product", 10.0, 1);

        // When
        viewModel.addItem(item);

        // Then
        List<CartItem> items = viewModel.getCartItems().getValue();
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(item, items.get(0));
    }

    @Test
    public void addItem_ShouldUpdateTotalAmount() {
        // Given
        CartItem item1 = new CartItem("1", "Product 1", 10.0, 2);
        CartItem item2 = new CartItem("2", "Product 2", 15.0, 1);

        // When
        viewModel.addItem(item1);
        viewModel.addItem(item2);

        // Then
        Double total = viewModel.getTotalAmount().getValue();
        assertNotNull(total);
        assertEquals(35.0, total, 0.001); // (10.0 * 2) + (15.0 * 1) = 35.0
    }

    @Test
    public void removeItem_ShouldDecreaseCartSize() {
        // Given
        CartItem item = new CartItem("1", "Test Product", 10.0, 1);
        viewModel.addItem(item);

        // When
        viewModel.removeItem("1");

        // Then
        List<CartItem> items = viewModel.getCartItems().getValue();
        assertNotNull(items);
        assertTrue(items.isEmpty());
    }

    @Test
    public void removeItem_ShouldUpdateTotalAmount() {
        // Given
        CartItem item1 = new CartItem("1", "Product 1", 10.0, 2);
        CartItem item2 = new CartItem("2", "Product 2", 15.0, 1);
        viewModel.addItem(item1);
        viewModel.addItem(item2);

        // When
        viewModel.removeItem("1");

        // Then
        Double total = viewModel.getTotalAmount().getValue();
        assertNotNull(total);
        assertEquals(15.0, total, 0.001); // Only item2 remains: 15.0 * 1 = 15.0
    }
} 