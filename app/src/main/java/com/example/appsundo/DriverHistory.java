package com.example.appsundo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DriverHistory extends AppCompatActivity implements RecyclerViewInterface{

    MaterialButton btnBack;
    RecyclerView historyList;
    TextView txtFname;
    TextView txtLname;
    
    String studentUID;
    String driverUID;
    String studentLName;
    String studentFName;
    String studentCNumber;
    String driverFname;
    String driverLName;
    String driverCNumber;

    History history;
    ArrayList<History> list;
    CustomHistoryAdapter adapter;

    ValueEventListener valueEventListener;

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_history);

        List<History> datesWithUID2 = new ArrayList<>();
        
        txtFname = findViewById(R.id.txtFName);
        txtLname = findViewById(R.id.txtLName);
        historyList = findViewById(R.id.historyList);
        btnBack = findViewById(R.id.btnBack);

        driverUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        studentUID = getIntent().getStringExtra("student_uid");
        studentLName = getIntent().getStringExtra("student_last_name");
        studentFName = getIntent().getStringExtra("student_first_name");
        studentCNumber = getIntent().getStringExtra("student_contact_number");
        driverFname = getIntent().getStringExtra("driver_first_name");
        driverLName = getIntent().getStringExtra("driver_last_name");
        driverCNumber = getIntent().getStringExtra("driver_contact_number");

        txtFname.setText(studentFName);
        txtLname.setText(studentLName);

        historyList.setHasFixedSize(true);
        historyList.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new CustomHistoryAdapter(this, list, this);
        historyList.setAdapter(adapter);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    String date;
                    if (dataSnapshot.hasChild(driverUID)) {
                        History mHistory = new History();

                        date = dataSnapshot.getKey();

                        mHistory.setDate(dataSnapshot.getKey());
                        mHistory.setDriverUID(driverUID);

                        for (DataSnapshot dtSnap : dataSnapshot.getChildren()){
                            mHistory.setStudentUID(dtSnap.getKey());
                            dtSnap.child("TO_HOME").child("DROP_OFF").child("dropOffTime").getValue();
                            dtSnap.child("TO_HOME").child("PICKUP").child("pickupTime").getValue();
                            dtSnap.child("TO_SCHOOL").child("DROP_OFF").child("dropOffTime").getValue();
                            dtSnap.child("TO_SCHOOL").child("PICKUP").child("pickupTime").getValue();
                        }

                        list.add(mHistory);
                    }

                    /*
                    history = dataSnapshot.getValue(History.class);
                    list.add(history);
                     */

                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        dbRef.child("HISTORY").addListenerForSingleValueEvent(valueEventListener);




        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ContainerDriver.class);
                intent.putExtra("fragment_to_display","fragment_service");
                intent.putExtra("service_to_display","driver_records");
                startActivity(intent);
                finish();
            }
        });
        
    }

    @Override
    public void onItemClick(int position) {

    }



}