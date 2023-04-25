package com.example.appsundo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;

public class LocationService extends Service {
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    public IBinder onBind(Intent intent) {
        // We don't need to bind to this service, so just return null
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start location updates when the service is started
        startLocationUpdates();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Stop location updates when the service is destroyed
        stopLocationUpdates();
        super.onDestroy();
    }

    private void startLocationUpdates() {
        // Get a reference to the LocationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Create a LocationListener to handle location updates
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Handle new location updates here
            }

            // Implement other LocationListener methods as needed
        };

        // Register the LocationListener to receive updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    private void stopLocationUpdates() {
        // Unregister the LocationListener to stop receiving updates
        locationManager.removeUpdates(locationListener);
    }
}

