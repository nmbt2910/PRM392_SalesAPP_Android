package com.prm392.salesapp.storelocations;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.prm392.salesapp.R;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

public class CustomInfoWindow extends InfoWindow {

    // Interface để gửi sự kiện click về MapActivity
    public interface DirectionsListener {
        void onDirectionsClicked(StoreLocation location);
    }

    private DirectionsListener listener;

    public CustomInfoWindow(int layoutResId, MapView mapView, DirectionsListener listener) {
        super(layoutResId, mapView);
        this.listener = listener;
    }

    @Override
    public void onOpen(Object arg0) {
        // Lấy Marker và StoreLocation đã lưu
        org.osmdroid.views.overlay.Marker marker = (org.osmdroid.views.overlay.Marker) arg0;
        StoreLocation location = (StoreLocation) marker.getRelatedObject();

        // Lấy các View từ layout
        TextView title = mView.findViewById(R.id.infowindow_title);
        TextView snippet = mView.findViewById(R.id.infowindow_snippet);
        Button directionsButton = mView.findViewById(R.id.infowindow_directions_button);
        ImageView imageView = mView.findViewById(R.id.infowindow_image);

        // Đặt nội dung
        if (location != null) {
            title.setText("Store #" + location.getLocationID());
            snippet.setText(location.getAddress());

            int[] storeImages = {
                    R.drawable.store_1,
                    R.drawable.store_2,
                    R.drawable.store_3
            };
            // Chọn ảnh dựa trên ID
            int imageIndex = location.getLocationID() % storeImages.length;
            int imageResId = storeImages[imageIndex];

            // Đặt ảnh (Vì là ảnh local nên không cần Glide)
            imageView.setImageResource(imageResId);
        } else {
            title.setText(marker.getTitle()); // Dự phòng
            snippet.setText(marker.getSnippet());
            imageView.setImageResource(R.drawable.ic_map);
        }

        // Đặt sự kiện click cho nút "Directions"
        directionsButton.setOnClickListener(v -> {
            if (listener != null && location != null) {
                listener.onDirectionsClicked(location);
            }
            close(); // Đóng InfoWindow sau khi nhấn
        });
    }

    @Override
    public void onClose() {
        // Dọn dẹp nếu cần
    }
}