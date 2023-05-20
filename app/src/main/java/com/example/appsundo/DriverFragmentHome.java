package com.example.appsundo;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

    NotificationManagerCompat notificationManagerCompat;
    Notification notification;
    private MaterialButton gpsBtn;

    private Double studentLat;
    private Double studentLong;
    private LatLng latLngDriver, latLngStudent;
    private String studentUid;
    private String driverUid;

    private Boolean isGpsON = false;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mLocationProvider;
    private Polyline mPolyline;
    private Marker markerDriver, markerStudent;
    private ValueAnimator animator;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Handler handler;

    private String accountCode;

    private static final String CHANNEL_ID = "channel_id01";
    private static final int NOTIFICATION_ID = 1;

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_home, container, false);

        driverUid = String.valueOf(FirebaseAuth.getInstance().getUid());

        fetchStudentID(driverUid);

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

                //createnotif();

                if (isGpsON) {
                    gpsBtnOn(false);

                    status = "OFFLINE";
                    timeString = "TIME_OUT";

                } else {
                    gpsBtnOn(true);

                    status = "ONLINE";
                    timeString = "TIME_IN";

                    onResume();
                }


                dbRef.child("ONLINE_DRIVER").child(driverUid).child("serviceStatus").setValue("In Transit");
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

                dbRef.child("ONLINE_DRIVER").child(driverUid).child(dateFormat.format(currentTime)).child(timeString).setValue(dateTimeFormat.format(currentTime));
                DatabaseReference historyRef = dbRef.child("HISTORY").child(dateFormat.format(currentTime)).child(driverUid);
                //TIME IN AND TIME OUT
                historyRef.child(dateFormat.format(currentTime)).child(timeString).child(dateTimeFormat.format(currentTime));
                historyRef.child(dateFormat.format(currentTime)).child(timeString).child(dateTimeFormat.format(currentTime));


                DatabaseReference toSchoolRef = historyRef.child(studentUid).child("TO_SCHOOL");
                DatabaseReference toHomeRef = historyRef.child(studentUid).child("TO_HOME");

                //STUDENT / TO SCHOOL / PICKUP
                toSchoolRef.child("PICKUP").child("pickupTime").setValue(dateTimeFormat.format(currentTime));
//                toSchoolRef.child("PICKUP").child("longitude").setValue(latLngDriver.longitude);
//                toSchoolRef.child("PICKUP").child("latitude").setValue(latLngDriver.latitude);

                //STUDENT / TO SCHOOL / DROP OFF
                toSchoolRef.child("DROP_OFF").child("dropOffTime").setValue(dateTimeFormat.format(currentTime));
 //               toSchoolRef.child("DROP_OFF").child("longitude").setValue(latLngDriver.longitude);
//                toSchoolRef.child("DROP_OFF").child("latitude").setValue(latLngDriver.latitude);

                //STUDENT / TO SCHOOL / DROP OFF
                toHomeRef.child("PICKUP").child("pickupTime").setValue(dateTimeFormat.format(currentTime));
//                toHomeRef.child("PICKUP").child("longitude").setValue(latLngDriver.longitude);
//                toHomeRef.child("PICKUP").child("latitude").setValue(latLngDriver.latitude);

                toHomeRef.child("DROP_OFF").child("dropOffTime").setValue(dateTimeFormat.format(currentTime));
//                toHomeRef.child("DROP_OFF").child("longitude").setValue(latLngDriver.longitude);
//                toHomeRef.child("DROP_OFF").child("latitude").setValue(latLngDriver.latitude);

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

    private void uploadLocationToFirebase(LatLng latlng) {
        driverUid = String.valueOf(FirebaseAuth.getInstance().getUid());

        dbRef.child("ONLINE_DRIVER").child(driverUid).child("CURRENT_LOCATION").child("latitude").setValue(latlng.latitude);
        dbRef.child("ONLINE_DRIVER").child(driverUid).child("CURRENT_LOCATION").child("longitude").setValue(latlng.longitude);
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

            if (markerDriver != null) {
                markerDriver.remove();
                markerDriver = null;
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

                    fetchStudentLocation(studentUid);

                    Location location = locationResult.getLastLocation();
                    latLngDriver = new LatLng(location.getLatitude(), location.getLongitude());

                    if (studentLat != null && studentLong != null) {
                        latLngStudent = new LatLng(studentLat, studentLong);
                    }

                    MarkerOptions markerOptionsDriver;
                    MarkerOptions markerOptionsStudent;

                    if (isGpsON) {

                        if (markerDriver == null) {
                            markerOptionsDriver = new MarkerOptions()
                                    .position(latLngDriver)
                                    .title("Current Location");
                            markerDriver = mGoogleMap.addMarker(markerOptionsDriver);
                        }

                        if (studentLat != null && studentLong != null) {

                            if (markerStudent != null) {
                                animateMarkerDriver(markerDriver, latLngDriver, markerStudent, latLngStudent);
                            } else {
                                markerOptionsStudent = new MarkerOptions()
                                        .position(latLngStudent)
                                        .icon(setIcon(getActivity(), R.drawable.bus_icon_map))
                                        .title("Student Location");

                                markerStudent = mGoogleMap.addMarker(markerOptionsStudent);
                            }

                        }


                    }

                    uploadLocationToFirebase(latLngDriver);
                }
            };

        }

    }

    public BitmapDescriptor setIcon(Activity context, int drawableID) {

        Drawable drawable = ActivityCompat.getDrawable(context, drawableID);

        drawable.setBounds(0, 0, 96, 96);

        Bitmap bitmap = Bitmap.createBitmap(96, 96, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        drawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }

    private void animateMarkerDriver(Marker marker1, LatLng newPosition1, Marker marker2, LatLng newPosition2) {
        if (animator != null) {
            animator.cancel();
        }
        animator = ValueAnimator.ofObject(new DriverFragmentHome.LatLngEvaluator(), marker1.getPosition(), newPosition1);
        animator.setDuration(500); // Animation duration in milliseconds
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                LatLng position = (LatLng) animation.getAnimatedValue();
                marker1.setPosition(position);
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Start the next animation here
                animateMarkerStudent(marker2, newPosition2);
            }
        });

        animator.start();


    }

    private void animateMarkerStudent(Marker marker, LatLng newPosition) {
        if (animator != null) {
            animator.cancel();
        }
        animator = ValueAnimator.ofObject(new DriverFragmentHome.LatLngEvaluator(), marker.getPosition(), newPosition);
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

        if (isGpsON) {
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

    private void fetchStudentID(String uidDriver) {
        dbRef.child("USER_INFORMATION").child("STUDENT").orderByChild("DRIVER_ASSIGNED").equalTo(uidDriver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    return;
                }
                for (DataSnapshot children : snapshot.getChildren()){
                    studentUid = String.valueOf(children.child("uid").getValue());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void fetchStudentLocation(String studentID) {

        if (studentID == null) { return; }

        dbRef.child("ACTIVE_STUDENT").child(studentID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentLat = Double.parseDouble(String.valueOf(snapshot.child("latitude").getValue()));
                studentLong = Double.parseDouble(String.valueOf(snapshot.child("longitude").getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}