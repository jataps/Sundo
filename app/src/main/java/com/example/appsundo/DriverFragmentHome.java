package com.example.appsundo;

import android.Manifest;
import android.animation.Animator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DriverFragmentHome extends Fragment implements OnMapReadyCallback {

    private MaterialButton gpsBtn;

    private Boolean isGpsON = false;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mLocationProvider;
    private Polyline mPolyline;
    private Marker marker;
    private ValueAnimator animator;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Handler handler;

    private String uid;
    private String accountCode;

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_home, container, false);

        uid = String.valueOf(FirebaseAuth.getInstance().getUid());
        fetchAccountCode();

        gpsBtn = view.findViewById(R.id.gpsBtn);

        mMapView = view.findViewById(R.id.mapViewDriver);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        mLocationProvider = LocationServices.getFusedLocationProviderClient(getActivity());



        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String status;
                String timeString;

                if(isGpsON) {
                    gpsBtnOn(false);

                    status = "OFFLINE";
                    timeString = "TIME_OUT";

                } else {
                    gpsBtnOn(true);

                    status = "ONLINE";
                    timeString = "TIME_IN";

                    onResume();
                }

                dbRef.child("ONLINE_DRIVER").child(accountCode).child("serviceStatus").setValue("In Transit");

                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

                dbRef.child("ONLINE_DRIVER").child(accountCode).child(dateFormat.format(currentTime)).child(timeString).setValue(dateTimeFormat.format(currentTime));

                DatabaseReference historyRef = dbRef.child("HISTORY").child(dateFormat.format(currentTime)).child(accountCode);
                //TIME IN AND TIME OUT
                historyRef.child(dateFormat.format(currentTime)).child(timeString).child(dateTimeFormat.format(currentTime));
                historyRef.child(dateFormat.format(currentTime)).child(timeString).child(dateTimeFormat.format(currentTime));

                String studentUid = "studentC";
                DatabaseReference toSchoolRef = historyRef.child("STUDENTS ONBOARD").child(studentUid).child("TO_SCHOOL");
                //STUDENT / TO SCHOOL / PICKUP
                toSchoolRef.child("PICKUP").child("pickupTime").setValue(dateTimeFormat.format(currentTime));
                toSchoolRef.child("PICKUP").child("longitude").setValue("insert longitude here");
                toSchoolRef.child("PICKUP").child("latitude").setValue("insert latitude here");

                //STUDENT / TO SCHOOL / DROP OFF
                toSchoolRef.child("DROP_OFF").child("dropOffTime").setValue(dateTimeFormat.format(currentTime));
                toSchoolRef.child("DROP_OFF").child("longitude").setValue("insert longitude here");
                toSchoolRef.child("DROP_OFF").child("latitude").setValue("insert latitude here");

                //STUDENT / TO SCHOOL / DROP OFF
                DatabaseReference toHomeRef = historyRef.child("STUDENTS ONBOARD").child(studentUid).child("TO_HOME");
                toHomeRef.child("PICKUP").child("pickupTime").setValue(dateTimeFormat.format(currentTime));
                toHomeRef.child("PICKUP").child("longitude").setValue("insert longitude here");
                toHomeRef.child("PICKUP").child("latitude").setValue("insert latitude here");

                toHomeRef.child("DROP_OFF").child("dropOffTime").setValue(dateTimeFormat.format(currentTime));
                toHomeRef.child("DROP_OFF").child("longitude").setValue("insert longitude here");
                toHomeRef.child("DROP_OFF").child("latitude").setValue("insert latitude here");

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    //CUSTOM FUNCTIONS
    private void requestLocationUpdates() {

        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Request location updates
            mLocationProvider.requestLocationUpdates(locationRequest, locationCallback, null);

        }
    }

    private void fetchAccountCode() {
        DatabaseReference infoIdRef = dbRef.child("USERS").child("DRIVER").child(uid).child("INFO_ID");
        infoIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String infoID = String.valueOf(snapshot.getValue());

                DatabaseReference acctCodeRef = dbRef.child("USER_INFORMATION").child("DRIVER").child(infoID).child("accountCode");

                acctCodeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        accountCode = String.valueOf(snapshot.getValue());
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void uploadLocationToFirebase(LatLng latlng) {
        uid = String.valueOf(FirebaseAuth.getInstance().getUid());

        dbRef.child("ONLINE_DRIVER").child(accountCode).child("CURRENT_LOCATION").child("latitude").setValue(latlng.latitude);
        dbRef.child("ONLINE_DRIVER").child(accountCode).child("CURRENT_LOCATION").child("longitude").setValue(latlng.longitude);
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

    private void gpsBtnOn(Boolean isON) {

        if (isON) {
            gpsBtn.setText("GPS ON");
            gpsBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));

        } else {
            handler.removeCallbacksAndMessages(null);
            mLocationProvider.removeLocationUpdates(locationCallback);

            if (marker!=null) {
                marker.remove();
                marker = null;
            }

            gpsBtn.setText("GPS OFF");
            gpsBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
        }

        isGpsON = isON;
    }

    @Override
    public void onResume() {
        super.onResume();

        mMapView.onResume();

        if (isGpsON) {

            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    moveCameraToCurrentLocation();
                    requestLocationUpdates();
                    handler.postDelayed(this, 60000);
                }
            }, 500);

            locationRequest = new LocationRequest
                    .Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                    .setWaitForAccurateLocation(false)
                    .setMinUpdateIntervalMillis(1000)
                    .setMaxUpdateDelayMillis(1200)
                    .build();
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);

                    Location location = locationResult.getLastLocation();
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

                    if (isGpsON) {
                        if (marker != null) {
                            animateMarkerToNewPosition(marker, latLng);
                        } else {
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(latLng)
                                    .title("Current Location");
                            marker = mGoogleMap.addMarker(markerOptions);
                        }
                    }

                    uploadLocationToFirebase(latLng);
                }
            };

        }

    }

    private void animateMarkerToNewPosition(Marker marker, LatLng newPosition) {
        if (animator != null) {
            animator.cancel();
        }
        animator = ValueAnimator.ofObject(new LatLngEvaluator(), marker.getPosition(), newPosition);
        animator.setDuration(500); // Animation duration in milliseconds
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                LatLng position = (LatLng) animation.getAnimatedValue();
                marker.setPosition(position);
            }
        });
        animator.start();
    }

    public class LatLngEvaluator implements TypeEvaluator<LatLng> {
        @Override
        public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
            double lat = (endValue.latitude - startValue.latitude) * fraction + startValue.latitude;
            double lng = (endValue.longitude - startValue.longitude) * fraction + startValue.longitude;
            return new LatLng(lat, lng);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        mMapView.onPause();

        if (isGpsON){
            handler.removeCallbacksAndMessages(null);
            mLocationProvider.removeLocationUpdates(locationCallback);
        }


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
    }

}