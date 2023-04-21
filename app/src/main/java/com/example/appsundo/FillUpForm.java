package com.example.appsundo;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FillUpForm extends AppCompatActivity {

    //region TextView
    TextView uidText;
    TextView provinceText;
    TextView cityText;
    TextView barangayText;
    TextView vehicleText;
    //endregion

    //region TextInputEditText
    TextInputEditText editTextFirstName;
    TextInputEditText editTextLastName;
    TextInputEditText editTextPhoneNumber;
    TextInputEditText editTextEmergencyName;
    TextInputEditText editTextEmergencyNumber;
    TextInputEditText editTextAddNotes;
    TextInputEditText editTextPlateNumber;
    TextInputEditText editTextSeatingCapacity;
    //endregion

    TextInputEditText[] editTextArr;

    //region TEXT INPUT LAYOUT
    TextInputLayout plateNumberContainer;
    TextInputLayout seatingCapacityContainer;
    //endregion

    //region MaterialButton
    MaterialButton signOutBtnStudent;
    MaterialButton submitBtnStudent;
    //endregion

    //region Strings
    String userType;
    String selectedProvince;
    String selectedCity;
    String selectedBarangay;
    //endregion

    //region Spinners
    Spinner provinceSpinner;
    Spinner citySpinner;
    Spinner barangaySpinner;
    //endregion

    //region Adapters
    ArrayAdapter<CharSequence> provinceAdapter;
    ArrayAdapter<CharSequence> cityAdapter;
    ArrayAdapter<CharSequence> barangayAdapter;
    //endregion

    //region HashMap
    HashMap<String, Integer> provinces = new HashMap<>();
    HashMap<String, Integer> cities = new HashMap<>();
    //endregion

    //region FIREBASE
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://sundo-app-44703-default-rtdb.firebaseio.com/");
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_up_form);

        getHashMaps();

        //region declaration of elements

        ArrayList<TextInputEditText> editTextArray = new ArrayList<>();
        ArrayList<TextInputEditText> editNumberArray = new ArrayList<>();

        //EDIT TEXTS
        editTextArray.add(editTextFirstName = findViewById(R.id.firstName));
        editTextArray.add(editTextLastName = findViewById(R.id.lastName));
        editTextArray.add(editTextAddNotes = findViewById(R.id.addressNote));
        editTextArray.add(editTextEmergencyName = findViewById(R.id.emergencyName));
        editTextArray.add(editTextPlateNumber = findViewById(R.id.plateNumber));

        editNumberArray.add(editTextPhoneNumber = findViewById(R.id.phoneNumber));
        editNumberArray.add(editTextEmergencyNumber = findViewById(R.id.emergencyNumber));
        editNumberArray.add(editTextSeatingCapacity = findViewById(R.id.seatingCapacity));


        // EDIT TEXTS LAYOUT
        plateNumberContainer = findViewById(R.id.plateNumberContainer);
        seatingCapacityContainer = findViewById(R.id.seatingCapacityContainer);

        //MATERIAL BUTTONS
        signOutBtnStudent = findViewById(R.id.signOutBtnStudent);
        submitBtnStudent = findViewById(R.id.submitBtnStudent);

        //TEXT VIEWS
        uidText = findViewById(R.id.uidText);
        provinceText = findViewById(R.id.provinceText);
        cityText = findViewById(R.id.cityText);
        barangayText = findViewById(R.id.barangayText);
        vehicleText = findViewById(R.id.vehicleText);

        CustomRulesFunctions crf = new CustomRulesFunctions();
        crf.restrictText(editTextArray);
        crf.restrictNumber(editNumberArray);

        //SPINNER
        provinceSpinner = findViewById(R.id.provinceSpinner);
        provinceAdapter = ArrayAdapter.createFromResource(this, R.array.array_provinces, R.layout.spinner_layout);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinceSpinner.setAdapter(provinceAdapter);

        //endregion declaration

        // WILL GET THE USER TYPE
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dbRef.child("USERS").child("STUDENT").orderByChild("UID").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User is a STUDENT
                    userType = "STUDENT";

                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.d(TAG, error.getMessage());
            }
        });

        dbRef.child("USERS").child("DRIVER").orderByChild("UID").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User with the given UID exists in DRIVER
                    userType = "DRIVER";
                    //uidText.setText(userType);
                    vehicleDetailShow(View.VISIBLE);
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        submitBtnStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String firstName = String.valueOf(editTextFirstName.getText());
                String lastName = String.valueOf(editTextLastName.getText());
                String contactNumber = String.valueOf(editTextPhoneNumber.getText());

                String emergencyName = String.valueOf(editTextEmergencyName.getText());
                String emergencyNumber = String.valueOf(editTextEmergencyNumber.getText());

                String province = String.valueOf(selectedProvince);
                String city = String.valueOf(selectedCity);
                String barangay = String.valueOf(selectedBarangay);

                String addNote = String.valueOf(editTextAddNotes.getText());

                // CHECK IF EDIT TEXTS ARE EMPTY
                if (TextUtils.isEmpty(firstName)) {
                    editTextFirstName.setError("Enter first name!");
                    editTextFirstName.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(lastName)) {
                    editTextLastName.setError("Enter last name!");
                    editTextLastName.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(contactNumber)) {
                    editTextPhoneNumber.setError("Enter valid number!");
                    editTextPhoneNumber.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(emergencyName)) {
                    editTextEmergencyName.setError("Enter valid name !");
                    editTextEmergencyName.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(emergencyNumber)) {
                    editTextEmergencyNumber.setError("Enter valid number!");
                    editTextEmergencyNumber.requestFocus();
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
                    barangayText.setError("Select barangay");
                    barangayText.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(addNote)) {
                    editTextAddNotes.setError("Enter note!");
                    editTextAddNotes.requestFocus();
                    return;
                }

                if (seatingCapacityContainer.getVisibility() == View.VISIBLE) {
                    String seatingCapacity = String.valueOf(editTextSeatingCapacity.getText());
                    if (TextUtils.isEmpty(seatingCapacity)) {
                        editTextSeatingCapacity.setError("Enter vehicle seating capacity!");
                        editTextSeatingCapacity.requestFocus();
                        return;
                    }
                }

                if (plateNumberContainer.getVisibility() == View.VISIBLE) {
                    String plateNumber = String.valueOf(editTextSeatingCapacity.getText());
                    if (TextUtils.isEmpty(plateNumber)) {
                        editTextPlateNumber.setError("Enter vehicle plate number!");
                        editTextPlateNumber.requestFocus();
                        return;
                    }
                }


                String completeAdd = addNote + " Brgy. " + barangay + ", " + city + ", " + province;

                DatabaseReference userRef = dbRef.child("USERS").child(userType).child(uid).child("INFO_ID");
                DatabaseReference recordRef = dbRef.child("USER_INFORMATION").child(userType);

                String requestID = recordRef.push().getKey();

                userRef.setValue(requestID);

                HashMap map = new HashMap();
                map.put("uid", uid);
                map.put("lastName", lastName);
                map.put("firstName", firstName);
                map.put("contactNumber", contactNumber);

                map.put("emergencyName", emergencyName);
                map.put("emergencyNumber", emergencyNumber);

                map.put("completeAdd", completeAdd);

                if (userType.equals("DRIVER")) {
                    String plateNumber = String.valueOf(editTextPlateNumber.getText());
                    String seatingCapacity = String.valueOf(editTextSeatingCapacity.getText());

                    map.put("VEHICLE/plateNumber", plateNumber);
                    map.put("VEHICLE/capacity", seatingCapacity);
                    map.put("VEHICLE/status", "active");

                }

                recordRef.child(requestID).updateChildren(map);

                Toast.makeText(FillUpForm.this, "Register Successful", Toast.LENGTH_SHORT).show();

                Intent intent = userType.equals("DRIVER") ?
                        new Intent(getApplicationContext(), ContainerDriver.class) :
                        new Intent(getApplicationContext(), ContainerStudent.class);

                startActivity(intent);
                finish();

            }
        });

        signOutBtnStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                startActivity(intent);
                finish();
            }
        });

        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                citySpinner = findViewById(R.id.citySpinner);

                selectedProvince = provinceSpinner.getSelectedItem().toString();

                int parentID = adapterView.getId();
                if(parentID == R.id.provinceSpinner){

                    if (provinces.containsKey(selectedProvince)){
                        cityAdapter = ArrayAdapter.createFromResource(adapterView.getContext(), provinces.get(selectedProvince), R.layout.spinner_layout);
                    }

                    cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    //populate the cities according to selected province
                    citySpinner.setAdapter(cityAdapter);

                    //To obtain the selected city from the cityspinner
                    citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            barangaySpinner = findViewById(R.id.barangaySpinner);

                            selectedCity = citySpinner.getSelectedItem().toString();

                            int parentID = adapterView.getId();

                            if(parentID == R.id.citySpinner) {

                                if (cities.containsKey(selectedCity)){
                                    barangayAdapter = ArrayAdapter.createFromResource(adapterView.getContext(), cities.get(selectedCity), R.layout.spinner_layout);
                                }

                                barangayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                barangaySpinner.setAdapter(barangayAdapter);

                                barangaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        selectedBarangay = barangaySpinner.getSelectedItem().toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });

                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void getHashMaps() {
        Locations location = new Locations();

        cities = location.getCities();
        provinces = location.getProvinces();
    }

    public void vehicleDetailShow(int is_visible) {
        vehicleText.setVisibility(is_visible);
        plateNumberContainer.setVisibility(is_visible);
        seatingCapacityContainer.setVisibility(is_visible);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        FillUpForm.super.onBackPressed();
                    }
                }).create().show();
    }

}