package com.prm392.salesapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.VH> {
    public interface OrderClickListener { void onOrderClicked(int orderId); }

    private List<OrderSummary> items;
    private OrderClickListener listener;

    public OrdersAdapter(List<OrderSummary> items, OrderClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_summary, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        OrderSummary s = items.get(position);
    holder.id.setText("Order #" + s.getOrderID());
    holder.status.setText(s.getOrderStatus());
    holder.date.setText(s.getOrderDate());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOrderClicked(s.getOrderID());
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView id, status, date;
        VH(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.text_order_id);
            status = itemView.findViewById(R.id.text_order_status);
            date = itemView.findViewById(R.id.text_order_date);
        }
    }
}
