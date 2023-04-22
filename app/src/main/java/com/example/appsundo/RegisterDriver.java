package com.example.appsundo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class RegisterDriver extends AppCompatActivity {

    MaterialButton registerDriverBtn;
    TextInputEditText editTextEmail, editTextPassword;
    TextView haveAccount;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://sundo-app-44703-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);

        mAuth = FirebaseAuth.getInstance();

        registerDriverBtn = findViewById(R.id.registerDriverBtn);
        editTextPassword = findViewById(R.id.password);
        editTextEmail = findViewById(R.id.username);
        progressBar = findViewById(R.id.progressBar);
        haveAccount = findViewById(R.id.haveAccount);

        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                startActivity(intent);
                finish();
            }
        });

        registerDriverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email = editTextEmail.getText().toString().trim();
                password = editTextPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    editTextEmail.setError("Enter Email!");
                    editTextEmail.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    editTextEmail.setError("Please provide a valid email!");
                    editTextEmail.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    editTextPassword.setError("Enter password!");
                    editTextPassword.requestFocus();
                    return;
                }

                if (password.length() <= 5) {
                    editTextPassword.setError("Password should be at least 6 characters long.");
                    editTextPassword.requestFocus();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {

                                    //send verification code
                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegisterDriver.this, "User registered successfully. Please verify your email id.",
                                                        Toast.LENGTH_SHORT).show();

                                                String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                                DatabaseReference driverRef = dbRef.child("USERS").child("DRIVER").child(currentUser);

                                                //USER UID store in realtime database
                                                driverRef.child("email").setValue(email);
                                                driverRef.child("UID").setValue(currentUser);
                                                driverRef.child("INFO_ID").setValue(0);
                                                driverRef.child("HISTORY_ID").setValue(0);

                                                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                                                startActivity((intent));
                                                finish();

                                            } else {
                                                Toast.makeText(RegisterDriver.this, task.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(RegisterDriver.this, task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }
}