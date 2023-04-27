package com.example.appsundo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DriverArrivedDetails extends AppCompatActivity  implements RecyclerViewInterface {

    RecyclerView studentList;
    DatabaseReference mRef;
    CustomStudentAdapter adapter;
    ArrayList<User> list;
    MaterialButton btnDropOffStudent;

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

    User userDriver;

    String firstName, lastName, emerName, emerNumber, contactNumber, province, city, barangay, stAddress, uidStudent, accountCode, infoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_arrived_details);

        studentList = findViewById(R.id.studentList);
        //btnDropOffStudent = findViewById(R.id.btnDropOffStudent);

        txtFname = findViewById(R.id.txtFName);
        txtLname = findViewById(R.id.txtLName);
        // txtEmerName = findViewById(R.id.textEmerName);
        // txtEmerNumber = findViewById(R.id.textEmerNumber);
        txtContactNumber = findViewById(R.id.txtContactNumber);
        txtProvince = findViewById(R.id.textAddress);
        txtStAddress = findViewById(R.id.textStAddress);

        uidStudent = getIntent().getStringExtra("UID");
        accountCode = getIntent().getStringExtra("ACCOUNT_CODE");
        lastName = getIntent().getStringExtra("LAST_NAME");
        firstName = getIntent().getStringExtra("FIRST_NAME");
        contactNumber = getIntent().getStringExtra("CONTACT_NUMBER");
        //       emerName = getIntent().getStringExtra("EMERGENCY_NAME");
        //       emerNumber = getIntent().getStringExtra("EMERGENCY_NUMBER");
        province = getIntent().getStringExtra("PROVINCE");
        city = getIntent().getStringExtra("CITY");
        barangay = getIntent().getStringExtra("BARANGAY");
        stAddress = getIntent().getStringExtra("ST_ADDRESS");
        infoId = getIntent().getStringExtra("INFO_REF");

        txtFname.setText(firstName);
        txtLname.setText(lastName);
//        txtEmerName.setText(emerName);
//        txtEmerNumber.setText(emerNumber);
        txtContactNumber.setText(contactNumber);
        txtProvince.setText(barangay + " | " + city + " | " + province);
        txtStAddress.setText(stAddress);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        /*
        btnDropOffStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference driverRef = ref.child("USERS").child("DRIVER").child(uid).child("ASSIGNED_STUDENT").child(infoId);

                driverRef.child("status").setValue("ARRIVED");

                getDriverInfo(ref, uid);

            }
        });

         */

    }

    public void getDriverInfo(DatabaseReference dbRef, String uid) {

        DatabaseReference infoIdRef;

        infoIdRef = dbRef.child("USERS").child("DRIVER").child(uid).child("INFO_ID");
        infoIdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User with the given UID exists in DRIVER

                    String infoID = String.valueOf(snapshot.getValue());

                    DatabaseReference profileRef = dbRef.child("USER_INFORMATION").child("DRIVER").child(infoID);

                    profileRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // User with the given UID exists in DRIVER

                                //studentList.clear();
                                userDriver = snapshot.getValue(User.class);
/*
                                DatabaseReference studentRef = dbRef.child("USERS").child("STUDENT").child(uidStudent).child("PICKUP_STUDENTS").child(userDriver.getAccountCode());

                                studentRef.child("UID").setValue(userDriver.getUid());
                                studentRef.child("firstName").setValue(userDriver.getFirstName());
                                studentRef.child("lastName").setValue(userDriver.getLastName());
                                studentRef.child("contactNumber").setValue(userDriver.getContactNumber());
                                studentRef.child("province").setValue(userDriver.getADDRESS().getProvince());
                                studentRef.child("city").setValue(userDriver.getADDRESS().getCity());
                                studentRef.child("barangay").setValue(userDriver.getADDRESS().getBarangay());

                                dbRef.child("USER_INFORMATION").child("STUDENT").child(infoId).child("DRIVER_ASSIGNED").setValue(userDriver.getAccountCode());


 */
                                Toast.makeText(DriverArrivedDetails.this, "Student Successfully Arrived", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(getApplicationContext(),ContainerDriver.class);
                                intent.putExtra("fragment_to_display","fragment_service");
                                intent.putExtra("service_to_display","onboard_student");
                                startActivity(intent);
                                finish();

                            } else {

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onItemClick(int position) {

    }
}