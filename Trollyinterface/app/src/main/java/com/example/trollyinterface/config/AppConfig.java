package com.example.trollyinterface.config;

public class AppConfig {
    // API Configuration
    public static final String API_BASE_URL = "http://56.228.26.246:8080/"; // Replace with your actual API base URL
    
    // API Endpoints
    public static final String CART_ENDPOINT = "v1/cart/";

    public static final String CART_UUID = "da4c6590-b0bf-4ee3-b436-fa868188f520";
    
    // Timeouts
    public static final int CONNECTION_TIMEOUT = 5000; // 5 seconds
    public static final int READ_TIMEOUT = 5000; // 5 seconds
    
    // Other Configuration
    public static final String DEFAULT_USER_ID = "user123"; // Replace with actual user ID logic
} 