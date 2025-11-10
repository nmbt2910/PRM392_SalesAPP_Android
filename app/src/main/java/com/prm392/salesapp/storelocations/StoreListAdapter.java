package com.prm392.salesapp.storelocations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.prm392.salesapp.R;

import java.util.List;

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.StoreViewHolder> {

    // Interface để xử lý sự kiện click
    public interface OnStoreClickListener {
        void onStoreClick(StoreLocation location); // Khi nhấn vào item
        void onDirectionsClick(StoreLocation location); // Khi nhấn nút chỉ đường
    }

    private final Context context;
    private List<StoreLocation> locations;
    private final OnStoreClickListener clickListener;

    public StoreListAdapter(Context context, List<StoreLocation> locations, OnStoreClickListener clickListener) {
        this.context = context;
        this.locations = locations;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_store, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        StoreLocation location = locations.get(position);
        holder.bind(location, clickListener);
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public void updateLocations(List<StoreLocation> newLocations) {
        this.locations = newLocations;
        notifyDataSetChanged();
    }

    // Thêm hàm này để MapActivity tìm vị trí
    public int getPositionForLocation(StoreLocation location) {
        if (locations == null) return -1;
        return locations.indexOf(location);
    }

    class StoreViewHolder extends RecyclerView.ViewHolder {
        ImageView storeImage;
        TextView storeTitle;
        TextView storeAddress;
        MaterialButton directionsButton;
        StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            storeImage = itemView.findViewById(R.id.store_image);
            storeTitle = itemView.findViewById(R.id.store_title);
            storeAddress = itemView.findViewById(R.id.store_address);
            directionsButton = itemView.findViewById(R.id.button_directions_list); // <-- THÊM
        }

        void bind(final StoreLocation location, final OnStoreClickListener listener) {
            storeTitle.setText("Store #" + location.getLocationID());
            storeAddress.setText(location.getAddress());


            int[] storeImages = {
                    R.drawable.store_1,
                    R.drawable.store_2,
                    R.drawable.store_3,
            };

            int imageIndex = location.getLocationID() % storeImages.length;
            int imageResId = storeImages[imageIndex];


            Glide.with(context)
                    .load(imageResId) // <-- Tải ảnh từ thư mục drawable
                    .centerCrop()
                    .error(R.drawable.ic_map) // Ảnh dự phòng nếu có lỗi
                    .into(storeImage);


            // 1. Click vào toàn bộ item
            itemView.setOnClickListener(v -> listener.onStoreClick(location));

            // 2. Click vào nút "Directions"
            directionsButton.setOnClickListener(v -> listener.onDirectionsClick(location));

        }
    }
}