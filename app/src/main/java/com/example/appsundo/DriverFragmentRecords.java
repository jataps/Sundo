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

public class DriverFragmentRecords extends Fragment implements RecyclerViewInterface {

    RecyclerView recyclerAssignedStudent;
    DatabaseReference mRef;
    CustomStudentAdapter adapter;
    ArrayList<User> list;
    User student;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_records, container, false);

        recyclerAssignedStudent = view.findViewById(R.id.recyclerAssignedStudents);

        String uidDriver = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRef = FirebaseDatabase.getInstance().getReference().child("USERS").child("DRIVER").child(uidDriver).child("ASSIGNED_STUDENT");

        recyclerAssignedStudent.setHasFixedSize(true);
        recyclerAssignedStudent.setLayoutManager(new LinearLayoutManager(getActivity()));

        list = new ArrayList<>();
        adapter = new CustomStudentAdapter(getActivity(), list, this);
        recyclerAssignedStudent.setAdapter(adapter);

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
        return view;
    }

    @Override
    public void onItemClick(int position) {

    }

}