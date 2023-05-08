package com.example.appsundo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentFragmentHome extends Fragment {

    User student;
    String studentCode;
    String studentUid;
    String status;
    String driverUid;
    String driverCode;

    ImageView imageView1, imageView2, imageView3;

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_home, container, false);

        imageView1 = view.findViewById(R.id.imageView1);
        imageView2 = view.findViewById(R.id.imageView2);
        imageView3 = view.findViewById(R.id.imageView3);

        studentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dbRef.child("USER_INFORMATION").child("STUDENT").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    student = dataSnapshot.getValue(User.class);

                    if (student.getUid().equals(studentUid)) {
                        studentCode = String.valueOf(dataSnapshot.child("accountCode").getValue());
                        student = null;

                        dbRef.child("USERS").child("STUDENT").child(studentUid).child("ASSIGNED_DRIVER").addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                //driverUid = String.valueOf(snapshot.child("UID").getValue());

                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                    driverCode = String.valueOf(dataSnapshot.getKey());
                                    driverUid = String.valueOf(dataSnapshot.child("UID").getValue());

                                    dbRef.child("USERS").child("DRIVER").child(driverUid).child("ASSIGNED_STUDENT").child(studentCode).child("status").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            status = String.valueOf(snapshot.getValue());

                                            switch (status) {
                                                case "WAITING":
                                                    imageView1.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
                                                    imageView2.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
                                                    imageView3.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
                                                    break;

                                                case "ONBOARD":
                                                    imageView1.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
                                                    imageView2.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
                                                    imageView3.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
                                                    break;

                                                case "ARRIVED":
                                                    imageView1.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
                                                    imageView2.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
                                                    imageView3.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
                                                    break;

                                                default:
                                                    imageView1.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
                                                    imageView2.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
                                                    imageView3.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
                                                    break;

                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                }
                                
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

        return view;
    }


}