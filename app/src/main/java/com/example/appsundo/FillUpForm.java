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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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

    String selectedTimeIn1;

    String selectedTimeIn2;

    String selectedTimeIn3;

    String selectedTimeIn4;

    String selectedTimeIn5;

    String selectedTimeOut1;

    String selectedTimeOut2;

    String selectedTimeOut3;

    String selectedTimeOut4;

    String selectedTimeOut5;


    String firstName, lastName, contactNumber, emergencyName, emergencyNumber, addNote;
    //endregion

    //region Spinners
    Spinner provinceSpinner;
    Spinner citySpinner;
    Spinner barangaySpinner;
    //endregion

    //region time in and out
    Spinner timeInSpinner1;

    Spinner timeOutSpinner1;

    Spinner timeInSpinner2;

    Spinner timeOutSpinner2;

    Spinner timeInSpinner3;

    Spinner timeOutSpinner3;

    Spinner timeInSpinner4;

    Spinner timeOutSpinner4;

    Spinner timeInSpinner5;

    Spinner timeOutSpinner5;
    // region end

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

        provinceSpinner = findViewById(R.id.provinceSpinner);
        citySpinner = findViewById(R.id.citySpinner);
        barangaySpinner = findViewById(R.id.barangaySpinner);

        CustomRulesFunctions crf = new CustomRulesFunctions();
        crf.restrictText(editTextArray);
        crf.restrictNumber(editNumberArray);


        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dbRef.child("USERS").child("STUDENT").orderByChild("UID").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User is a STUDENT
                    userType = "STUDENT";

                } else {

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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                Log.d(TAG, error.getMessage());
            }
        });


        submitBtnStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firstName = String.valueOf(editTextFirstName.getText());
                lastName = String.valueOf(editTextLastName.getText());
                contactNumber = String.valueOf(editTextPhoneNumber.getText());

                emergencyName = String.valueOf(editTextEmergencyName.getText());
                emergencyNumber = String.valueOf(editTextEmergencyNumber.getText());

                String addNote = String.valueOf(editTextAddNotes.getText());

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


                DatabaseReference recordRef = dbRef.child("USER_INFORMATION").child(userType);

                HashMap map = new HashMap();
                map.put("uid", uid);
                map.put("lastName", lastName);
                map.put("firstName", firstName);
                map.put("contactNumber", contactNumber);

                map.put("emergencyName", emergencyName);
                map.put("emergencyNumber", emergencyNumber);

                map.put("ADDRESS/province", selectedProvince);
                map.put("ADDRESS/city", selectedCity);
                map.put("ADDRESS/barangay", selectedBarangay);
                map.put("ADDRESS/streetAddress", addNote);

                if (userType.equals("DRIVER")) {
                    String plateNumber = String.valueOf(editTextPlateNumber.getText());
                    String seatingCapacity = String.valueOf(editTextSeatingCapacity.getText());

                    map.put("VEHICLE/plateNumber", plateNumber);
                    map.put("VEHICLE/capacity", seatingCapacity);
                    map.put("VEHICLE/status", "active");
                }

                recordRef.child(uid).updateChildren(map);
                dbRef.child("USERS").child(userType).child(uid).child("COMPLETE_FORM").setValue(true);

                Toast.makeText(FillUpForm.this, "Register Successful", Toast.LENGTH_SHORT).show();

                Intent intent = userType.equals("DRIVER") ?
                        new Intent(getApplicationContext(), ContainerDriver.class) :
                        new Intent(getApplicationContext(), ContainerStudent.class);

                startActivity(intent);
                finish();

            }
        });


        ArrayAdapter<CharSequence> provinceAdapter = ArrayAdapter.createFromResource(this, R.array.array_provinces, R.layout.spinner_layout);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinceSpinner.setAdapter(provinceAdapter);

        ArrayAdapter<CharSequence> cityAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, new ArrayList<>());
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);

        ArrayAdapter<CharSequence> barangayAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, new ArrayList<>());
        barangayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        barangaySpinner.setAdapter(barangayAdapter);

        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                selectedProvince = (String) adapterView.getItemAtPosition(i);
                // Update the middle spinner's items based on the selected value of the outer spinner

                List<String> cityItems = Arrays.asList(getResources().getStringArray(provinces.get(selectedProvince)));

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

                List<String> innerItems = Arrays.asList(getResources().getStringArray(cities.get(selectedCity)));

                barangayAdapter.clear();
                barangayAdapter.addAll(innerItems);

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

        signOutBtnStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                startActivity(intent);
                finish();
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