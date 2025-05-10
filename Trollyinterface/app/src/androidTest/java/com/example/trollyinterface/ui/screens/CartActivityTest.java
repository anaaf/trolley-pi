package com.example.trollyinterface.ui.screens;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.trollyinterface.R;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CartActivityTest {

    @Before
    public void setup() {
        // Launch the activity
        ActivityScenario.launch(CartActivity.class);
    }

    @Test
    public void cartDisplaysHardcodedItemsAndTotal() {
        // Check for Apple
        Espresso.onView(ViewMatchers.withText("Apple"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Check for Banana
        Espresso.onView(ViewMatchers.withText("Banana"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Check for Orange
        Espresso.onView(ViewMatchers.withText("Orange"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Check for the correct total
        Espresso.onView(ViewMatchers.withId(R.id.totalAmountText))
                .check(ViewAssertions.matches(ViewMatchers.withText(Matchers.containsString("23"))));
    }
} 