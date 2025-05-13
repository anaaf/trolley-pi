package com.example.trollyinterface.api;

import android.util.Log;
import com.example.trollyinterface.config.AppConfig;
import com.example.trollyinterface.model.CartResponse;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiClient {
    private static final String TAG = "ApiClient";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Gson gson = new Gson();

    public interface ApiCallback<T> {
        void onSuccess(T response);
        void onError(String error);
    }

    public static void getCartItems(ApiCallback<CartResponse> callback) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(AppConfig.API_BASE_URL + AppConfig.CART_ENDPOINT + AppConfig.CART_UUID);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(AppConfig.CONNECTION_TIMEOUT);
                connection.setReadTimeout(AppConfig.READ_TIMEOUT);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    CartResponse cartResponse = gson.fromJson(response.toString(), CartResponse.class);
                    callback.onSuccess(cartResponse);
                } else {
                    callback.onError("Error: " + responseCode);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching cart items", e);
                callback.onError("Network error: " + e.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    public static void scanBarcode(ApiCallback<CartResponse> callback) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(AppConfig.API_BASE_URL + AppConfig.SCAN_ENDPOINT + AppConfig.CART_UUID);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(AppConfig.CONNECTION_TIMEOUT);
                connection.setReadTimeout(AppConfig.READ_TIMEOUT);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    CartResponse cartResponse = gson.fromJson(response.toString(), CartResponse.class);
                    callback.onSuccess(cartResponse);
                } else {
                    callback.onError("Error: " + responseCode);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error scanning barcode", e);
                callback.onError("Network error: " + e.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }
} 