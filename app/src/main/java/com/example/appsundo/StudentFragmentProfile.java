package com.example.appsundo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentFragmentProfile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StudentFragmentProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StudentFragmentProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static StudentFragmentProfile newInstance(String param1, String param2) {
        StudentFragmentProfile fragment = new StudentFragmentProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    private TextView emailText;

    private FirebaseAuth mAuth;

    private String info_id;

    private MaterialButton signOutBtnStudent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_profile, container, false);

        mAuth = FirebaseAuth.getInstance();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        String uid = mAuth.getCurrentUser().getUid();

        DatabaseReference info_ref = ref.child("USERS").child("STUDENT").child(uid).child("INFO_ID");

        emailText = view.findViewById(R.id.emailText);

        signOutBtnStudent = view.findViewById(R.id.signOutBtnStudent);

        signOutBtnStudent.setOnClickListener(view1 -> {

            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LogIn.class);
            startActivity(intent);
            getActivity().finish();
        });

        //get info_id
        getInfoID(info_ref);

        // Inflate the layout for this fragment
        return view;
    }

    void getInfoID (DatabaseReference info_ref) {

        info_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                info_id = String.valueOf(snapshot.getValue());

                emailText.setText(info_id);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



/*
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

                                    driverIntent = new Intent(getApplicationContext(), HomeDriver.class);

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

                                            if (infoIdValue.equals(false)) {

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
*/

}