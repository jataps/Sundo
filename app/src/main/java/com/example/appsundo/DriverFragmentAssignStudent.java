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

public class DriverFragmentAssignStudent extends Fragment implements RecyclerViewInterface {

    RecyclerView allStudentList;
    DatabaseReference mRef;
    CustomStudentAdapter adapter;
    ArrayList<User> list;
    MaterialButton addStudentBtn;

    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_assign_student, container, false);

        allStudentList = view.findViewById(R.id.allStudentList);
        addStudentBtn = view.findViewById(R.id.addStudentBtn);

        mRef = FirebaseDatabase.getInstance().getReference().child("USER_INFORMATION").child("STUDENT");

        allStudentList.setHasFixedSize(true);
        allStudentList.setLayoutManager(new LinearLayoutManager(getActivity()));

        list = new ArrayList<>();
        adapter = new CustomStudentAdapter(getActivity(), list, this);
        allStudentList.setAdapter(adapter);


        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    user = dataSnapshot.getValue(User.class);
                    list.add(user);

                    Collections.sort(list);
                }



                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),DriverAddStudent.class);
                intent.putExtra("fragment_to_display","fragment_service");
                startActivity(intent);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onItemClick(int position) {

        Intent intent = new Intent(getActivity(), DriverAddStudent.class);

        intent.putExtra("fragment_to_display","fragment_service");
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