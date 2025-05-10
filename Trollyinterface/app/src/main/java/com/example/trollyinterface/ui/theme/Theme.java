package com.example.trollyinterface.ui.theme;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.TypedValue;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.example.trollyinterface.R;

public class Theme {
    public static void applyTheme(Context context) {
        // Set the default night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        
        // Apply primary color
        int primaryColor = ContextCompat.getColor(context, R.color.purple_500);
        int primaryDarkColor = ContextCompat.getColor(context, R.color.purple_700);
        int accentColor = ContextCompat.getColor(context, R.color.teal_200);
        
        // Set the status bar color
        if (context instanceof android.app.Activity) {
            android.app.Activity activity = (android.app.Activity) context;
            activity.getWindow().setStatusBarColor(primaryDarkColor);
        }
    }

    public static int getPrimaryColor(Context context) {
        return ContextCompat.getColor(context, R.color.purple_500);
    }

    public static int getPrimaryDarkColor(Context context) {
        return ContextCompat.getColor(context, R.color.purple_700);
    }

    public static int getAccentColor(Context context) {
        return ContextCompat.getColor(context, R.color.teal_200);
    }

    public static ColorStateList getPrimaryColorStateList(Context context) {
        return ColorStateList.valueOf(getPrimaryColor(context));
    }

    public static ColorStateList getAccentColorStateList(Context context) {
        return ColorStateList.valueOf(getAccentColor(context));
    }

    public static float dpToPx(Context context, float dp) {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.getResources().getDisplayMetrics()
        );
    }

    public static float spToPx(Context context, float sp) {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            context.getResources().getDisplayMetrics()
        );
    }
} 