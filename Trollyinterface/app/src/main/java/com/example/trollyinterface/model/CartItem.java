package com.example.trollyinterface.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class CartItem implements Parcelable {
    @SerializedName("productName")
    private String productName;

    @SerializedName("quantity")
    private final String quantity;

    @SerializedName("total")
    private final double price;

    public CartItem(String id, String name, double price, int quantity) {
        this(id, name, price, quantity, null);
    }

    public CartItem(String id, String productName, double price, int quantity, String imageUrl) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    protected CartItem(Parcel in) {
        productName = in.readString();
        price = in.readDouble();
        quantity = in.readInt();
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeInt(quantity);
        dest.writeString(imageUrl);
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