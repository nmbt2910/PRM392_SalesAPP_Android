package com.prm392.salesapp.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm392.salesapp.R;

import java.util.ArrayList;
import java.util.List;

public class ThreadAdapter extends RecyclerView.Adapter<ThreadAdapter.ThreadViewHolder> implements Filterable {

    private List<Thread> threads;
    private final List<Thread> threadsFull;
    private final OnThreadClickListener onThreadClickListener;

    public ThreadAdapter(List<Thread> threads, OnThreadClickListener onThreadClickListener) {
        this.threads = threads;
        this.threadsFull = new ArrayList<>(threads);
        this.onThreadClickListener = onThreadClickListener;
    }

    public void updateThreads(List<Thread> newThreads) {
        threads.clear();
        threads.addAll(newThreads);
        threadsFull.clear();
        threadsFull.addAll(newThreads);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ThreadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thread, parent, false);
        return new ThreadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThreadViewHolder holder, int position) {
        Thread thread = threads.get(position);
        holder.bind(thread, onThreadClickListener);
    }

    @Override
    public int getItemCount() {
        return threads.size();
    }

    @Override
    public Filter getFilter() {
        return threadFilter;
    }

    private final Filter threadFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Thread> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(threadsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Thread thread : threadsFull) {
                    if (thread.getCustomerName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(thread);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            threads.clear();
            threads.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    static class ThreadViewHolder extends RecyclerView.ViewHolder {

        private final TextView customerNameTextView;
        private final TextView lastMessageTextView;
        private final TextView lastMessageAtTextView;
        private final ImageView unreadIndicatorImageView;

        public ThreadViewHolder(@NonNull View itemView) {
            super(itemView);
            customerNameTextView = itemView.findViewById(R.id.customer_name_text_view);
            lastMessageTextView = itemView.findViewById(R.id.last_message_text_view);
            lastMessageAtTextView = itemView.findViewById(R.id.last_message_at_text_view);
            unreadIndicatorImageView = itemView.findViewById(R.id.unread_indicator_image_view);
        }

        public void bind(final Thread thread, final OnThreadClickListener onThreadClickListener) {
            customerNameTextView.setText(thread.getCustomerName());
            lastMessageTextView.setText(thread.getLastMessage());
            lastMessageAtTextView.setText(thread.getLastMessageAt());
            unreadIndicatorImageView.setVisibility(thread.isUnread() ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(v -> onThreadClickListener.onThreadClick(thread));
        }
    }

    public interface OnThreadClickListener {
        void onThreadClick(Thread thread);
    }
}
