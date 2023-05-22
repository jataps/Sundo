package com.example.appsundo;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class DriverFragmentService2 extends Fragment implements RecyclerViewInterface {

    ValueEventListener valueEventListener;
    RecyclerView statusStudentList;
    DatabaseReference mRef;
    CustomStudentAdapter adapter;

    MaterialButton btnChangeYes, btnChangeNo;

    ArrayList<User> list;
    User student;
    DatabaseReference dbRef;
    String status;

    String whereTo;
    String whatTo;
    String whatVar;

    Location location;
    LatLng latLngDriver;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient mLocationProvider;
    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver_onboard, container, false);

        statusStudentList = view.findViewById(R.id.statusStudentList);

        statusStudentList.setHasFixedSize(false);
        statusStudentList.setLayoutManager(new LinearLayoutManager(getActivity()));

        list = new ArrayList<>();
        adapter = new CustomStudentAdapter(getActivity(), list, this);
        statusStudentList.setAdapter(adapter);

        String uidDriver = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dbRef = FirebaseDatabase.getInstance().getReference();

        mRef = dbRef.child("USERS").child("DRIVER").child(uidDriver).child("ASSIGNED_STUDENT");

        mLocationProvider = LocationServices.getFusedLocationProviderClient(getActivity());

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    student = dataSnapshot.getValue(User.class);
                    student.setReferenceID(dataSnapshot.getKey());

                    list.add(student);
                    Collections.sort(list);

                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        };

        mRef.addValueEventListener(valueEventListener);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
                latLngDriver = new LatLng(location.getLatitude(), location.getLongitude());

            }
        };

        }

    private void requestLocationUpdates() {

        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Request location updates
            mLocationProvider.requestLocationUpdates(locationRequest, locationCallback, null);

        }
    }

    private void uploadLocationToFirebase(LatLng latlng, String whereTo, String whatTo,
                                          String whatVar) {
        String driverUid = String.valueOf(FirebaseAuth.getInstance().getUid());

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");

        DatabaseReference historyRef =
                dbRef.child("HISTORY").child(dateFormat.format(currentTime)).child(driverUid).child(student.getReferenceID());

        historyRef.child(whereTo).child(whatTo).child(whatVar).setValue(dateTimeFormat.format(currentTime));
        historyRef.child(whereTo).child(whatTo).child("longitude").setValue(latlng.longitude);
        historyRef.child(whereTo).child(whatTo).child("latitude").setValue(latlng.latitude);

    }

    private void showDialog() {

        final Dialog dialog = new Dialog(getActivity());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        btnChangeYes = dialog.findViewById(R.id.btnChangeYes);
        btnChangeNo = dialog.findViewById(R.id.btnChangeNo);

        btnChangeYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "YES", Toast.LENGTH_SHORT).show();
                serviceStatusChange();

                dialog.cancel();
            }
        });

        btnChangeNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void serviceStatusChange() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference driverRef = dbRef.child("USERS").child("DRIVER").child(uid).child("ASSIGNED_STUDENT").child(student.getReferenceID());

        status = student.getStatus();

        switch (status) {

            case "TO SCHOOL | WAITING":
                uploadLocationToFirebase(latLngDriver, "TO_SCHOOL", "PICKUP", "pickupTime");
                status = "TO SCHOOL | ONBOARD";

                break;

            case "TO SCHOOL | ONBOARD":
                status = "TO SCHOOL | ARRIVED";
                uploadLocationToFirebase(latLngDriver, "TO_SCHOOL", "DROP_OFF", "dropOffTime");
                break;

            case "TO SCHOOL | ARRIVED":
                status = "TO HOME | WAITING";
                break;

            case "TO HOME | WAITING":

                uploadLocationToFirebase(latLngDriver, "TO_HOME", "PICKUP", "pickupTime");
                status = "TO HOME | ONBOARD";

                break;

            case "TO HOME | ONBOARD":
                uploadLocationToFirebase(latLngDriver, "TO_HOME", "DROP_OFF", "dropOffTime");
                status = "TO HOME | ARRIVED";
                break;

            case "TO HOME | ARRIVED":
                status = "TO SCHOOL | WAITING";
                break;

            default:
                break;
        }


        driverRef.child("status").setValue(status);

    }


    @Override
    public void onPause() {
        super.onPause();

        if (valueEventListener != null) {
            mRef.removeEventListener(valueEventListener);
            handler.removeCallbacksAndMessages(null);
            mLocationProvider.removeLocationUpdates(locationCallback);
        }

    }

    @Override
    public void onItemClick(int position) {

        showDialog();

    }
}