package com.example.appsundo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogIn extends AppCompatActivity {

    MaterialButton registerBtn;
    MaterialButton loginBtn;
    TextInputEditText editTextEmail;
    TextInputEditText editTextPassword;
    TextView forgotPass;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        //if last user has logged in and verified, auto-sign in will trigger
        if((currentUser != null) && (currentUser.isEmailVerified())) {

            String uid = mAuth.getCurrentUser().getUid();
            filterUserType(uid);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        registerBtn = findViewById(R.id.registerbtn);
        loginBtn = findViewById(R.id.loginbtn);
        editTextEmail = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        forgotPass = findViewById(R.id.forgotPass);

        mAuth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterMain.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = String.valueOf(editTextEmail.getText()).trim();
                password = String.valueOf(editTextPassword.getText());

                if (TextUtils.isEmpty(email)) {
                    editTextEmail.setError("Enter Email!");
                    editTextEmail.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    editTextPassword.setError("Enter password!");
                    editTextPassword.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                // Check if email is verified
                                if(!(mAuth.getCurrentUser().isEmailVerified())){
                                    Toast.makeText(getApplicationContext(), "Please verify your email.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String uid = mAuth.getCurrentUser().getUid();

                                filterUserType(uid);

                            } else {
                                // If log in fails, display a message to the user.
                                Toast.makeText(LogIn.this, task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(intent);
            }
        });

    }

    void filterUserType (String uid) {
        // Login successful, get the user's UID
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        DatabaseReference studentRef = ref.child("USERS").child("STUDENT");
        DatabaseReference driverRef = ref.child("USERS").child("DRIVER");


        // Check if the user is a student or a driver
        driverRef.orderByChild("UID").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User with the given UID exists in DRIVER

                    DatabaseReference driverInfoRef = driverRef.child(uid);

                    driverInfoRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild("INFO_ID")) {
                                // "INFO_ID" exists for this user
                                Object infoIdValue = snapshot.child("INFO_ID").getValue();

                                Intent driverIntent;

                                if (infoIdValue.equals("false")) {

                                    driverIntent = new Intent(getApplicationContext(), FillUpForm.class);

                                } else {

                                    driverIntent = new Intent(getApplicationContext(), ContainerDriver.class);

                                }

                                startActivity(driverIntent);
                                finish();

                            } else {

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    // User with the given UID does not exist in DRIVER
                    // Check if it exists in STUDENT
                    studentRef.orderByChild("UID").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // User is a STUDENT

                                DatabaseReference studentInfoRef = studentRef.child(uid);

                                studentInfoRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.hasChild("INFO_ID")) {
                                            // "INFO_ID" exists for this user
                                            Object infoIdValue = snapshot.child("INFO_ID").getValue();

                                            Intent studentIntent;

                                            if (infoIdValue.equals("false")) {

                                                studentIntent = new Intent(getApplicationContext(), FillUpForm.class);

                                            } else {

                                                studentIntent = new Intent(getApplicationContext(), ContainerStudent.class);

                                            }

                                            startActivity(studentIntent);
                                            finish();


                                        } else {
                                            // "INFO_ID" does not exist for this user
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            } else {
                                // User with the given UID does not exist in either DRIVER or STUDENT node
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        LogIn.super.onBackPressed();
                    }
                }).create().show();
    }
}