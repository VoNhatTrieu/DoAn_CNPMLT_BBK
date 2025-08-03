package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapPickerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    private LatLng selectedLocation;
    private Marker selectedMarker;
    private TextView tvSelectedAddress;
    private Button btnConfirm, btnCancel;
    private FloatingActionButton btnCurrentLocation;
    private String selectedAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_picker);

        // Khởi tạo views
        tvSelectedAddress = findViewById(R.id.tv_selected_address);
        btnConfirm = findViewById(R.id.btn_confirm_location);
        btnCancel = findViewById(R.id.btn_cancel);
        btnCurrentLocation = findViewById(R.id.btn_current_location);

        // Khởi tạo location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Khởi tạo map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Set up button listeners
        btnConfirm.setOnClickListener(v -> confirmLocation());
        btnCancel.setOnClickListener(v -> finish());
        btnCurrentLocation.setOnClickListener(v -> getCurrentLocation());

        // Disable confirm button initially
        btnConfirm.setEnabled(false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Set default location (Ho Chi Minh City)
        LatLng hoChiMinh = new LatLng(10.8231, 106.6297);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hoChiMinh, 15));

        // Set map click listener
        mMap.setOnMapClickListener(latLng -> {
            // Remove previous marker
            if (selectedMarker != null) {
                selectedMarker.remove();
            }

            // Add new marker
            selectedMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Vị trí đã chọn"));

            selectedLocation = latLng;

            // Get address from coordinates
            getAddressFromLocation(latLng);

            // Enable confirm button
            btnConfirm.setEnabled(true);
        });

        // Check and request location permission
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            enableMyLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "Cần quyền truy cập vị trí để sử dụng tính năng này",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Cần quyền truy cập vị trí", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng currentLocation = new LatLng(location.getLatitude(),
                                    location.getLongitude());

                            // Move camera to current location
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17));

                            // Add marker at current location
                            if (selectedMarker != null) {
                                selectedMarker.remove();
                            }

                            selectedMarker = mMap.addMarker(new MarkerOptions()
                                    .position(currentLocation)
                                    .title("Vị trí hiện tại"));

                            selectedLocation = currentLocation;
                            getAddressFromLocation(currentLocation);
                            btnConfirm.setEnabled(true);
                        } else {
                            Toast.makeText(MapPickerActivity.this,
                                    "Không thể lấy vị trí hiện tại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getAddressFromLocation(LatLng latLng) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(
                    latLng.latitude, latLng.longitude, 1);

            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                selectedAddress = address.getAddressLine(0);

                if (selectedAddress != null) {
                    tvSelectedAddress.setText("📍 " + selectedAddress);
                    tvSelectedAddress.setVisibility(View.VISIBLE);
                } else {
                    selectedAddress = "Lat: " + latLng.latitude + ", Lng: " + latLng.longitude;
                    tvSelectedAddress.setText("📍 " + selectedAddress);
                    tvSelectedAddress.setVisibility(View.VISIBLE);
                }
            } else {
                selectedAddress = "Lat: " + latLng.latitude + ", Lng: " + latLng.longitude;
                tvSelectedAddress.setText("📍 " + selectedAddress);
                tvSelectedAddress.setVisibility(View.VISIBLE);
            }
        } catch (IOException e) {
            Log.e("Geocoder", "Error getting address", e);
            selectedAddress = "Lat: " + latLng.latitude + ", Lng: " + latLng.longitude;
            tvSelectedAddress.setText("📍 " + selectedAddress);
            tvSelectedAddress.setVisibility(View.VISIBLE);
        }
    }

    private void confirmLocation() {
        if (selectedLocation != null) {
            Intent result = new Intent();
            result.putExtra("selected_address", selectedAddress);
            result.putExtra("latitude", selectedLocation.latitude);
            result.putExtra("longitude", selectedLocation.longitude);
            setResult(RESULT_OK, result);
            finish();
        } else {
            Toast.makeText(this, "Vui lòng chọn một vị trí trên bản đồ", Toast.LENGTH_SHORT).show();
        }
    }
}