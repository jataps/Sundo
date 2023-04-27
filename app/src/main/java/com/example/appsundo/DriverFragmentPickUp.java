package com.example.appsundo;

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

    MaterialButton addStudentBtn;

    User student;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_pick_up, container, false);

        pickUpStudentList = view.findViewById(R.id.pickUpStudentList);

        String uidDriver = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRef = FirebaseDatabase.getInstance().getReference().child("USERS").child("DRIVER").child(uidDriver).child("ASSIGNED_STUDENT");

        pickUpStudentList.setHasFixedSize(true);
        pickUpStudentList.setLayoutManager(new LinearLayoutManager(getActivity()));

        list = new ArrayList<>();
        adapter = new CustomStudentAdapter(getActivity(), list, this);
        pickUpStudentList.setAdapter(adapter);

        mRef.addValueEventListener(new ValueEventListener() {
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
        });

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_driver_pick_up, container, false);
    }

    @Override
    public void onItemClick(int position) {

    }
}