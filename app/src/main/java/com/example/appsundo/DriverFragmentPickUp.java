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

public class DriverFragmentPickUp extends Fragment implements RecyclerViewInterface{

    RecyclerView pickUpStudentList;
    DatabaseReference mRef;
    CustomStudentAdapter adapter;
    ArrayList<User> list;
    User student;

    MaterialButton addStudentBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_pick_up, container, false);

        pickUpStudentList = view.findViewById(R.id.pickUpStudentList);

        pickUpStudentList.setHasFixedSize(true);
        pickUpStudentList.setLayoutManager(new LinearLayoutManager(getActivity()));

        list = new ArrayList<>();
        adapter = new CustomStudentAdapter(getActivity(), list, this);
        pickUpStudentList.setAdapter(adapter);

        String uidDriver = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference().child("USERS").child("DRIVER").child(uidDriver).child("ASSIGNED_STUDENT");

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    student = dataSnapshot.getValue(User.class);
                    student.setReferenceID(dataSnapshot.getKey());

                    if(student.getStatus().equals("WAITING")) {
                        list.add(student);
                        Collections.sort(list);
                    }

                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onItemClick(int position) {

        Intent intent = new Intent(getActivity(), DriverPickupStudent.class);

        intent.putExtra("fragment_to_display","fragment_service");
        intent.putExtra("INFO_REF", list.get(position).getReferenceID());
        intent.putExtra("UID", list.get(position).getUid());
        intent.putExtra("LAST_NAME", list.get(position).getLastName());
        intent.putExtra("FIRST_NAME", list.get(position).getFirstName());
        intent.putExtra("CONTACT_NUMBER", list.get(position).getContactNumber());
        intent.putExtra("EMERGENCY_NAME", list.get(position).getEmergencyName());
        intent.putExtra("EMERGENCY_NUMBER", list.get(position).getEmergencyNumber());
        intent.putExtra("PROVINCE", list.get(position).getADDRESS().getProvince());
        intent.putExtra("CITY", list.get(position).getADDRESS().getCity());
        intent.putExtra("BARANGAY", list.get(position).getADDRESS().getBarangay());
        intent.putExtra("ST_ADDRESS", list.get(position).getADDRESS().getStreetAddress());
        intent.putExtra("ACCOUNT_CODE", list.get(position).getAccountCode());

        startActivity(intent);


    }
}