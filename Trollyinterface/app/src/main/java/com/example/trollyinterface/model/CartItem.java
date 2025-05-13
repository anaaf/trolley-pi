package com.example.trollyinterface.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class CartItem implements Parcelable {
    @SerializedName("productName")
    private final String productName;

    @SerializedName("quantity")
    private final int quantity;

    @SerializedName("total")
    private final BigDecimal total;

    public CartItem(String productName, BigDecimal total, int quantity) {
        this.productName = productName;
        this.total = total;
        this.quantity = quantity;
    }

    protected CartItem(Parcel in) {
        productName = in.readString();
        total = new BigDecimal(in.readString());
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

    public String getProductName() {
        return productName;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productName);
        dest.writeString(total.toString());
        dest.writeInt(quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return quantity == cartItem.quantity &&
               productName.equals(cartItem.productName) &&
               total.compareTo(cartItem.total) == 0;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + productName.hashCode();
        result = 31 * result + total.hashCode();
        result = 31 * result + quantity;
        return result;
    }

    @Override
    public String toString() {
        return "CartItem{" +
               "productName='" + productName + '\'' +
               ", total=" + total +
               ", quantity=" + quantity +
               '}';
    }
} 