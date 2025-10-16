package com.prm392.salesapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItemDetail> cartItems;
    private CartItemListener listener;

    public interface CartItemListener {
        void onIncreaseQuantity(int cartItemId, int currentQuantity);
        void onDecreaseQuantity(int cartItemId, int currentQuantity);
        void onRemoveItem(int cartItemId);
    }

    public CartAdapter(List<CartItemDetail> cartItems, CartItemListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItemDetail cartItem = cartItems.get(position);
        holder.productName.setText(cartItem.getProductName());
        holder.productPrice.setText(String.format(Locale.US, "$%.2f", cartItem.getPrice()));
        holder.quantity.setText(String.valueOf(cartItem.getQuantity()));

        Glide.with(holder.itemView.getContext())
                .load(cartItem.getImageURL())
                .into(holder.productImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("PRODUCT_ID", cartItem.getProductID());
            v.getContext().startActivity(intent);
        });

        holder.increaseQuantityButton.setOnClickListener(v -> listener.onIncreaseQuantity(cartItem.getCartItemID(), cartItem.getQuantity()));
        holder.decreaseQuantityButton.setOnClickListener(v -> listener.onDecreaseQuantity(cartItem.getCartItemID(), cartItem.getQuantity()));
        holder.removeItemButton.setOnClickListener(v -> listener.onRemoveItem(cartItem.getCartItemID()));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView quantity;
        ImageButton increaseQuantityButton;
        ImageButton decreaseQuantityButton;
        ImageButton removeItemButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image_cart);
            productName = itemView.findViewById(R.id.product_name_cart);
            productPrice = itemView.findViewById(R.id.product_price_cart);
            quantity = itemView.findViewById(R.id.quantity_cart);
            increaseQuantityButton = itemView.findViewById(R.id.increase_quantity_button);
            decreaseQuantityButton = itemView.findViewById(R.id.decrease_quantity_button);
            removeItemButton = itemView.findViewById(R.id.remove_item_button);
        }
    }
}