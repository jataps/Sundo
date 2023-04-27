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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DriverAddStudent extends AppCompatActivity implements RecyclerViewInterface {

    RecyclerView studentList;
    DatabaseReference mRef;
    CustomStudentAdapter adapter;
    ArrayList<User> list;
    MaterialButton addBtnConfirm;

    TextView txtFname;
    TextView txtLname;
    TextView txtEmerName;
    TextView txtEmerNumber;
    TextView txtContactNumber;
    TextView txtProvince;
    TextView txtCity;
    TextView txtBarangay;
    TextView txtStAddress;
    TextView txtUid;

    String firstName, lastName, emerName, emerNumber, contactNumber, province, city, barangay, stAddress, uid, fragment_to_display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_add_student);

        studentList = findViewById(R.id.studentList);
        addBtnConfirm = findViewById(R.id.addBtnConfirm);

        txtFname = findViewById(R.id.txtFName);
        txtLname = findViewById(R.id.txtLName);
        txtEmerName = findViewById(R.id.textEmerName);
        txtEmerNumber = findViewById(R.id.textEmerNumber);
        txtContactNumber = findViewById(R.id.txtContactNumber);
        txtProvince = findViewById(R.id.textAddress);
        txtStAddress = findViewById(R.id.textStAddress);
        
        uid = getIntent().getStringExtra("UID");
        lastName = getIntent().getStringExtra("LAST_NAME");
        firstName = getIntent().getStringExtra("FIRST_NAME");
        contactNumber = getIntent().getStringExtra("CONTACT_NUMBER");
        emerName = getIntent().getStringExtra("EMERGENCY_NAME");
        emerNumber = getIntent().getStringExtra("EMERGENCY_NUMBER");
        province = getIntent().getStringExtra("PROVINCE");
        city = getIntent().getStringExtra("CITY");
        barangay = getIntent().getStringExtra("BARANGAY");
        stAddress = getIntent().getStringExtra("ST_ADDRESS");

        txtFname.setText(firstName);
        txtLname.setText(lastName);
        txtEmerName.setText(emerName);
        txtEmerNumber.setText(emerNumber);
        txtContactNumber.setText(contactNumber);
        txtProvince.setText(barangay + " | " + city + " | " + province);
        txtStAddress.setText(stAddress);

        mRef = FirebaseDatabase.getInstance().getReference().child("USER_INFORMATION").child("STUDENT");

        studentList.setHasFixedSize(true);
        studentList.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapter = new CustomStudentAdapter(this, list, this);
        studentList.setAdapter(adapter);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    User user = dataSnapshot.getValue(User.class);
                    list.add(user);

                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),ContainerDriver.class);
                intent.putExtra("fragment_to_display","fragment_service");
                intent.putExtra("service_to_display","add_student");
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onItemClick(int position) {



    }
}