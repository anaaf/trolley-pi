package com.example.trollyinterface.model;

public class CartItem {
    private final String id;
    private final String name;
    private final double price;
    private final int quantity;
    private final String imageUrl;

    public CartItem(String id, String name, double price, int quantity) {
        this(id, name, price, quantity, null);
    }

    public CartItem(String id, String name, double price, int quantity, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Double.compare(cartItem.price, price) == 0 &&
               quantity == cartItem.quantity &&
               id.equals(cartItem.id) &&
               name.equals(cartItem.name) &&
               (imageUrl == null ? cartItem.imageUrl == null : imageUrl.equals(cartItem.imageUrl));
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (int) (Double.doubleToLongBits(price) ^ (Double.doubleToLongBits(price) >>> 32));
        result = 31 * result + quantity;
        result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CartItem{" +
               "id='" + id + '\'' +
               ", name='" + name + '\'' +
               ", price=" + price +
               ", quantity=" + quantity +
               ", imageUrl='" + imageUrl + '\'' +
               '}';
    }
} 