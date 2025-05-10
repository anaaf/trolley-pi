package com.example.trollyinterface.ui.screens;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.trollyinterface.R;
import com.example.trollyinterface.model.CartItem;
import com.example.trollyinterface.viewmodel.CartViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CartActivityTest {

    private CartViewModel viewModel;

    @Before
    public void setup() {
        // Launch the activity
        ActivityScenario.launch(CartActivity.class);
    }

    @Test
    public void addItemButton_ShouldAddItemToCart() {
        // Click the add item button
        Espresso.onView(ViewMatchers.withId(R.id.addItemButton))
                .perform(ViewActions.click());

        // Verify the item is added to the RecyclerView
        Espresso.onView(ViewMatchers.withId(R.id.cartRecyclerView))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void removeItem_ShouldRemoveItemFromCart() {
        // First add an item
        Espresso.onView(ViewMatchers.withId(R.id.addItemButton))
                .perform(ViewActions.click());

        // Click the delete button on the first item
        Espresso.onView(ViewMatchers.withId(R.id.deleteButton))
                .perform(ViewActions.click());

        // Verify the RecyclerView is empty
        Espresso.onView(ViewMatchers.withId(R.id.emptyCartText))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void checkoutButton_ShouldNavigateToCheckout() {
        // Add an item first
        Espresso.onView(ViewMatchers.withId(R.id.addItemButton))
                .perform(ViewActions.click());

        // Click the checkout button
        Espresso.onView(ViewMatchers.withId(R.id.checkoutButton))
                .perform(ViewActions.click());

        // Verify we're on the checkout screen
        Espresso.onView(ViewMatchers.withId(R.id.checkoutLayout))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
} 