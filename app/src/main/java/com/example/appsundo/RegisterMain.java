package com.example.appsundo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
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

public class RegisterMain extends AppCompatActivity {

    MaterialButton  userBtn, registerBtn;
    TextView userTxt, haveAccount;

    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextInputEditText editTextEmail, editTextPassword;

    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://sundo-app-44703-default-rtdb.firebaseio.com/");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_main);

        mAuth = FirebaseAuth.getInstance();

        userBtn = findViewById(R.id.userBtn);
        registerBtn = findViewById(R.id.registerBtn);
        editTextEmail = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        userTxt = findViewById(R.id.userTxt);
        haveAccount = findViewById(R.id.haveAccount);
        progressBar = findViewById(R.id.progressBar);

        int colorStudent = ContextCompat.getColor(this, R.color.yellow);
        int colorDriver = ContextCompat.getColor(this, R.color.green);
        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtDefault = "Click to change to ";

                if (userBtn.getText().equals("DRIVER")) {
                    userTxt.setText(txtDefault + "DRIVER");
                    userBtn.setText("STUDENT");
                    userBtn.setBackgroundTintList(ColorStateList.valueOf(colorStudent));
                } else if (userBtn.getText().equals("STUDENT")) {
                    userTxt.setText(txtDefault + "STUDENT");
                    userBtn.setText("DRIVER");
                    userBtn.setBackgroundTintList(ColorStateList.valueOf(colorDriver));
                }

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
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    editTextEmail.setError("Please provide a valid email!");
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

                if (password.length() <= 5) {
                    editTextPassword.setError("Password should be at least 6 characters long.");
                    editTextPassword.requestFocus();
                    progressBar.setVisibility(View.GONE);
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
                                                Toast.makeText(RegisterMain.this, "User registered successfully. Please verify your email account.",
                                                        Toast.LENGTH_SHORT).show();

                                                String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                                DatabaseReference driverRef = dbRef.child("USERS").child(String.valueOf(userBtn.getText())).child(currentUser);

                                                //USER UID store in realtime database
                                                driverRef.child("email").setValue(email);
                                                driverRef.child("UID").setValue(currentUser);
                                                driverRef.child("COMPLETE_FORM").setValue(false);

                                                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                                                startActivity((intent));
                                                finish();

                                            } else {
                                                Toast.makeText(RegisterMain.this, task.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                } else {
                                    // If sign in fails, display a message to the user.
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(RegisterMain.this, task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                startActivity(intent);
                finish();
            }
        });

    }
}