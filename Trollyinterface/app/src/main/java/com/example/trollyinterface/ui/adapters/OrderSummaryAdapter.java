package com.example.trollyinterface.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.trollyinterface.R;
import com.example.trollyinterface.model.CartItem;
import java.util.ArrayList;
import java.util.List;

public class OrderSummaryAdapter extends RecyclerView.Adapter<OrderSummaryAdapter.OrderSummaryViewHolder> {
    private List<CartItem> items;

    public OrderSummaryAdapter() {
        this.items = new ArrayList<>();
    }

    public void updateItems(List<CartItem> newItems) {
        this.items = new ArrayList<>(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderSummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_order_summary, parent, false);
        return new OrderSummaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderSummaryViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class OrderSummaryViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView priceText;

        OrderSummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.itemNameText);
            priceText = itemView.findViewById(R.id.itemPriceText);
        }

        void bind(CartItem item) {
            nameText.setText(item.getName());
            priceText.setText(String.format("$%.2f", item.getPrice() * item.getQuantity()));
        }
    }
} 