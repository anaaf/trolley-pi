package com.example.trollyinterface.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.trollyinterface.R;
import com.example.trollyinterface.model.CartItem;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> items;
    private final Consumer<String> onDeleteClick;

    public CartAdapter(List<CartItem> items, Consumer<String> onDeleteClick) {
        this.items = new ArrayList<>(items);
        this.onDeleteClick = onDeleteClick;
    }

    public void updateItems(List<CartItem> newItems) {
        this.items = new ArrayList<>(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameText;
        private final TextView priceText;
        private final ImageButton deleteButton;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.itemNameText);
            priceText = itemView.findViewById(R.id.itemPriceText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        void bind(CartItem item) {
            nameText.setText(item.getName());
            priceText.setText(String.format("$%.2f x %d", item.getPrice(), item.getQuantity()));
            deleteButton.setOnClickListener(v -> onDeleteClick.accept(item.getId()));
        }
    }
} 