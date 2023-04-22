package com.example.appsundo;

import android.content.Intent;
import android.opengl.Visibility;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DriverFragmentProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DriverFragmentProfile extends Fragment {



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DriverFragmentProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DriverFragmentProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static DriverFragmentProfile newInstance(String param1, String param2) {
        DriverFragmentProfile fragment = new DriverFragmentProfile();
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

    private MaterialButton signOutBtnDriver;
    private MaterialButton btnEditProfile;
    private MaterialButton btnSaveProfile;

    private TextView txtFName;
    private TextView uidText;
    private TextView provinceText;
    private TextView cityText;
    private TextView barangayText;
    private TextView vehicleText;

    private TextInputLayout plateNumberContainer;
    private TextInputLayout seatingCapacityContainer;

    private TextInputEditText txtfieldFirstName;
    private TextInputEditText txtfieldLastName;
    private TextInputEditText txtfieldPhone;
    private TextInputEditText txtAddressNote;
    private TextInputEditText txtPlateNumber;
    private TextInputEditText txtSeatingCapacity;

    private Spinner provinceSpinner;
    private Spinner citySpinner;
    private Spinner barangaySpinner;

    private ArrayAdapter<CharSequence> provinceAdapter;
    private ArrayAdapter<CharSequence> cityAdapter;
    private ArrayAdapter<CharSequence> barangayAdapter;

    private ArrayList<TextInputEditText> textArr = new ArrayList<>();
    private ArrayList<TextInputEditText> numArr = new ArrayList<>();

    private HashMap<String, Integer> provinces = new HashMap<>();
    private HashMap<String, Integer> cities = new HashMap<>();

    private String selectedProvince;
    private String selectedCity;
    private String selectedBarangay;
    private String uid;
    private String infoID;

    private DatabaseReference dbRef;
    private DatabaseReference infoIdRef;
    private DatabaseReference profileRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_driver_profile, container, false);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://sundo-app-44703-default-rtdb.firebaseio.com/");

        infoIdRef = dbRef.child("USERS").child("DRIVER").child(uid).child("INFO_ID");


        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);
        signOutBtnDriver = view.findViewById(R.id.signOutBtnDriver);

        provinceSpinner = view.findViewById(R.id.provinceSpinner);
        citySpinner = view.findViewById(R.id.citySpinner);
        barangaySpinner = view.findViewById(R.id.barangaySpinner);

        txtFName = view.findViewById(R.id.txtFName);

        textArr.add(txtfieldFirstName = view.findViewById(R.id.txtfieldFirstName));
        textArr.add(txtfieldLastName = view.findViewById(R.id.txtfieldLastName));
        textArr.add(txtAddressNote = view.findViewById(R.id.txtAddressNote));
        textArr.add(txtPlateNumber = view.findViewById(R.id.txtPlateNumber));

        numArr.add(txtfieldPhone = view.findViewById(R.id.txtfieldPhone));
        numArr.add(txtSeatingCapacity = view.findViewById(R.id.txtSeatingCapacity));

        CustomRulesFunctions etr = new CustomRulesFunctions();
        etr.restrictText(textArr);
        etr.restrictNumber(numArr);

        infoIdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User with the given UID exists in DRIVER

                    infoID = String.valueOf(snapshot.getValue());

                    txtFName.setText(infoID);

                    profileRef = dbRef.child("USER_INFORMATION").child("DRIVER").child(infoID);

                    profileRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // User with the given UID exists in DRIVER

                                txtfieldFirstName.setText(String.valueOf(snapshot.child("firstName").getValue()));
                                txtfieldLastName.setText(String.valueOf(snapshot.child("lastName").getValue()));
                                txtAddressNote.setText(String.valueOf(snapshot.child("completeAdd").getValue()));
                                txtfieldPhone.setText(String.valueOf(snapshot.child("contactNumber").getValue()));

                                txtPlateNumber.setText(String.valueOf(snapshot.child("VEHICLE").child("plateNumber").getValue()));
                                txtSeatingCapacity.setText(String.valueOf(snapshot.child("VEHICLE").child("capacity").getValue()));

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

        getHashMaps();

        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

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

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnSaveProfile.setVisibility(View.VISIBLE);
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

                //String emergencyName = String.valueOf(editTextEmergencyName.getText());
                //String emergencyNumber = String.valueOf(editTextEmergencyNumber.getText());

                String addNote = String.valueOf(txtAddressNote.getText());

                String plateNumber = String.valueOf(txtPlateNumber.getText());
                String seatingCapacity = String.valueOf(txtSeatingCapacity.getText());

                /*
                etr.editTextEmpty(txtfieldFirstName, firstName, "Enter first Name!");
                etr.editTextEmpty(txtfieldLastName, lastName, "Enter last name!");
                etr.editTextEmpty(txtfieldPhone, contactNumber, "Enter contact number!");
                etr.editTextEmpty(txtAddressNote, addNote, "Enter address note!");
                //etr.editTextEmpty(txtfieldFirstName, firstName, "Enter First Name!");
                //etr.editTextEmpty(txtfieldFirstName, firstName, "Enter First Name!");

                etr.spinnerDefault(provinceText, province, "Select your province", "Select province");
                etr.spinnerDefault(cityText, city, "Select your city", "Select city");
                etr.spinnerDefault(barangayText, barangay, "Select your barangay", "Select barangay");

                etr.containerVisible(plateNumberContainer, txtPlateNumber, "Enter valid number");
                etr.containerVisible(seatingCapacityContainer, txtSeatingCapacity, "Enter valid number");
                 */

                HashMap map = new HashMap();
                map.put("uid", uid);
                map.put("lastName", lastName);
                map.put("firstName", firstName);
                map.put("contactNumber", contactNumber);

                //map.put("emergencyName", emergencyName);
                //map.put("emergencyNumber", emergencyNumber);

                map.put("ADDRESS/province", selectedProvince);
                map.put("ADDRESS/city", selectedCity);
                map.put("ADDRESS/barangay", selectedBarangay);
                map.put("ADDRESS/streetAddress", addNote);

                map.put("VEHICLE/plateNumber", plateNumber);
                map.put("VEHICLE/capacity", seatingCapacity);
                map.put("VEHICLE/status", "active");

                profileRef.child(infoID).updateChildren(map);

                enableFields(false);
                btnSaveProfile.setVisibility(View.GONE);
                btnEditProfile.setVisibility(View.VISIBLE);

            }
        });

        signOutBtnDriver.setOnClickListener(new View.OnClickListener() {
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

        // Inflate the layout for this fragment
        return view;
    }

    public void enableFields (Boolean isEnabled) {

        txtfieldFirstName.setEnabled(isEnabled);
        txtfieldLastName.setEnabled(isEnabled);
        txtAddressNote.setEnabled(isEnabled);
        txtPlateNumber.setEnabled(isEnabled);
        txtfieldPhone.setEnabled(isEnabled);
        txtSeatingCapacity.setEnabled(isEnabled);

        provinceSpinner.setEnabled(isEnabled);
        citySpinner.setEnabled(isEnabled);
        barangaySpinner.setEnabled(isEnabled);

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
}