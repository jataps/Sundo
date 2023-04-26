package com.example.appsundo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

    //private TextView emailText;
    //------------

    private MaterialButton signOutBtnStudent;
    private MaterialButton btnEditProfile;
    private MaterialButton btnSaveProfile;
    private MaterialButton btnCancelSave;

    private TextView txtFirstName;
    private TextView txtLastName;

    private TextView uidText;
    private TextView brgyText;
    private TextView cityText;
    private TextView provinceText;

    private TextInputEditText txtfieldFirstName;
    private TextInputEditText txtfieldLastName;
    private TextInputEditText txtfieldPhone;
    private TextInputEditText txtEmergencyName;
    private TextInputEditText txtEmergencyNumber;
    private TextInputEditText txtAddressNote;

    private Spinner provinceSpinner;
    private Spinner citySpinner;
    private Spinner barangaySpinner;

    private ArrayList<TextInputEditText> textArr = new ArrayList<>();
    private ArrayList<TextInputEditText> numArr = new ArrayList<>();

    private ArrayAdapter<CharSequence> provinceAdapter;
    private ArrayAdapter<CharSequence> cityAdapter;
    private ArrayAdapter<CharSequence> barangayAdapter;

    private HashMap<String, Integer> provinces = new HashMap<>();
    private HashMap<String, Integer> cities = new HashMap<>();

    private List<String> provinceItems;
    private List<String> cityItems;
    private List<String> barangayItems;

    private String uid;
    private String selectedProvince;
    private String selectedCity;
    private String selectedBarangay;
    private String infoID;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String emergName;
    private String emergNumber;
    private String addressNote;

    private DatabaseReference dbRef;
    private DatabaseReference infoIdRef;
    private DatabaseReference profileRef;



    private String info_id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_profile, container, false);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://sundo-app-44703-default-rtdb.firebaseio.com/");
        infoIdRef = dbRef.child("USERS").child("STUDENT").child(uid).child("INFO_ID");
        //DatabaseReference info_ref = ref.child("USERS").child("STUDENT").child(uid).child("INFO_ID");

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);
        signOutBtnStudent = view.findViewById(R.id.signOutBtnStudent);
        btnCancelSave = view.findViewById(R.id.btnCancelSave);

        provinceSpinner = view.findViewById(R.id.provinceSpinner);
        citySpinner = view.findViewById(R.id.citySpinner);
        barangaySpinner = view.findViewById(R.id.barangaySpinner);

        txtFirstName = view.findViewById(R.id.txtFName);
        txtLastName = view.findViewById(R.id.txtLName);

        textArr.add(txtfieldFirstName = view.findViewById(R.id.txtfieldFirstName));
        textArr.add(txtfieldLastName = view.findViewById(R.id.txtfieldLastName));
        numArr.add(txtfieldPhone = view.findViewById(R.id.txtfieldPhone));
        textArr.add(txtAddressNote = view.findViewById(R.id.txtAddressNote));

        textArr.add(txtEmergencyName = view.findViewById(R.id.emergencyName));
        numArr.add(txtEmergencyNumber = view.findViewById(R.id.emergencyNumber));

        CustomRulesFunctions etr = new CustomRulesFunctions();
        etr.restrictText(textArr);
        etr.restrictNumber(numArr);

        enableFields(false);

        getHashMaps();

        provinceAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.array_provinces, R.layout.spinner_layout);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinceSpinner.setAdapter(provinceAdapter);

        provinceItems = Arrays.asList(getResources().getStringArray(R.array.array_provinces));

        cityAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_layout, new ArrayList<>());
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);

        barangayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_layout, new ArrayList<>());
        barangayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        barangaySpinner.setAdapter(barangayAdapter);

        infoIdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User with the given UID exists in DRIVER

                    infoID = String.valueOf(snapshot.getValue());

                    profileRef = dbRef.child("USER_INFORMATION").child("STUDENT").child(infoID);

                    profileRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // User with the given UID exists in STUDENT

                                firstName = String.valueOf(snapshot.child("firstName").getValue());
                                lastName = String.valueOf(snapshot.child("lastName").getValue());
                                phoneNumber = String.valueOf(snapshot.child("contactNumber").getValue());

                                emergName = String.valueOf(snapshot.child("emergencyName").getValue());
                                emergNumber = String.valueOf(snapshot.child("emergencyNumber").getValue());

                                addressNote = String.valueOf(snapshot.child("ADDRESS/streetAddress").getValue());
                                selectedProvince = String.valueOf(snapshot.child("ADDRESS/province").getValue());
                                selectedCity = String.valueOf(snapshot.child("ADDRESS/city").getValue());
                                selectedBarangay = String.valueOf(snapshot.child("ADDRESS/barangay").getValue());

                                fillInfoFields();

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

        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                selectedProvince = (String) adapterView.getItemAtPosition(i);


                // Update the middle spinner's items based on the selected value of the outer spinner

                cityItems = Arrays.asList(getResources().getStringArray(provinces.get(selectedProvince)));

                cityAdapter.clear();
                cityAdapter.addAll(cityItems);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                selectedCity = (String) adapterView.getItemAtPosition(i);
                // Update the middle spinner's items based on the selected value of the outer spinner

                barangayItems = Arrays.asList(getResources().getStringArray(cities.get(selectedCity)));

                barangayAdapter.clear();
                barangayAdapter.addAll(barangayItems);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        barangaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedBarangay = (String) adapterView.getItemAtPosition(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnCancelSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnSaveProfile.setVisibility(View.GONE);
                btnCancelSave.setVisibility(View.GONE);
                btnEditProfile.setVisibility(View.VISIBLE);

                fillInfoFields();

                enableFields(false);
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnSaveProfile.setVisibility(View.VISIBLE);
                btnCancelSave.setVisibility(View.VISIBLE);
                btnEditProfile.setVisibility(View.GONE);

                enableFields(true);

            }
        });

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String firstName = String.valueOf(txtfieldFirstName.getText());
                String lastName = String.valueOf(txtfieldLastName.getText());
                String contactNumber = String.valueOf(txtfieldPhone.getText());

                String emergName = String.valueOf(txtEmergencyName.getText());
                String emergNumber = String.valueOf(txtEmergencyNumber.getText());

                String addNote = String.valueOf(txtAddressNote.getText());


                if (TextUtils.isEmpty(firstName)) {
                    txtfieldFirstName.setError("Enter first name!");
                    txtfieldFirstName.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(lastName)) {
                    txtfieldLastName.setError("Enter last name!");
                    txtfieldLastName.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(contactNumber)) {
                    txtfieldPhone.setError("Enter valid number!");
                    txtfieldPhone.requestFocus();
                    return;
                }


                if (TextUtils.isEmpty(emergName)) {
                    txtEmergencyName.setError("Enter valid name !");
                    txtEmergencyName.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(emergNumber)) {
                    txtEmergencyNumber.setError("Enter valid number!");
                    txtEmergencyNumber.requestFocus();
                    return;
                }


                if (selectedProvince.equals("Select your province")) {
                    provinceText.setError("Select province!");
                    provinceText.requestFocus();
                    return;
                }

                if (selectedCity.equals("Select your city")) {
                    ((TextView)citySpinner.getSelectedView()).setError("Select city");
                    citySpinner.requestFocus();
                    return;
                }

                if (selectedBarangay.equals("Select your barangay")) {
                    brgyText.setError("Select barangay");
                    brgyText.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(addNote)) {
                    txtAddressNote.setError("Enter note!");
                    txtAddressNote.requestFocus();
                    return;
                }

                HashMap map = new HashMap();
                map.put("uid", uid);
                map.put("lastName", lastName);
                map.put("firstName", firstName);
                map.put("contactNumber", contactNumber);

                map.put("emergencyName", emergName);
                map.put("emergencyNumber", emergNumber);

                map.put("ADDRESS/province", selectedProvince);
                map.put("ADDRESS/city", selectedCity);
                map.put("ADDRESS/barangay", selectedBarangay);
                map.put("ADDRESS/streetAddress", addNote);

                profileRef.updateChildren(map);

                enableFields(false);
                btnSaveProfile.setVisibility(View.GONE);
                btnCancelSave.setVisibility(View.GONE);
                btnEditProfile.setVisibility(View.VISIBLE);

            }
        });

        signOutBtnStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Navigate the user to the login screen
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getActivity(), LogIn.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();

            }
        });

        //emailText = view.findViewById(R.id.emailText);
        //get info_id
        //getInfoID(info_ref);

        // Inflate the layout for this fragment
        return view;
    }

    public void fillInfoFields() {
        txtFirstName.setText(firstName);
        txtLastName.setText(lastName);

        txtfieldFirstName.setText(firstName);
        txtfieldLastName.setText(lastName);
        txtfieldPhone.setText(phoneNumber);

        txtEmergencyName.setText(emergName);
        txtEmergencyNumber.setText(emergNumber);

        txtAddressNote.setText(addressNote);

        provinceSpinner.setSelection(provinceItems.indexOf(selectedProvince));

        cityItems = Arrays.asList(getResources().getStringArray(provinces.get(selectedProvince)));
        cityAdapter.clear();
        cityAdapter.addAll(cityItems);

        citySpinner.setSelection(cityItems.indexOf(selectedCity));

        barangayItems = Arrays.asList(getResources().getStringArray(cities.get(selectedCity)));
        barangayAdapter.clear();
        barangayAdapter.addAll(barangayItems);

        barangaySpinner.setSelection(barangayItems.indexOf(selectedBarangay));
    }

    public void enableFields (Boolean isEnabled) {

        txtfieldFirstName.setEnabled(isEnabled);
        txtfieldLastName.setEnabled(isEnabled);
        txtAddressNote.setEnabled(isEnabled);
        txtEmergencyName.setEnabled(isEnabled);
        txtEmergencyNumber.setEnabled(isEnabled);
        txtfieldPhone.setEnabled(isEnabled);

        provinceSpinner.setEnabled(isEnabled);
        citySpinner.setEnabled(isEnabled);
        barangaySpinner.setEnabled(isEnabled);

    }

    public void getHashMaps() {

        Locations location = new Locations();

        cities = location.getCities();
        provinces = location.getProvinces();

    }
    /*
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
*/


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