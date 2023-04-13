package com.example.appsundo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverFragmentHome extends Fragment implements OnMapReadyCallback {

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mLocationProvider;
    private Polyline mPolyline;
    private Marker marker;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Handler handler;

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_home, container, false);

        mMapView = view.findViewById(R.id.mapViewDriver);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        mLocationProvider = LocationServices.getFusedLocationProviderClient(getActivity());

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mMapView.onResume();

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                moveCameraToCurrentLocation();
                handler.postDelayed(this, 60000);
            }
        }, 60000);

        locationRequest = new LocationRequest
                .Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000)
                .setMaxUpdateDelayMillis(10000)
                .build();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                if (marker != null) {
                    marker.remove();
                }

                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title("Current Location");

                marker = mGoogleMap.addMarker(markerOptions);

                //not yet working
                mPolyline.getPoints().add(latLng);

                uploadLocationToFirebase(latLng);
            }
        };

    }

    @Override
    public void onPause() {
        super.onPause();

        mMapView.onPause();

        handler.removeCallbacksAndMessages(null);

        mLocationProvider.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        // Request location updates
        requestLocationUpdates();

        // Create a new Polyline object and add it to the map
        mPolyline = mGoogleMap.addPolyline(new PolylineOptions()
                .width(10)
                .color(Color.RED));

        // Move the camera to the initial position
        moveCameraToCurrentLocation();
    }

    //CUSTOM FUNCTIONS
    private void requestLocationUpdates() {

        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Request location updates
            mLocationProvider.requestLocationUpdates(locationRequest, locationCallback, null);

        }
    }

    private void uploadLocationToFirebase(LatLng latlng) {
        String uid = String.valueOf(FirebaseAuth.getInstance().getUid());

        dbRef.child("ACTIVE_DRIVER").child(uid).setValue(latlng);
    }

    private void moveCameraToCurrentLocation() {
        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Get the last known location from the FusedLocationProviderClient
            mLocationProvider.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        // Animate the camera to the current location
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
                        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(17).tilt(30).build()));
                    }
                }
            });
        }
    }
}