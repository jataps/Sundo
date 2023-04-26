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

public class RegisterStudent extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    TextView haveAccount;
    MaterialButton registerBtn;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://sundo-app-44703-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        registerBtn = findViewById(R.id.registerBtn);
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

        registerBtn.setOnClickListener(new View.OnClickListener() {
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
                                                Toast.makeText(RegisterStudent.this, "User registered successfully. Please verify your email id.",
                                                        Toast.LENGTH_SHORT).show();

                                                String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                                DatabaseReference studentRef = dbRef.child("USERS").child("STUDENT").child(currentUser);

                                                //USER UID store in realtime database
                                                studentRef.child("email").setValue(email);
                                                studentRef.child("UID").setValue(currentUser);
                                                studentRef.child("INFO_ID").setValue(false);
                                                studentRef.child("HISTORY_ID").setValue(false);

                                                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(RegisterStudent.this, task.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(RegisterStudent.this, task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

    }
}