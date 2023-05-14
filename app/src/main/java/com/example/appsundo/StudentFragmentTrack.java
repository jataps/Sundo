package com.example.appsundo;

import android.Manifest;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StudentFragmentTrack#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StudentFragmentTrack extends Fragment implements OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StudentFragmentTrack() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StudentFragmentTrack.
     */
    // TODO: Rename and change types and number of parameters
    public static StudentFragmentTrack newInstance(String param1, String param2) {
        StudentFragmentTrack fragment = new StudentFragmentTrack();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private static final String TAG = "StudentFragmentTrack";

    private GeofencingClient geofencingClient;
    private float GEOFENCE_RADIUS = 200;
    private GeofenceHelper geofenceHelper;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";

    private ValueAnimator animator;

    private Circle radiusCircle, radiusClosestCircle;
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mLocationProvider;
    private Polyline mPolyline;
    private Marker markerDriver, markerStudent;

    private String status;
    private String studentCode;
    private String driverCode;
    private String studentUid;
    private String driverUid;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Handler handler;

    private User student;
    ;
    private User driver;

    private Double driverLat;
    private Double driverLong;
    private LatLng latLngDriver;

    ActivityResultLauncher<String[]> mPermissionResultLauncher;
    private boolean isLocationPermissionGranted = false;
    private boolean isInsideRadius= false;
    private boolean isInsideClosestRadius= false;
    final float USER_RADIUS = 200;
    final float USER_CLOSEST_RADIUS = 30;
    private int triggerCount = 0;
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_track, container, false);

        mPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {

                if (result.get(Manifest.permission.ACCESS_FINE_LOCATION) != null) {
                    isLocationPermissionGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                }

            }
        });
        requestPermission();

        //GOOGLE MAPS
        mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);

        studentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        fetchDriverID(studentUid);

        mLocationProvider = LocationServices.getFusedLocationProviderClient(getActivity());

        return view;
    }


    @Override
    public void onPause() {
        super.onPause();

        mMapView.onPause();

        handler.removeCallbacksAndMessages(null);
        mLocationProvider.removeLocationUpdates(locationCallback);

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

                //this should be continuously updating
                fetchDriverLocation(driverUid);

                MarkerOptions markerOptionsDriver;

                Location location = locationResult.getLastLocation();
                LatLng latLngStudent = new LatLng(location.getLatitude(), location.getLongitude());

                if (markerStudent != null) {
                    animateMarkerToNewPosition(markerStudent, latLngStudent);
                } else {
                    MarkerOptions markerOptionsStudent = new MarkerOptions()
                            .position(latLngStudent)
                            .title("Student Location");

                    markerStudent = mGoogleMap.addMarker(markerOptionsStudent);

                    if (triggerCount == 0){
                        radiusCircle = mGoogleMap.addCircle(new CircleOptions()
                                .center(latLngStudent)
                                .radius(USER_RADIUS)
                                .strokeWidth(2)
                                .strokeColor(Color.BLUE)
                                .fillColor(Color.argb( 70,174,198,207)));

                        radiusClosestCircle = mGoogleMap.addCircle(new CircleOptions()
                                .center(latLngStudent)
                                .radius(USER_CLOSEST_RADIUS)
                                .strokeWidth(2)
                                .strokeColor(Color.RED)
                                .fillColor(Color.argb( 70,255, 0, 0)));

                        triggerCount++;
                    }

                }

                if (driverLat != null || driverLong != null) {
                    latLngDriver = new LatLng(driverLat, driverLong);

                    if (markerDriver != null) {
                        //markerDriver.remove();
                        animateMarkerToNewPosition(markerDriver, latLngDriver);
                    } else {
                        markerOptionsDriver = new MarkerOptions()
                                .position(latLngDriver)
                                .icon(setIcon(getActivity(), R.drawable.bus_icon_map))
                                .title("Driver Location");

                        markerDriver = mGoogleMap.addMarker(markerOptionsDriver);
                    }

                    if (!isInsideRadius || !isInsideClosestRadius) {
                        Location driverLocation = new Location("");
                        driverLocation.setLatitude(latLngDriver.latitude);
                        driverLocation.setLongitude(latLngDriver.longitude);

                        Location studentLocation = new Location("");
                        studentLocation.setLatitude(latLngStudent.latitude);
                        studentLocation.setLongitude(latLngStudent.longitude);

                        // Check if the target location is within the specified radius of the user's location
                        isLocationWithinRadius(studentLocation, driverLocation);
                    }

                }

                uploadLocationToFirebase(latLngStudent);
            }
        };

    }

    public void isLocationWithinRadius(Location userLocation, Location targetLocation) {
        // Calculate the distance between the user's location and the target location
        float distance = userLocation.distanceTo(targetLocation);

        // Check if the distance is within the specified radius
        if (distance <= USER_RADIUS && !isInsideRadius && triggerCount == 1) {
            createnotif("SERVICE IS ARRIVING", "Service is within your area. Please make the necessary preparation.");
            isInsideRadius = !isInsideRadius;
            radiusCircle.remove();
        }

        if (distance <= USER_CLOSEST_RADIUS && !isInsideClosestRadius && triggerCount == 2){
            createnotif("SERVICE HAS ARRIVED", "Time for school! Your service has arrived.");
            isInsideRadius = !isInsideRadius;
            if (radiusCircle != null) {
                radiusCircle.remove();
            }
            radiusClosestCircle.remove();
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

        // Request location updates
        requestLocationUpdates();

        // Create a new Polyline object and add it to the map
        mPolyline = mGoogleMap.addPolyline(new PolylineOptions()
                .width(10)
                .color(Color.RED));

        // Move the camera to the initial position
        moveCameraToCurrentLocation();
    }

    public BitmapDescriptor setIcon(Activity context, int drawableID) {


        Drawable drawable = ActivityCompat.getDrawable(context, drawableID);

        drawable.setBounds(0, 0, 96, 96);

        Bitmap bitmap = Bitmap.createBitmap(96, 96, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        drawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }

    //CUSTOM FUNCTIONS
    public void createnotif(String title, String content) {
        String id = "myCh";
        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = manager.getNotificationChannel(id);
            if (channel == null) {
                channel = new NotificationChannel(id, "Channel Title", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("[Channel description]");
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 1000, 200, 340});
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                manager.createNotificationChannel(channel);
            }
        }

        Intent notificationIntent = new Intent(getActivity(), DriverFragmentHome.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), id)
                .setSmallIcon(R.drawable.notif_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.bus))
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{100,1000,200,340})
                .setAutoCancel(false)
                .setTicker("Notification");
        builder.setContentIntent(contentIntent);
        NotificationManagerCompat m = NotificationManagerCompat.from(getActivity().getApplicationContext());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        m.notify(1, builder.build());

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

    private void addGeofence(LatLng latLng, float radius) {

        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailure: " + errorMessage);
                    }
                });

    }

    private void requestLocationUpdates() {

        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Request location updates
            mLocationProvider.requestLocationUpdates(locationRequest, locationCallback, null);

        }
    }

    private void uploadLocationToFirebase(LatLng latlng) {
        String uid = String.valueOf(FirebaseAuth.getInstance().getUid());

        dbRef.child("ACTIVE_STUDENT").child(uid).setValue(latlng);
    }

    private void fetchDriverID(String uidStudent) {
        dbRef.child("USER_INFORMATION").child("STUDENT").child(uidStudent).child("DRIVER_ASSIGNED").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(!snapshot.exists()) {
                    return;
                } else {
                    driverUid = snapshot.getValue(String.class);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void fetchDriverLocation(String driverID) {

        if (driverID == null) { return; }

        dbRef.child("ONLINE_DRIVER").child(driverID).child("CURRENT_LOCATION").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                driverLat = Double.parseDouble(String.valueOf(snapshot.child("latitude").getValue()));
                driverLong = Double.parseDouble(String.valueOf(snapshot.child("longitude").getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

    private void requestPermission() {
        isLocationPermissionGranted = ContextCompat.checkSelfPermission(
                getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        List<String> permissionRequest = new ArrayList<>();

        if (!isLocationPermissionGranted) {
            permissionRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!permissionRequest.isEmpty()) {
            mPermissionResultLauncher.launch(permissionRequest.toArray(new String[0]));
        }

    }

    public void LocationStatusCheck() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}
