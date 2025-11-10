package com.prm392.salesapp.storelocations;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prm392.salesapp.R;
import com.prm392.salesapp.viewmodel.MapViewModel;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends AppCompatActivity implements
        StoreListAdapter.OnStoreClickListener,
        CustomInfoWindow.DirectionsListener,
        MapEventsReceiver {

    // ... (Tất cả các biến của bạn giữ nguyên) ...
    private MapViewModel viewModel;
    private MapView mapView;
    private ProgressBar progressBar;
    private MyLocationNewOverlay myLocationOverlay;
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private RecyclerView storeRecyclerView;
    private StoreListAdapter storeListAdapter;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private LinearLayout bottomSheetLayout;
    private HashMap<Integer, Marker> markerMap = new HashMap<>();
    private IMapController mapController;
    private List<StoreLocation> currentLocations;
    private CustomInfoWindow infoWindow;
    private MapEventsOverlay mapEventsOverlay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(R.layout.activity_map);

        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION
        });

        // Setup Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar_map);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Khởi tạo Views
        mapView = findViewById(R.id.map_view);
        progressBar = findViewById(R.id.progress_bar_map);

        // --- CÀI ĐẶT BOTTOM SHEET ---
        storeRecyclerView = findViewById(R.id.store_recycler_view);
        bottomSheetLayout = findViewById(R.id.bottom_sheet_layout);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        setupRecyclerView();
        // --- KẾT THÚC CÀI ĐẶT ---

        // Cấu hình MapView
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        mapController = mapView.getController();
        mapController.setZoom(15.0);
        GeoPoint startPoint = new GeoPoint(10.7769, 106.7009);
        mapController.setCenter(startPoint);

        // 1. Tạo MapEventsOverlay để lắng nghe tap
        mapEventsOverlay = new MapEventsOverlay(this);
        mapView.getOverlays().add(0, mapEventsOverlay);

        // 2. Tạo MyLocationNewOverlay
        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        myLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(myLocationOverlay); // <-- Thêm 1 LẦN DUY NHẤT

        // (Code FAB Zoom giữ nguyên)
        FloatingActionButton fabZoomIn = findViewById(R.id.fab_zoom_in);
        FloatingActionButton fabZoomOut = findViewById(R.id.fab_zoom_out);
        fabZoomIn.setOnClickListener(v -> {
            if (mapView.canZoomIn()) {
                mapController.zoomIn();
            }
        });
        fabZoomOut.setOnClickListener(v -> {
            if (mapView.canZoomOut()) {
                mapController.zoomOut();
            }
        });

        // --- THAY ĐỔI 1: XÓA DÒNG BỊ LẶP ---
        // XÓA: myLocationOverlay.enableMyLocation();
        // XÓA: mapView.getOverlays().add(myLocationOverlay); // <-- Dòng này bị lặp

        // Tạo InfoWindow MỘT LẦN
        infoWindow = new CustomInfoWindow(R.layout.layout_custom_infowindow, mapView, this);

        viewModel = new ViewModelProvider(this).get(MapViewModel.class);
        observeViewModel();

        progressBar.setVisibility(View.VISIBLE);
        viewModel.fetchLocations();
    }

    private void setupRecyclerView() {
        // ... (Giữ nguyên)
        storeListAdapter = new StoreListAdapter(this, new ArrayList<>(), this);
        storeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        storeRecyclerView.setAdapter(storeListAdapter);
    }

    private void observeViewModel() {
        // ... (Giữ nguyên)
        viewModel.getLocations().observe(this, locations -> {
            progressBar.setVisibility(View.GONE);
            if (locations != null && !locations.isEmpty()) {
                this.currentLocations = locations;
                addMarkersToMap(locations);
                storeListAdapter.updateLocations(locations);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                Toast.makeText(this, "No store locations found.", Toast.LENGTH_SHORT).show();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        viewModel.getError().observe(this, error -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        });
    }

    private void addMarkersToMap(List<StoreLocation> locations) {
        // ... (Giữ nguyên logic clear overlay)
        Drawable storeIcon = ContextCompat.getDrawable(this, R.drawable.ic_map);
        while (mapView.getOverlays().size() > 2) {
            mapView.getOverlays().remove(mapView.getOverlays().size() - 1);
        }
        markerMap.clear();

        for (int i = 0; i < locations.size(); i++) {
            // ... (Giữ nguyên toàn bộ logic for loop)
            StoreLocation location = locations.get(i);
            final int listPosition = i;
            GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
            Marker storeMarker = new Marker(mapView);
            storeMarker.setPosition(point);
            storeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            storeMarker.setIcon(storeIcon);
            storeMarker.setRelatedObject(location);
            storeMarker.setInfoWindow(infoWindow);
            storeMarker.setOnMarkerClickListener((marker, mapView) -> {
                storeRecyclerView.smoothScrollToPosition(listPosition);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                marker.showInfoWindow();
                return true;
            });
            mapView.getOverlays().add(storeMarker);
            markerMap.put(location.getLocationID(), storeMarker);
        }
        mapView.invalidate();
    }


    // --- THAY ĐỔI 2: SỬA TÊN CÁC HÀM OVERRIDE ---

    @Override
    public boolean singleTapConfirmedHelper(GeoPoint p) { // <-- Sửa tên
        // Khi người dùng tap vào vùng trống trên bản đồ
        // --- THAY ĐỔI 3: SỬA TÊN HÀM (BỎ "On") ---
        InfoWindow.closeAllInfoWindowsOn(mapView); // <-- Sửa tên
        return true;
    }

    @Override
    public boolean longPressHelper(GeoPoint p) { // <-- Sửa tên
        // Chúng ta không làm gì khi nhấn giữ
        return false;
    }
    // --- KẾT THÚC THAY ĐỔI 2 & 3 ---

    // Hàm này được gọi khi nhấn vào item trong RecyclerView
    @Override
    public void onStoreClick(StoreLocation location) {
        // ... (Giữ nguyên)
        Marker marker = markerMap.get(location.getLocationID());
        if (marker != null) {
            mapController.animateTo(marker.getPosition());
            marker.showInfoWindow();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    // 2. Từ StoreListAdapter: Khi nhấn nút "Directions"
    @Override
    public void onDirectionsClick(StoreLocation location) {
        // ... (Giữ nguyên)
        openGoogleMaps(location);
    }

    // 3. Từ CustomInfoWindow: Khi nhấn nút "Directions"
    @Override
    public void onDirectionsClicked(StoreLocation location) {
        // ... (Giữ nguyên)
        openGoogleMaps(location);
    }


    private void openGoogleMaps(StoreLocation location) {
        // ... (Giữ nguyên)
        Uri gmmIntentUri = Uri.parse("geo:" + location.getLatitude() + "," + location.getLongitude() + "?q=" + Uri.encode(location.getAddress()));
        Intent generalMapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        Intent specificMapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        specificMapIntent.setPackage("com.google.android.apps.maps");

        try {
            startActivity(specificMapIntent);
        } catch (ActivityNotFoundException e1) {
            try {
                startActivity(generalMapIntent);
            } catch (ActivityNotFoundException e2) {
                Toast.makeText(this, "No map application found.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ----- Các hàm quản lý vòng đời và quyền (GIỮ NGUYÊN) -----
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // ... (Giữ nguyên)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            boolean locationAccepted = false;
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    locationAccepted = true;
                    break;
                }
            }
            if (locationAccepted) {
                myLocationOverlay.enableMyLocation();
            } else {
                Toast.makeText(this, "Location permission is required to show your position.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        // ... (Giữ nguyên)
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}