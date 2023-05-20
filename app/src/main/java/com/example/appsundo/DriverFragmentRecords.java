package com.example.appsundo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class DriverFragmentRecords extends Fragment implements RecyclerViewInterface {

    RecyclerView recyclerStudentRecord;
    DatabaseReference mRef, driverRef;
    CustomStudentAdapter adapter;
    ArrayList<User> list;
    User student;

    String driverUID;
    String driverFName;
    String driverLName;
    String driverCNumber;

    ValueEventListener valueEventListener, valueEventlistenerDriver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_records, container, false);

        recyclerStudentRecord = view.findViewById(R.id.recyclerStudentRecord);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onItemClick(int position) {

        Intent intent = new Intent(getActivity(), DriverHistory.class);

        intent.putExtra("fragment_to_display","fragment_service");
        intent.putExtra("student_uid", list.get(position).getUid());
        intent.putExtra("student_last_name", list.get(position).getLastName());
        intent.putExtra("student_first_name", list.get(position).getFirstName());
        intent.putExtra("student_contact_number", list.get(position).getContactNumber());
        intent.putExtra("driver_first_name", driverFName);
        intent.putExtra("driver_last_name", driverLName);
        intent.putExtra("driver_contact_number", driverCNumber);

        startActivity(intent);

    }

    @Override
    public void onResume() {
        super.onResume();

        driverUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        mRef = dbRef.child("USERS").child("DRIVER").child(driverUID).child("ASSIGNED_STUDENT");
        driverRef = dbRef.child("USER_INFORMATION").child("DRIVER").child(driverUID);

        recyclerStudentRecord.setHasFixedSize(true);
        recyclerStudentRecord.setLayoutManager(new LinearLayoutManager(getActivity()));

        list = new ArrayList<>();
        adapter = new CustomStudentAdapter(getActivity(), list, this);
        recyclerStudentRecord.setAdapter(adapter);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    student = dataSnapshot.getValue(User.class);
                    list.add(student);

                    Collections.sort(list);
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        valueEventlistenerDriver = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                driverFName = snapshot.child("firstName").getValue(String.class);
                driverLName = snapshot.child("lastName").getValue(String.class);
                driverCNumber = snapshot.child("contactNumber").getValue(String.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };


        mRef.addValueEventListener(valueEventListener);
        driverRef.addListenerForSingleValueEvent(valueEventlistenerDriver);

    }

    @Override
    public void onPause() {
        super.onPause();

        if (valueEventListener != null || valueEventlistenerDriver != null) {
            mRef.removeEventListener(valueEventListener);
            driverRef.removeEventListener(valueEventListener);
        }

    }
}