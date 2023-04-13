package com.example.appsundo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
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
    TextInputEditText editTextMiddleName;
    TextInputEditText editTextPhoneNumber;
    TextInputEditText editTextEmergencyName;
    TextInputEditText editTextEmergencyNumber;
    TextInputEditText editTextAddNotes;
    TextInputEditText editTextPlateNumber;
    TextInputEditText editTextSeatingCapacity;
    //endregion

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
    FirebaseAuth mAuth;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://sundo-app-44703-default-rtdb.firebaseio.com/");
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_up_form);

        //region declaration of elements

        //EDIT TEXTS
        editTextFirstName = findViewById(R.id.firstName);
        editTextLastName = findViewById(R.id.lastName);
        editTextMiddleName = findViewById(R.id.middleName);
        editTextPhoneNumber = findViewById(R.id.phoneNumber);
        editTextEmergencyName = findViewById(R.id.emergencyName);
        editTextEmergencyNumber = findViewById(R.id.emergencyNumber);
        editTextAddNotes = findViewById(R.id.addressNote);
        editTextPlateNumber = findViewById(R.id.plateNumber);
        editTextSeatingCapacity = findViewById(R.id.seatingCapacity);

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

        //EDIT TEXT FILTERS
        editTextFirstName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        editTextLastName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        editTextMiddleName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        editTextEmergencyName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        editTextAddNotes.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        editTextPhoneNumber.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        editTextEmergencyNumber.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
        editTextSeatingCapacity.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);

        //SPINNER
        provinceSpinner = findViewById(R.id.provinceSpinner);
        provinceAdapter = ArrayAdapter.createFromResource(this, R.array.array_provinces, R.layout.spinner_layout);
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinceSpinner.setAdapter(provinceAdapter);

        //endregion declaration

        mAuth = FirebaseAuth.getInstance();

        // WILL GET THE USER TYPE
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dbRef.child("USERS").child("STUDENT").orderByChild("UID").equalTo(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User is a STUDENT
                    userType = "STUDENT";

                    uidText.setText(userType);

                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        dbRef.child("USERS").child("DRIVER").orderByChild("UID").equalTo(currentUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // User with the given UID exists in DRIVER
                    userType = "DRIVER";

                    uidText.setText(userType);

                    vehicleDetailShow(View.VISIBLE);
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        //region button functions
        submitBtnStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String firstName = String.valueOf(editTextFirstName.getText());
                String lastName = String.valueOf(editTextLastName.getText());
                String middleName = String.valueOf(editTextMiddleName.getText());
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

                if (TextUtils.isEmpty(middleName)) {
                    editTextMiddleName.setError("Enter middle name!");
                    editTextMiddleName.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(contactNumber)) {
                    editTextMiddleName.setError("Enter valid number!");
                    editTextMiddleName.requestFocus();
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


                String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String completeAdd = addNote + " Brgy. " + barangay + ", " + city + ", " + province;

                DatabaseReference userRef = dbRef.child("USERS").child(userType).child(currentUser).child("INFO_ID");
                DatabaseReference recordRef = dbRef.child("USER_INFORMATION").child(userType);

                String requestID = recordRef.push().getKey();

                userRef.setValue(requestID);

                HashMap map = new HashMap();
                map.put("uid", currentUser);
                map.put("lastName", lastName);
                map.put("firstName", firstName);
                map.put("middleName", middleName);
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

                Intent intent;

                if (userType.equals("DRIVER")){
                    intent = new Intent(getApplicationContext(), ContainerDriver.class);

                } else {

                    intent = new Intent(getApplicationContext(), ContainerStudent.class);
                }

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
        //endregion button functions

        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                citySpinner = findViewById(R.id.citySpinner);

                selectedProvince = provinceSpinner.getSelectedItem().toString();

                int parentID = adapterView.getId();
                if(parentID == R.id.provinceSpinner){

                    mapProvinces();

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

                                mapCities();

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

    public void mapCities() {
        cities.put("Select your city", R.array.array_default_barangays);
        cities.put("ADAMS", R.array.array_adams_12801_barangays);
        cities.put("BACARRA", R.array.array_bacarra_12802_barangays);
        cities.put("BADOC", R.array.array_badoc_12803_barangays);
        cities.put("BANGUI", R.array.array_bangui_12804_barangays);
        cities.put("CITY OF BATAC", R.array.array_city_of_batac_12805_barangays);
        cities.put("ILOCOS NORTE-BURGOS", R.array.array_burgos_12806_barangays);
        cities.put("CARASI", R.array.array_carasi_12807_barangays);
        cities.put("CURRIMAO", R.array.array_currimao_12808_barangays);
        cities.put("DINGRAS", R.array.array_dingras_12809_barangays);
        cities.put("DUMALNEG", R.array.array_dumalneg_12810_barangays);
        cities.put("BANNA (ESPIRITU)", R.array.array_banna_espiritu_12811_barangays);
        cities.put("LAOAG CITY (Capital)", R.array.array_laoag_city_capital_12812_barangays);
        cities.put("MARCOS", R.array.array_marcos_12813_barangays);
        cities.put("NUEVA ERA", R.array.array_nueva_era_12814_barangays);
        cities.put("PAGUDPUD", R.array.array_pagudpud_12815_barangays);
        cities.put("PAOAY", R.array.array_paoay_12816_barangays);
        cities.put("PASUQUIN", R.array.array_pasuquin_12817_barangays);
        cities.put("PIDDIG", R.array.array_piddig_12818_barangays);
        cities.put("PINILI", R.array.array_pinili_12819_barangays);
        cities.put("ILOCOS NORTE-SAN NICOLAS", R.array.array_san_nicolas_12820_barangays);
        cities.put("SARRAT", R.array.array_sarrat_12821_barangays);
        cities.put("SOLSONA", R.array.array_solsona_12822_barangays);
        cities.put("VINTAR", R.array.array_vintar_12823_barangays);
        cities.put("ALILEM", R.array.array_alilem_12901_barangays);
        cities.put("BANAYOYO", R.array.array_banayoyo_12902_barangays);
        cities.put("BANTAY", R.array.array_bantay_12903_barangays);
        cities.put("ILOCOS SUR-BURGOS", R.array.array_burgos_12904_barangays);
        cities.put("CABUGAO", R.array.array_cabugao_12905_barangays);
        cities.put("CITY OF CANDON", R.array.array_city_of_candon_12906_barangays);
        cities.put("CAOAYAN", R.array.array_caoayan_12907_barangays);
        cities.put("CERVANTES", R.array.array_cervantes_12908_barangays);
        cities.put("GALIMUYOD", R.array.array_galimuyod_12909_barangays);
        cities.put("GREGORIO DEL PILAR (CONCEPCION)", R.array.array_gregorio_del_pilar_concepcion_12910_barangays);
        cities.put("LIDLIDDA", R.array.array_lidlidda_12911_barangays);
        cities.put("MAGSINGAL", R.array.array_magsingal_12912_barangays);
        cities.put("NAGBUKEL", R.array.array_nagbukel_12913_barangays);
        cities.put("NARVACAN", R.array.array_narvacan_12914_barangays);
        cities.put("QUIRINO (ANGKAKI)", R.array.array_quirino_angkaki_12915_barangays);
        cities.put("SALCEDO (BAUGEN)", R.array.array_salcedo_baugen_12916_barangays);
        cities.put("SAN EMILIO", R.array.array_san_emilio_12917_barangays);
        cities.put("SAN ESTEBAN", R.array.array_san_esteban_12918_barangays);
        cities.put("ILOCOS SUR-SAN ILDEFONSO", R.array.array_san_ildefonso_12919_barangays);
        cities.put("SAN JUAN (LAPOG)", R.array.array_san_juan_lapog_12920_barangays);
        cities.put("ILOCOS SUR-SAN VICENTE", R.array.array_san_vicente_12921_barangays);
        cities.put("SANTA", R.array.array_santa_12922_barangays);
        cities.put("ILOCOS SUR-SANTA CATALINA", R.array.array_santa_catalina_12923_barangays);
        cities.put("ILOCOS SUR-SANTA CRUZ", R.array.array_santa_cruz_12924_barangays);
        cities.put("SANTA LUCIA", R.array.array_santa_lucia_12925_barangays);
        cities.put("ILOCOS SUR-SANTA MARIA", R.array.array_santa_maria_12926_barangays);
        cities.put("ILOCOS SUR-SANTIAGO", R.array.array_santiago_12927_barangays);
        cities.put("ILOCOS SUR-SANTO DOMINGO", R.array.array_santo_domingo_12928_barangays);
        cities.put("SIGAY", R.array.array_sigay_12929_barangays);
        cities.put("SINAIT", R.array.array_sinait_12930_barangays);
        cities.put("SUGPON", R.array.array_sugpon_12931_barangays);
        cities.put("SUYO", R.array.array_suyo_12932_barangays);
        cities.put("TAGUDIN", R.array.array_tagudin_12933_barangays);
        cities.put("CITY OF VIGAN (Capital)", R.array.array_city_of_vigan_capital_12934_barangays);
        cities.put("AGOO", R.array.array_agoo_13301_barangays);
        cities.put("ARINGAY", R.array.array_aringay_13302_barangays);
        cities.put("BACNOTAN", R.array.array_bacnotan_13303_barangays);
        cities.put("BAGULIN", R.array.array_bagulin_13304_barangays);
        cities.put("BALAOAN", R.array.array_balaoan_13305_barangays);
        cities.put("BANGAR", R.array.array_bangar_13306_barangays);
        cities.put("BAUANG", R.array.array_bauang_13307_barangays);
        cities.put("LA UNION-BURGOS", R.array.array_burgos_13308_barangays);
        cities.put("CABA", R.array.array_caba_13309_barangays);
        cities.put("LA UNION-LUNA", R.array.array_luna_13310_barangays);
        cities.put("LA UNION-NAGUILIAN", R.array.array_naguilian_13311_barangays);
        cities.put("PUGO", R.array.array_pugo_13312_barangays);
        cities.put("LA UNION-ROSARIO", R.array.array_rosario_13313_barangays);
        cities.put("LA UNION-CITY OF SAN FERNANDO (Capital)", R.array.array_city_of_san_fernando_capital_13314_barangays);
        cities.put("SAN GABRIEL", R.array.array_san_gabriel_13315_barangays);
        cities.put("LA UNION-SAN JUAN", R.array.array_san_juan_13316_barangays);
        cities.put("LA UNION-SANTO TOMAS", R.array.array_santo_tomas_13317_barangays);
        cities.put("SANTOL", R.array.array_santol_13318_barangays);
        cities.put("SUDIPEN", R.array.array_sudipen_13319_barangays);
        cities.put("TUBAO", R.array.array_tubao_13320_barangays);
        cities.put("AGNO", R.array.array_agno_15501_barangays);
        cities.put("AGUILAR", R.array.array_aguilar_15502_barangays);
        cities.put("CITY OF ALAMINOS", R.array.array_city_of_alaminos_15503_barangays);
        cities.put("PANGASINAN-ALCALA", R.array.array_alcala_15504_barangays);
        cities.put("PANGASINAN-ANDA", R.array.array_anda_15505_barangays);
        cities.put("ASINGAN", R.array.array_asingan_15506_barangays);
        cities.put("BALUNGAO", R.array.array_balungao_15507_barangays);
        cities.put("BANI", R.array.array_bani_15508_barangays);
        cities.put("BASISTA", R.array.array_basista_15509_barangays);
        cities.put("BAUTISTA", R.array.array_bautista_15510_barangays);
        cities.put("BAYAMBANG", R.array.array_bayambang_15511_barangays);
        cities.put("BINALONAN", R.array.array_binalonan_15512_barangays);
        cities.put("BINMALEY", R.array.array_binmaley_15513_barangays);
        cities.put("BOLINAO", R.array.array_bolinao_15514_barangays);
        cities.put("BUGALLON", R.array.array_bugallon_15515_barangays);
        cities.put("PANGASINAN-BURGOS", R.array.array_burgos_15516_barangays);
        cities.put("CALASIAO", R.array.array_calasiao_15517_barangays);
        cities.put("DAGUPAN CITY", R.array.array_dagupan_city_15518_barangays);
        cities.put("DASOL", R.array.array_dasol_15519_barangays);
        cities.put("PANGASINAN-INFANTA", R.array.array_infanta_15520_barangays);
        cities.put("LABRADOR", R.array.array_labrador_15521_barangays);
        cities.put("LINGAYEN (Capital)", R.array.array_lingayen_capital_15522_barangays);
        cities.put("PANGASINAN-MABINI", R.array.array_mabini_15523_barangays);
        cities.put("MALASIQUI", R.array.array_malasiqui_15524_barangays);
        cities.put("MANAOAG", R.array.array_manaoag_15525_barangays);
        cities.put("MANGALDAN", R.array.array_mangaldan_15526_barangays);
        cities.put("MANGATAREM", R.array.array_mangatarem_15527_barangays);
        cities.put("MAPANDAN", R.array.array_mapandan_15528_barangays);
        cities.put("NATIVIDAD", R.array.array_natividad_15529_barangays);
        cities.put("POZORRUBIO", R.array.array_pozorrubio_15530_barangays);
        cities.put("ROSALES", R.array.array_rosales_15531_barangays);
        cities.put("PANGASINAN-SAN CARLOS CITY", R.array.array_san_carlos_city_15532_barangays);
        cities.put("SAN FABIAN", R.array.array_san_fabian_15533_barangays);
        cities.put("PANGASINAN-SAN JACINTO", R.array.array_san_jacinto_15534_barangays);
        cities.put("PANGASINAN-SAN MANUEL", R.array.array_san_manuel_15535_barangays);
        cities.put("PANGASINAN-SAN NICOLAS", R.array.array_san_nicolas_15536_barangays);
        cities.put("PANGASINAN-SAN QUINTIN", R.array.array_san_quintin_15537_barangays);
        cities.put("PANGASINAN-SANTA BARBARA", R.array.array_santa_barbara_15538_barangays);
        cities.put("PANGASINAN-SANTA MARIA", R.array.array_santa_maria_15539_barangays);
        cities.put("PANGASINAN-SANTO TOMAS", R.array.array_santo_tomas_15540_barangays);
        cities.put("PANGASINAN-SISON", R.array.array_sison_15541_barangays);
        cities.put("SUAL", R.array.array_sual_15542_barangays);
        cities.put("TAYUG", R.array.array_tayug_15543_barangays);
        cities.put("UMINGAN", R.array.array_umingan_15544_barangays);
        cities.put("URBIZTONDO", R.array.array_urbiztondo_15545_barangays);
        cities.put("CITY OF URDANETA", R.array.array_city_of_urdaneta_15546_barangays);
        cities.put("VILLASIS", R.array.array_villasis_15547_barangays);
        cities.put("LAOAC", R.array.array_laoac_15548_barangays);
        cities.put("BASCO (Capital)", R.array.array_basco_capital_20901_barangays);
        cities.put("ITBAYAT", R.array.array_itbayat_20902_barangays);
        cities.put("IVANA", R.array.array_ivana_20903_barangays);
        cities.put("MAHATAO", R.array.array_mahatao_20904_barangays);
        cities.put("SABTANG", R.array.array_sabtang_20905_barangays);
        cities.put("UYUGAN", R.array.array_uyugan_20906_barangays);
        cities.put("ABULUG", R.array.array_abulug_21501_barangays);
        cities.put("CAGAYAN-ALCALA", R.array.array_alcala_21502_barangays);
        cities.put("ALLACAPAN", R.array.array_allacapan_21503_barangays);
        cities.put("AMULUNG", R.array.array_amulung_21504_barangays);
        cities.put("APARRI", R.array.array_aparri_21505_barangays);
        cities.put("BAGGAO", R.array.array_baggao_21506_barangays);
        cities.put("BALLESTEROS", R.array.array_ballesteros_21507_barangays);
        cities.put("BUGUEY", R.array.array_buguey_21508_barangays);
        cities.put("CALAYAN", R.array.array_calayan_21509_barangays);
        cities.put("CAMALANIUGAN", R.array.array_camalaniugan_21510_barangays);
        cities.put("CAGAYAN-CLAVERIA", R.array.array_claveria_21511_barangays);
        cities.put("ENRILE", R.array.array_enrile_21512_barangays);
        cities.put("GATTARAN", R.array.array_gattaran_21513_barangays);
        cities.put("GONZAGA", R.array.array_gonzaga_21514_barangays);
        cities.put("IGUIG", R.array.array_iguig_21515_barangays);
        cities.put("LAL-LO", R.array.array_lal_lo_21516_barangays);
        cities.put("LASAM", R.array.array_lasam_21517_barangays);
        cities.put("CAGAYAN-PAMPLONA", R.array.array_pamplona_21518_barangays);
        cities.put("PEÑABLANCA", R.array.array_peñablanca_21519_barangays);
        cities.put("PIAT", R.array.array_piat_21520_barangays);
        cities.put("CAGAYAN-RIZAL", R.array.array_rizal_21521_barangays);
        cities.put("SANCHEZ-MIRA", R.array.array_sanchez_mira_21522_barangays);
        cities.put("CAGAYAN-SANTA ANA", R.array.array_santa_ana_21523_barangays);
        cities.put("SANTA PRAXEDES", R.array.array_santa_praxedes_21524_barangays);
        cities.put("CAGAYAN-SANTA TERESITA", R.array.array_santa_teresita_21525_barangays);
        cities.put("SANTO NIÑO (FAIRE)", R.array.array_santo_niño_faire_21526_barangays);
        cities.put("SOLANA", R.array.array_solana_21527_barangays);
        cities.put("TUAO", R.array.array_tuao_21528_barangays);
        cities.put("TUGUEGARAO CITY (Capital)", R.array.array_tuguegarao_city_capital_21529_barangays);
        cities.put("ISABELA-ALICIA", R.array.array_alicia_23101_barangays);
        cities.put("ANGADANAN", R.array.array_angadanan_23102_barangays);
        cities.put("ISABELA-AURORA", R.array.array_aurora_23103_barangays);
        cities.put("BENITO SOLIVEN", R.array.array_benito_soliven_23104_barangays);
        cities.put("ISABELA-BURGOS", R.array.array_burgos_23105_barangays);
        cities.put("CABAGAN", R.array.array_cabagan_23106_barangays);
        cities.put("ISABELA-CABATUAN", R.array.array_cabatuan_23107_barangays);
        cities.put("CITY OF CAUAYAN", R.array.array_city_of_cauayan_23108_barangays);
        cities.put("CORDON", R.array.array_cordon_23109_barangays);
        cities.put("DINAPIGUE", R.array.array_dinapigue_23110_barangays);
        cities.put("DIVILACAN", R.array.array_divilacan_23111_barangays);
        cities.put("ECHAGUE", R.array.array_echague_23112_barangays);
        cities.put("GAMU", R.array.array_gamu_23113_barangays);
        cities.put("ILAGAN CITY (Capital)", R.array.array_ilagan_city_capital_23114_barangays);
        cities.put("JONES", R.array.array_jones_23115_barangays);
        cities.put("ISABELA-LUNA", R.array.array_luna_23116_barangays);
        cities.put("MACONACON", R.array.array_maconacon_23117_barangays);
        cities.put("DELFIN ALBANO (MAGSAYSAY)", R.array.array_delfin_albano_magsaysay_23118_barangays);
        cities.put("MALLIG", R.array.array_mallig_23119_barangays);
        cities.put("ISABELA-NAGUILIAN", R.array.array_naguilian_23120_barangays);
        cities.put("PALANAN", R.array.array_palanan_23121_barangays);
        cities.put("ISABELA-QUEZON", R.array.array_quezon_23122_barangays);
        cities.put("QUIRINO", R.array.array_quirino_23123_barangays);
        cities.put("RAMON", R.array.array_ramon_23124_barangays);
        cities.put("REINA MERCEDES", R.array.array_reina_mercedes_23125_barangays);
        cities.put("ISABELA-ROXAS", R.array.array_roxas_23126_barangays);
        cities.put("ISABELA-SAN AGUSTIN", R.array.array_san_agustin_23127_barangays);
        cities.put("SAN GUILLERMO", R.array.array_san_guillermo_23128_barangays);
        cities.put("ISABELA-SAN ISIDRO", R.array.array_san_isidro_23129_barangays);
        cities.put("ISABELA-SAN MANUEL", R.array.array_san_manuel_23130_barangays);
        cities.put("SAN MARIANO", R.array.array_san_mariano_23131_barangays);
        cities.put("ISABELA-SAN MATEO", R.array.array_san_mateo_23132_barangays);
        cities.put("ISABELA-SAN PABLO", R.array.array_san_pablo_23133_barangays);
        cities.put("ISABELA-SANTA MARIA", R.array.array_santa_maria_23134_barangays);
        cities.put("CITY OF SANTIAGO", R.array.array_city_of_santiago_23135_barangays);
        cities.put("ISABELA-SANTO TOMAS", R.array.array_santo_tomas_23136_barangays);
        cities.put("TUMAUINI", R.array.array_tumauini_23137_barangays);
        cities.put("AMBAGUIO", R.array.array_ambaguio_25001_barangays);
        cities.put("ARITAO", R.array.array_aritao_25002_barangays);
        cities.put("BAGABAG", R.array.array_bagabag_25003_barangays);
        cities.put("BAMBANG", R.array.array_bambang_25004_barangays);
        cities.put("BAYOMBONG (Capital)", R.array.array_bayombong_capital_25005_barangays);
        cities.put("DIADI", R.array.array_diadi_25006_barangays);
        cities.put("DUPAX DEL NORTE", R.array.array_dupax_del_norte_25007_barangays);
        cities.put("DUPAX DEL SUR", R.array.array_dupax_del_sur_25008_barangays);
        cities.put("KASIBU", R.array.array_kasibu_25009_barangays);
        cities.put("KAYAPA", R.array.array_kayapa_25010_barangays);
        cities.put("NUEVA VIZCAYA-QUEZON", R.array.array_quezon_25011_barangays);
        cities.put("NUEVA VIZCAYA-SANTA FE", R.array.array_santa_fe_25012_barangays);
        cities.put("SOLANO", R.array.array_solano_25013_barangays);
        cities.put("VILLAVERDE", R.array.array_villaverde_25014_barangays);
        cities.put("ALFONSO CASTANEDA", R.array.array_alfonso_castaneda_25015_barangays);
        cities.put("AGLIPAY", R.array.array_aglipay_25701_barangays);
        cities.put("CABARROGUIS (Capital)", R.array.array_cabarroguis_capital_25702_barangays);
        cities.put("DIFFUN", R.array.array_diffun_25703_barangays);
        cities.put("MADDELA", R.array.array_maddela_25704_barangays);
        cities.put("SAGUDAY", R.array.array_saguday_25705_barangays);
        cities.put("NAGTIPUNAN", R.array.array_nagtipunan_25706_barangays);
        cities.put("ABUCAY", R.array.array_abucay_30801_barangays);
        cities.put("BAGAC", R.array.array_bagac_30802_barangays);
        cities.put("CITY OF BALANGA (Capital)", R.array.array_city_of_balanga_capital_30803_barangays);
        cities.put("DINALUPIHAN", R.array.array_dinalupihan_30804_barangays);
        cities.put("HERMOSA", R.array.array_hermosa_30805_barangays);
        cities.put("LIMAY", R.array.array_limay_30806_barangays);
        cities.put("MARIVELES", R.array.array_mariveles_30807_barangays);
        cities.put("BATAAN-MORONG", R.array.array_morong_30808_barangays);
        cities.put("ORANI", R.array.array_orani_30809_barangays);
        cities.put("ORION", R.array.array_orion_30810_barangays);
        cities.put("BATAAN-PILAR", R.array.array_pilar_30811_barangays);
        cities.put("SAMAL", R.array.array_samal_30812_barangays);
        cities.put("ANGAT", R.array.array_angat_31401_barangays);
        cities.put("BALAGTAS (BIGAA)", R.array.array_balagtas_bigaa_31402_barangays);
        cities.put("BALIUAG", R.array.array_baliuag_31403_barangays);
        cities.put("BOCAUE", R.array.array_bocaue_31404_barangays);
        cities.put("BULACAN", R.array.array_bulacan_31405_barangays);
        cities.put("BUSTOS", R.array.array_bustos_31406_barangays);
        cities.put("CALUMPIT", R.array.array_calumpit_31407_barangays);
        cities.put("GUIGUINTO", R.array.array_guiguinto_31408_barangays);
        cities.put("BULACAN-HAGONOY", R.array.array_hagonoy_31409_barangays);
        cities.put("CITY OF MALOLOS (Capital)", R.array.array_city_of_malolos_capital_31410_barangays);
        cities.put("MARILAO", R.array.array_marilao_31411_barangays);
        cities.put("CITY OF MEYCAUAYAN", R.array.array_city_of_meycauayan_31412_barangays);
        cities.put("NORZAGARAY", R.array.array_norzagaray_31413_barangays);
        cities.put("OBANDO", R.array.array_obando_31414_barangays);
        cities.put("PANDI", R.array.array_pandi_31415_barangays);
        cities.put("PAOMBONG", R.array.array_paombong_31416_barangays);
        cities.put("BULACAN-PLARIDEL", R.array.array_plaridel_31417_barangays);
        cities.put("PULILAN", R.array.array_pulilan_31418_barangays);
        cities.put("BULACAN-SAN ILDEFONSO", R.array.array_san_ildefonso_31419_barangays);
        cities.put("CITY OF SAN JOSE DEL MONTE", R.array.array_city_of_san_jose_del_monte_31420_barangays);
        cities.put("BULACAN-SAN MIGUEL", R.array.array_san_miguel_31421_barangays);
        cities.put("BULACAN-SAN RAFAEL", R.array.array_san_rafael_31422_barangays);
        cities.put("BULACAN-SANTA MARIA", R.array.array_santa_maria_31423_barangays);
        cities.put("DOÑA REMEDIOS TRINIDAD", R.array.array_doña_remedios_trinidad_31424_barangays);
        cities.put("ALIAGA", R.array.array_aliaga_34901_barangays);
        cities.put("BONGABON", R.array.array_bongabon_34902_barangays);
        cities.put("CABANATUAN CITY", R.array.array_cabanatuan_city_34903_barangays);
        cities.put("CABIAO", R.array.array_cabiao_34904_barangays);
        cities.put("CARRANGLAN", R.array.array_carranglan_34905_barangays);
        cities.put("CUYAPO", R.array.array_cuyapo_34906_barangays);
        cities.put("GABALDON (BITULOK & SABANI)", R.array.array_gabaldon_bitulok_sabani_34907_barangays);
        cities.put("CITY OF GAPAN", R.array.array_city_of_gapan_34908_barangays);
        cities.put("GENERAL MAMERTO NATIVIDAD", R.array.array_general_mamerto_natividad_34909_barangays);
        cities.put("GENERAL TINIO (PAPAYA)", R.array.array_general_tinio_papaya_34910_barangays);
        cities.put("GUIMBA", R.array.array_guimba_34911_barangays);
        cities.put("JAEN", R.array.array_jaen_34912_barangays);
        cities.put("LAUR", R.array.array_laur_34913_barangays);
        cities.put("LICAB", R.array.array_licab_34914_barangays);
        cities.put("LLANERA", R.array.array_llanera_34915_barangays);
        cities.put("LUPAO", R.array.array_lupao_34916_barangays);
        cities.put("SCIENCE CITY OF MUÑOZ", R.array.array_science_city_of_muñoz_34917_barangays);
        cities.put("NAMPICUAN", R.array.array_nampicuan_34918_barangays);
        cities.put("PALAYAN CITY (Capital)", R.array.array_palayan_city_capital_34919_barangays);
        cities.put("PANTABANGAN", R.array.array_pantabangan_34920_barangays);
        cities.put("PEÑARANDA", R.array.array_peñaranda_34921_barangays);
        cities.put("NUEVA ECIJA-QUEZON", R.array.array_quezon_34922_barangays);
        cities.put("NUEVA ECIJA-RIZAL", R.array.array_rizal_34923_barangays);
        cities.put("NUEVA ECIJA-SAN ANTONIO", R.array.array_san_antonio_34924_barangays);
        cities.put("NUEVA ECIJA-SAN ISIDRO", R.array.array_san_isidro_34925_barangays);
        cities.put("SAN JOSE CITY", R.array.array_san_jose_city_34926_barangays);
        cities.put("SAN LEONARDO", R.array.array_san_leonardo_34927_barangays);
        cities.put("SANTA ROSA", R.array.array_santa_rosa_34928_barangays);
        cities.put("NUEVA ECIJA-SANTO DOMINGO", R.array.array_santo_domingo_34929_barangays);
        cities.put("TALAVERA", R.array.array_talavera_34930_barangays);
        cities.put("TALUGTUG", R.array.array_talugtug_34931_barangays);
        cities.put("ZARAGOZA", R.array.array_zaragoza_34932_barangays);
        cities.put("ANGELES CITY", R.array.array_angeles_city_35401_barangays);
        cities.put("APALIT", R.array.array_apalit_35402_barangays);
        cities.put("ARAYAT", R.array.array_arayat_35403_barangays);
        cities.put("BACOLOR", R.array.array_bacolor_35404_barangays);
        cities.put("CANDABA", R.array.array_candaba_35405_barangays);
        cities.put("FLORIDABLANCA", R.array.array_floridablanca_35406_barangays);
        cities.put("GUAGUA", R.array.array_guagua_35407_barangays);
        cities.put("LUBAO", R.array.array_lubao_35408_barangays);
        cities.put("MABALACAT CITY", R.array.array_mabalacat_city_35409_barangays);
        cities.put("MACABEBE", R.array.array_macabebe_35410_barangays);
        cities.put("MAGALANG", R.array.array_magalang_35411_barangays);
        cities.put("MASANTOL", R.array.array_masantol_35412_barangays);
        cities.put("MEXICO", R.array.array_mexico_35413_barangays);
        cities.put("MINALIN", R.array.array_minalin_35414_barangays);
        cities.put("PORAC", R.array.array_porac_35415_barangays);
        cities.put("PAMPANGA-CITY OF SAN FERNANDO (Capital)", R.array.array_city_of_san_fernando_capital_35416_barangays);
        cities.put("PAMPANGA-SAN LUIS", R.array.array_san_luis_35417_barangays);
        cities.put("SAN SIMON", R.array.array_san_simon_35418_barangays);
        cities.put("PAMPANGA-SANTA ANA", R.array.array_santa_ana_35419_barangays);
        cities.put("PAMPANGA-SANTA RITA", R.array.array_santa_rita_35420_barangays);
        cities.put("PAMPANGA-SANTO TOMAS", R.array.array_santo_tomas_35421_barangays);
        cities.put("SASMUAN (Sexmoan)", R.array.array_sasmuan_sexmoan_35422_barangays);
        cities.put("ANAO", R.array.array_anao_36901_barangays);
        cities.put("BAMBAN", R.array.array_bamban_36902_barangays);
        cities.put("CAMILING", R.array.array_camiling_36903_barangays);
        cities.put("CAPAS", R.array.array_capas_36904_barangays);
        cities.put("TARLAC-CONCEPCION", R.array.array_concepcion_36905_barangays);
        cities.put("GERONA", R.array.array_gerona_36906_barangays);
        cities.put("TARLAC-LA PAZ", R.array.array_la_paz_36907_barangays);
        cities.put("MAYANTOC", R.array.array_mayantoc_36908_barangays);
        cities.put("MONCADA", R.array.array_moncada_36909_barangays);
        cities.put("PANIQUI", R.array.array_paniqui_36910_barangays);
        cities.put("PURA", R.array.array_pura_36911_barangays);
        cities.put("RAMOS", R.array.array_ramos_36912_barangays);
        cities.put("SAN CLEMENTE", R.array.array_san_clemente_36913_barangays);
        cities.put("TARLAC-SAN MANUEL", R.array.array_san_manuel_36914_barangays);
        cities.put("SANTA IGNACIA", R.array.array_santa_ignacia_36915_barangays);
        cities.put("CITY OF TARLAC (Capital)", R.array.array_city_of_tarlac_capital_36916_barangays);
        cities.put("TARLAC-VICTORIA", R.array.array_victoria_36917_barangays);
        cities.put("TARLAC-SAN JOSE", R.array.array_san_jose_36918_barangays);
        cities.put("BOTOLAN", R.array.array_botolan_37101_barangays);
        cities.put("CABANGAN", R.array.array_cabangan_37102_barangays);
        cities.put("ZAMBALES-CANDELARIA", R.array.array_candelaria_37103_barangays);
        cities.put("CASTILLEJOS", R.array.array_castillejos_37104_barangays);
        cities.put("IBA (Capital)", R.array.array_iba_capital_37105_barangays);
        cities.put("MASINLOC", R.array.array_masinloc_37106_barangays);
        cities.put("OLONGAPO CITY", R.array.array_olongapo_city_37107_barangays);
        cities.put("PALAUIG", R.array.array_palauig_37108_barangays);
        cities.put("ZAMBALES-SAN ANTONIO", R.array.array_san_antonio_37109_barangays);
        cities.put("SAN FELIPE", R.array.array_san_felipe_37110_barangays);
        cities.put("SAN MARCELINO", R.array.array_san_marcelino_37111_barangays);
        cities.put("ZAMBALES-SAN NARCISO", R.array.array_san_narciso_37112_barangays);
        cities.put("ZAMBALES-SANTA CRUZ", R.array.array_santa_cruz_37113_barangays);
        cities.put("SUBIC", R.array.array_subic_37114_barangays);
        cities.put("BALER (Capital)", R.array.array_baler_capital_37701_barangays);
        cities.put("AURORA-CASIGURAN", R.array.array_casiguran_37702_barangays);
        cities.put("DILASAG", R.array.array_dilasag_37703_barangays);
        cities.put("DINALUNGAN", R.array.array_dinalungan_37704_barangays);
        cities.put("DINGALAN", R.array.array_dingalan_37705_barangays);
        cities.put("DIPACULAO", R.array.array_dipaculao_37706_barangays);
        cities.put("MARIA AURORA", R.array.array_maria_aurora_37707_barangays);
        cities.put("AURORA-SAN LUIS", R.array.array_san_luis_37708_barangays);
        cities.put("AGONCILLO", R.array.array_agoncillo_41001_barangays);
        cities.put("ALITAGTAG", R.array.array_alitagtag_41002_barangays);
        cities.put("BALAYAN", R.array.array_balayan_41003_barangays);
        cities.put("BATANGAS-BALETE", R.array.array_balete_41004_barangays);
        cities.put("BATANGAS CITY (Capital)", R.array.array_batangas_city_capital_41005_barangays);
        cities.put("BAUAN", R.array.array_bauan_41006_barangays);
        cities.put("CALACA", R.array.array_calaca_41007_barangays);
        cities.put("CALATAGAN", R.array.array_calatagan_41008_barangays);
        cities.put("CUENCA", R.array.array_cuenca_41009_barangays);
        cities.put("IBAAN", R.array.array_ibaan_41010_barangays);
        cities.put("LAUREL", R.array.array_laurel_41011_barangays);
        cities.put("BATANGAS-LEMERY", R.array.array_lemery_41012_barangays);
        cities.put("LIAN", R.array.array_lian_41013_barangays);
        cities.put("LIPA CITY", R.array.array_lipa_city_41014_barangays);
        cities.put("LOBO", R.array.array_lobo_41015_barangays);
        cities.put("BATANGAS-MABINI", R.array.array_mabini_41016_barangays);
        cities.put("MALVAR", R.array.array_malvar_41017_barangays);
        cities.put("MATAASNAKAHOY", R.array.array_mataasnakahoy_41018_barangays);
        cities.put("NASUGBU", R.array.array_nasugbu_41019_barangays);
        cities.put("PADRE GARCIA", R.array.array_padre_garcia_41020_barangays);
        cities.put("BATANGAS-ROSARIO", R.array.array_rosario_41021_barangays);
        cities.put("BATANGAS-SAN JOSE", R.array.array_san_jose_41022_barangays);
        cities.put("BATANGAS-SAN JUAN", R.array.array_san_juan_41023_barangays);
        cities.put("BATANGAS-SAN LUIS", R.array.array_san_luis_41024_barangays);
        cities.put("BATANGAS-SAN NICOLAS", R.array.array_san_nicolas_41025_barangays);
        cities.put("BATANGAS-SAN PASCUAL", R.array.array_san_pascual_41026_barangays);
        cities.put("BATANGAS-SANTA TERESITA", R.array.array_santa_teresita_41027_barangays);
        cities.put("BATANGAS-SANTO TOMAS", R.array.array_santo_tomas_41028_barangays);
        cities.put("TAAL", R.array.array_taal_41029_barangays);
        cities.put("BATANGAS-TALISAY", R.array.array_talisay_41030_barangays);
        cities.put("CITY OF TANAUAN", R.array.array_city_of_tanauan_41031_barangays);
        cities.put("TAYSAN", R.array.array_taysan_41032_barangays);
        cities.put("TINGLOY", R.array.array_tingloy_41033_barangays);
        cities.put("TUY", R.array.array_tuy_41034_barangays);
        cities.put("ALFONSO", R.array.array_alfonso_42101_barangays);
        cities.put("AMADEO", R.array.array_amadeo_42102_barangays);
        cities.put("BACOOR CITY", R.array.array_bacoor_city_42103_barangays);
        cities.put("CARMONA", R.array.array_carmona_42104_barangays);
        cities.put("CAVITE CITY", R.array.array_cavite_city_42105_barangays);
        cities.put("CITY OF DASMARIÑAS", R.array.array_city_of_dasmariñas_42106_barangays);
        cities.put("GENERAL EMILIO AGUINALDO", R.array.array_general_emilio_aguinaldo_42107_barangays);
        cities.put("GENERAL TRIAS", R.array.array_general_trias_42108_barangays);
        cities.put("IMUS CITY", R.array.array_imus_city_42109_barangays);
        cities.put("INDANG", R.array.array_indang_42110_barangays);
        cities.put("KAWIT", R.array.array_kawit_42111_barangays);
        cities.put("CAVITE-MAGALLANES", R.array.array_magallanes_42112_barangays);
        cities.put("MARAGONDON", R.array.array_maragondon_42113_barangays);
        cities.put("MENDEZ (MENDEZ-NUÑEZ)", R.array.array_mendez_mendez_nuñez_42114_barangays);
        cities.put("NAIC", R.array.array_naic_42115_barangays);
        cities.put("NOVELETA", R.array.array_noveleta_42116_barangays);
        cities.put("CAVITE-ROSARIO", R.array.array_rosario_42117_barangays);
        cities.put("SILANG", R.array.array_silang_42118_barangays);
        cities.put("TAGAYTAY CITY", R.array.array_tagaytay_city_42119_barangays);
        cities.put("TANZA", R.array.array_tanza_42120_barangays);
        cities.put("TERNATE", R.array.array_ternate_42121_barangays);
        cities.put("TRECE MARTIRES CITY (Capital)", R.array.array_trece_martires_city_capital_42122_barangays);
        cities.put("GEN. MARIANO ALVAREZ", R.array.array_gen_mariano_alvarez_42123_barangays);
        cities.put("ALAMINOS", R.array.array_alaminos_43401_barangays);
        cities.put("BAY", R.array.array_bay_43402_barangays);
        cities.put("CITY OF BIÑAN", R.array.array_city_of_biñan_43403_barangays);
        cities.put("CABUYAO CITY", R.array.array_cabuyao_city_43404_barangays);
        cities.put("CITY OF CALAMBA", R.array.array_city_of_calamba_43405_barangays);
        cities.put("CALAUAN", R.array.array_calauan_43406_barangays);
        cities.put("CAVINTI", R.array.array_cavinti_43407_barangays);
        cities.put("FAMY", R.array.array_famy_43408_barangays);
        cities.put("LAGUNA-KALAYAAN", R.array.array_kalayaan_43409_barangays);
        cities.put("LILIW", R.array.array_liliw_43410_barangays);
        cities.put("LOS BAÑOS", R.array.array_los_baños_43411_barangays);
        cities.put("LUISIANA", R.array.array_luisiana_43412_barangays);
        cities.put("LUMBAN", R.array.array_lumban_43413_barangays);
        cities.put("MABITAC", R.array.array_mabitac_43414_barangays);
        cities.put("MAGDALENA", R.array.array_magdalena_43415_barangays);
        cities.put("MAJAYJAY", R.array.array_majayjay_43416_barangays);
        cities.put("NAGCARLAN", R.array.array_nagcarlan_43417_barangays);
        cities.put("PAETE", R.array.array_paete_43418_barangays);
        cities.put("PAGSANJAN", R.array.array_pagsanjan_43419_barangays);
        cities.put("PAKIL", R.array.array_pakil_43420_barangays);
        cities.put("PANGIL", R.array.array_pangil_43421_barangays);
        cities.put("PILA", R.array.array_pila_43422_barangays);
        cities.put("LAGUNA-RIZAL", R.array.array_rizal_43423_barangays);
        cities.put("SAN PABLO CITY", R.array.array_san_pablo_city_43424_barangays);
        cities.put("CITY OF SAN PEDRO", R.array.array_city_of_san_pedro_43425_barangays);
        cities.put("SANTA CRUZ (Capital)", R.array.array_santa_cruz_capital_43426_barangays);
        cities.put("LAGUNA-SANTA MARIA", R.array.array_santa_maria_43427_barangays);
        cities.put("CITY OF SANTA ROSA", R.array.array_city_of_santa_rosa_43428_barangays);
        cities.put("SINILOAN", R.array.array_siniloan_43429_barangays);
        cities.put("LAGUNA-VICTORIA", R.array.array_victoria_43430_barangays);
        cities.put("AGDANGAN", R.array.array_agdangan_45601_barangays);
        cities.put("ALABAT", R.array.array_alabat_45602_barangays);
        cities.put("ATIMONAN", R.array.array_atimonan_45603_barangays);
        cities.put("QUEZON-BUENAVISTA", R.array.array_buenavista_45605_barangays);
        cities.put("BURDEOS", R.array.array_burdeos_45606_barangays);
        cities.put("CALAUAG", R.array.array_calauag_45607_barangays);
        cities.put("QUEZON-CANDELARIA", R.array.array_candelaria_45608_barangays);
        cities.put("CATANAUAN", R.array.array_catanauan_45610_barangays);
        cities.put("QUEZON-DOLORES", R.array.array_dolores_45615_barangays);
        cities.put("QUEZON-GENERAL LUNA", R.array.array_general_luna_45616_barangays);
        cities.put("GENERAL NAKAR", R.array.array_general_nakar_45617_barangays);
        cities.put("GUINAYANGAN", R.array.array_guinayangan_45618_barangays);
        cities.put("GUMACA", R.array.array_gumaca_45619_barangays);
        cities.put("QUEZON-INFANTA", R.array.array_infanta_45620_barangays);
        cities.put("JOMALIG", R.array.array_jomalig_45621_barangays);
        cities.put("LOPEZ", R.array.array_lopez_45622_barangays);
        cities.put("LUCBAN", R.array.array_lucban_45623_barangays);
        cities.put("LUCENA CITY (Capital)", R.array.array_lucena_city_capital_45624_barangays);
        cities.put("MACALELON", R.array.array_macalelon_45625_barangays);
        cities.put("MAUBAN", R.array.array_mauban_45627_barangays);
        cities.put("MULANAY", R.array.array_mulanay_45628_barangays);
        cities.put("QUEZON-PADRE BURGOS", R.array.array_padre_burgos_45629_barangays);
        cities.put("PAGBILAO", R.array.array_pagbilao_45630_barangays);
        cities.put("PANUKULAN", R.array.array_panukulan_45631_barangays);
        cities.put("PATNANUNGAN", R.array.array_patnanungan_45632_barangays);
        cities.put("PEREZ", R.array.array_perez_45633_barangays);
        cities.put("QUEZON-PITOGO", R.array.array_pitogo_45634_barangays);
        cities.put("QUEZON-PLARIDEL", R.array.array_plaridel_45635_barangays);
        cities.put("POLILLO", R.array.array_polillo_45636_barangays);
        cities.put("QUEZON-QUEZON", R.array.array_quezon_45637_barangays);
        cities.put("REAL", R.array.array_real_45638_barangays);
        cities.put("QUEZON-SAMPALOC", R.array.array_sampaloc_45639_barangays);
        cities.put("QUEZON-SAN ANDRES", R.array.array_san_andres_45640_barangays);
        cities.put("QUEZON-SAN ANTONIO", R.array.array_san_antonio_45641_barangays);
        cities.put("SAN FRANCISCO (AURORA)", R.array.array_san_francisco_aurora_45642_barangays);
        cities.put("QUEZON-SAN NARCISO", R.array.array_san_narciso_45644_barangays);
        cities.put("SARIAYA", R.array.array_sariaya_45645_barangays);
        cities.put("TAGKAWAYAN", R.array.array_tagkawayan_45646_barangays);
        cities.put("CITY OF TAYABAS", R.array.array_city_of_tayabas_45647_barangays);
        cities.put("TIAONG", R.array.array_tiaong_45648_barangays);
        cities.put("UNISAN", R.array.array_unisan_45649_barangays);
        cities.put("ANGONO", R.array.array_angono_45801_barangays);
        cities.put("CITY OF ANTIPOLO", R.array.array_city_of_antipolo_45802_barangays);
        cities.put("RIZAL-BARAS", R.array.array_baras_45803_barangays);
        cities.put("BINANGONAN", R.array.array_binangonan_45804_barangays);
        cities.put("CAINTA", R.array.array_cainta_45805_barangays);
        cities.put("CARDONA", R.array.array_cardona_45806_barangays);
        cities.put("JALA-JALA", R.array.array_jala_jala_45807_barangays);
        cities.put("RODRIGUEZ (MONTALBAN)", R.array.array_rodriguez_montalban_45808_barangays);
        cities.put("RIZAL-MORONG", R.array.array_morong_45809_barangays);
        cities.put("PILILLA", R.array.array_pililla_45810_barangays);
        cities.put("RIZAL-SAN MATEO", R.array.array_san_mateo_45811_barangays);
        cities.put("TANAY", R.array.array_tanay_45812_barangays);
        cities.put("RIZAL-TAYTAY", R.array.array_taytay_45813_barangays);
        cities.put("TERESA", R.array.array_teresa_45814_barangays);
        cities.put("BOAC (Capital)", R.array.array_boac_capital_174001_barangays);
        cities.put("MARINDUQUE-BUENAVISTA", R.array.array_buenavista_174002_barangays);
        cities.put("GASAN", R.array.array_gasan_174003_barangays);
        cities.put("MOGPOG", R.array.array_mogpog_174004_barangays);
        cities.put("MARINDUQUE-SANTA CRUZ", R.array.array_santa_cruz_174005_barangays);
        cities.put("TORRIJOS", R.array.array_torrijos_174006_barangays);
        cities.put("ABRA DE ILOG", R.array.array_abra_de_ilog_175101_barangays);
        cities.put("CALINTAAN", R.array.array_calintaan_175102_barangays);
        cities.put("OCCIDENTAL MINDORO-LOOC", R.array.array_looc_175103_barangays);
        cities.put("LUBANG", R.array.array_lubang_175104_barangays);
        cities.put("OCCIDENTAL MINDORO-MAGSAYSAY", R.array.array_magsaysay_175105_barangays);
        cities.put("MAMBURAO (Capital)", R.array.array_mamburao_capital_175106_barangays);
        cities.put("PALUAN", R.array.array_paluan_175107_barangays);
        cities.put("OCCIDENTAL MINDORO-RIZAL", R.array.array_rizal_175108_barangays);
        cities.put("SABLAYAN", R.array.array_sablayan_175109_barangays);
        cities.put("OCCIDENTAL MINDORO-SAN JOSE", R.array.array_san_jose_175110_barangays);
        cities.put("OCCIDENTAL MINDORO-SANTA CRUZ", R.array.array_santa_cruz_175111_barangays);
        cities.put("BACO", R.array.array_baco_175201_barangays);
        cities.put("BANSUD", R.array.array_bansud_175202_barangays);
        cities.put("BONGABONG", R.array.array_bongabong_175203_barangays);
        cities.put("BULALACAO (SAN PEDRO)", R.array.array_bulalacao_san_pedro_175204_barangays);
        cities.put("CITY OF CALAPAN (Capital)", R.array.array_city_of_calapan_capital_175205_barangays);
        cities.put("GLORIA", R.array.array_gloria_175206_barangays);
        cities.put("MANSALAY", R.array.array_mansalay_175207_barangays);
        cities.put("NAUJAN", R.array.array_naujan_175208_barangays);
        cities.put("PINAMALAYAN", R.array.array_pinamalayan_175209_barangays);
        cities.put("POLA", R.array.array_pola_175210_barangays);
        cities.put("PUERTO GALERA", R.array.array_puerto_galera_175211_barangays);
        cities.put("ORIENTAL MINDORO-ROXAS", R.array.array_roxas_175212_barangays);
        cities.put("SAN TEODORO", R.array.array_san_teodoro_175213_barangays);
        cities.put("ORIENTAL MINDORO-SOCORRO", R.array.array_socorro_175214_barangays);
        cities.put("ORIENTAL MINDORO-VICTORIA", R.array.array_victoria_175215_barangays);
        cities.put("ABORLAN", R.array.array_aborlan_175301_barangays);
        cities.put("AGUTAYA", R.array.array_agutaya_175302_barangays);
        cities.put("ARACELI", R.array.array_araceli_175303_barangays);
        cities.put("BALABAC", R.array.array_balabac_175304_barangays);
        cities.put("BATARAZA", R.array.array_bataraza_175305_barangays);
        cities.put("BROOKE S POINT", R.array.array_brookes_point_175306_barangays);
        cities.put("BUSUANGA", R.array.array_busuanga_175307_barangays);
        cities.put("CAGAYANCILLO", R.array.array_cagayancillo_175308_barangays);
        cities.put("CORON", R.array.array_coron_175309_barangays);
        cities.put("CUYO", R.array.array_cuyo_175310_barangays);
        cities.put("DUMARAN", R.array.array_dumaran_175311_barangays);
        cities.put("EL NIDO (BACUIT)", R.array.array_el_nido_bacuit_175312_barangays);
        cities.put("LINAPACAN", R.array.array_linapacan_175313_barangays);
        cities.put("PALAWAN-MAGSAYSAY", R.array.array_magsaysay_175314_barangays);
        cities.put("NARRA", R.array.array_narra_175315_barangays);
        cities.put("PUERTO PRINCESA CITY (Capital)", R.array.array_puerto_princesa_city_capital_175316_barangays);
        cities.put("PALAWAN-QUEZON", R.array.array_quezon_175317_barangays);
        cities.put("PALAWAN-ROXAS", R.array.array_roxas_175318_barangays);
        cities.put("PALAWAN-SAN VICENTE", R.array.array_san_vicente_175319_barangays);
        cities.put("PALAWAN-TAYTAY", R.array.array_taytay_175320_barangays);
        cities.put("PALAWAN-KALAYAAN", R.array.array_kalayaan_175321_barangays);
        cities.put("CULION", R.array.array_culion_175322_barangays);
        cities.put("RIZAL (MARCOS)", R.array.array_rizal_marcos_175323_barangays);
        cities.put("SOFRONIO ESPAÑOLA", R.array.array_sofronio_española_175324_barangays);
        cities.put("ROMBLON-ALCANTARA", R.array.array_alcantara_175901_barangays);
        cities.put("BANTON", R.array.array_banton_175902_barangays);
        cities.put("CAJIDIOCAN", R.array.array_cajidiocan_175903_barangays);
        cities.put("ROMBLON-CALATRAVA", R.array.array_calatrava_175904_barangays);
        cities.put("ROMBLON-CONCEPCION", R.array.array_concepcion_175905_barangays);
        cities.put("CORCUERA", R.array.array_corcuera_175906_barangays);
        cities.put("ROMBLON-LOOC", R.array.array_looc_175907_barangays);
        cities.put("MAGDIWANG", R.array.array_magdiwang_175908_barangays);
        cities.put("ODIONGAN", R.array.array_odiongan_175909_barangays);
        cities.put("ROMBLON (Capital)", R.array.array_romblon_capital_175910_barangays);
        cities.put("ROMBLON-SAN AGUSTIN", R.array.array_san_agustin_175911_barangays);
        cities.put("ROMBLON-SAN ANDRES", R.array.array_san_andres_175912_barangays);
        cities.put("ROMBLON-SAN FERNANDO", R.array.array_san_fernando_175913_barangays);
        cities.put("ROMBLON-SAN JOSE", R.array.array_san_jose_175914_barangays);
        cities.put("ROMBLON-SANTA FE", R.array.array_santa_fe_175915_barangays);
        cities.put("FERROL", R.array.array_ferrol_175916_barangays);
        cities.put("SANTA MARIA (IMELDA)", R.array.array_santa_maria_imelda_175917_barangays);
        cities.put("BACACAY", R.array.array_bacacay_50501_barangays);
        cities.put("CAMALIG", R.array.array_camalig_50502_barangays);
        cities.put("DARAGA (LOCSIN)", R.array.array_daraga_locsin_50503_barangays);
        cities.put("GUINOBATAN", R.array.array_guinobatan_50504_barangays);
        cities.put("JOVELLAR", R.array.array_jovellar_50505_barangays);
        cities.put("LEGAZPI CITY (Capital)", R.array.array_legazpi_city_capital_50506_barangays);
        cities.put("LIBON", R.array.array_libon_50507_barangays);
        cities.put("CITY OF LIGAO", R.array.array_city_of_ligao_50508_barangays);
        cities.put("MALILIPOT", R.array.array_malilipot_50509_barangays);
        cities.put("ALBAY-MALINAO", R.array.array_malinao_50510_barangays);
        cities.put("MANITO", R.array.array_manito_50511_barangays);
        cities.put("OAS", R.array.array_oas_50512_barangays);
        cities.put("PIO DURAN", R.array.array_pio_duran_50513_barangays);
        cities.put("POLANGUI", R.array.array_polangui_50514_barangays);
        cities.put("RAPU-RAPU", R.array.array_rapu_rapu_50515_barangays);
        cities.put("SANTO DOMINGO (LIBOG)", R.array.array_santo_domingo_libog_50516_barangays);
        cities.put("CITY OF TABACO", R.array.array_city_of_tabaco_50517_barangays);
        cities.put("TIWI", R.array.array_tiwi_50518_barangays);
        cities.put("BASUD", R.array.array_basud_51601_barangays);
        cities.put("CAPALONGA", R.array.array_capalonga_51602_barangays);
        cities.put("DAET (Capital)", R.array.array_daet_capital_51603_barangays);
        cities.put("SAN LORENZO RUIZ (IMELDA)", R.array.array_san_lorenzo_ruiz_imelda_51604_barangays);
        cities.put("JOSE PANGANIBAN", R.array.array_jose_panganiban_51605_barangays);
        cities.put("LABO", R.array.array_labo_51606_barangays);
        cities.put("CAMARINES NORTE-MERCEDES", R.array.array_mercedes_51607_barangays);
        cities.put("PARACALE", R.array.array_paracale_51608_barangays);
        cities.put("CAMARINES NORTE-SAN VICENTE", R.array.array_san_vicente_51609_barangays);
        cities.put("SANTA ELENA", R.array.array_santa_elena_51610_barangays);
        cities.put("CAMARINES NORTE-TALISAY", R.array.array_talisay_51611_barangays);
        cities.put("VINZONS", R.array.array_vinzons_51612_barangays);
        cities.put("BAAO", R.array.array_baao_51701_barangays);
        cities.put("BALATAN", R.array.array_balatan_51702_barangays);
        cities.put("CAMARINES SUR-BATO", R.array.array_bato_51703_barangays);
        cities.put("BOMBON", R.array.array_bombon_51704_barangays);
        cities.put("BUHI", R.array.array_buhi_51705_barangays);
        cities.put("BULA", R.array.array_bula_51706_barangays);
        cities.put("CABUSAO", R.array.array_cabusao_51707_barangays);
        cities.put("CALABANGA", R.array.array_calabanga_51708_barangays);
        cities.put("CAMALIGAN", R.array.array_camaligan_51709_barangays);
        cities.put("CANAMAN", R.array.array_canaman_51710_barangays);
        cities.put("CARAMOAN", R.array.array_caramoan_51711_barangays);
        cities.put("DEL GALLEGO", R.array.array_del_gallego_51712_barangays);
        cities.put("GAINZA", R.array.array_gainza_51713_barangays);
        cities.put("GARCHITORENA", R.array.array_garchitorena_51714_barangays);
        cities.put("GOA", R.array.array_goa_51715_barangays);
        cities.put("IRIGA CITY", R.array.array_iriga_city_51716_barangays);
        cities.put("LAGONOY", R.array.array_lagonoy_51717_barangays);
        cities.put("LIBMANAN", R.array.array_libmanan_51718_barangays);
        cities.put("LUPI", R.array.array_lupi_51719_barangays);
        cities.put("MAGARAO", R.array.array_magarao_51720_barangays);
        cities.put("MILAOR", R.array.array_milaor_51721_barangays);
        cities.put("MINALABAC", R.array.array_minalabac_51722_barangays);
        cities.put("NABUA", R.array.array_nabua_51723_barangays);
        cities.put("NAGA CITY", R.array.array_naga_city_51724_barangays);
        cities.put("OCAMPO", R.array.array_ocampo_51725_barangays);
        cities.put("CAMARINES SUR-PAMPLONA", R.array.array_pamplona_51726_barangays);
        cities.put("PASACAO", R.array.array_pasacao_51727_barangays);
        cities.put("PILI (Capital)", R.array.array_pili_capital_51728_barangays);
        cities.put("PRESENTACION (PARUBCAN)", R.array.array_presentacion_parubcan_51729_barangays);
        cities.put("RAGAY", R.array.array_ragay_51730_barangays);
        cities.put("SAGÑAY", R.array.array_sagñay_51731_barangays);
        cities.put("CAMARINES SUR-SAN FERNANDO", R.array.array_san_fernando_51732_barangays);
        cities.put("CAMARINES SUR-SAN JOSE", R.array.array_san_jose_51733_barangays);
        cities.put("SIPOCOT", R.array.array_sipocot_51734_barangays);
        cities.put("SIRUMA", R.array.array_siruma_51735_barangays);
        cities.put("TIGAON", R.array.array_tigaon_51736_barangays);
        cities.put("TINAMBAC", R.array.array_tinambac_51737_barangays);
        cities.put("BAGAMANOC", R.array.array_bagamanoc_52001_barangays);
        cities.put("CATANDUANES-BARAS", R.array.array_baras_52002_barangays);
        cities.put("CATANDUANES-BATO", R.array.array_bato_52003_barangays);
        cities.put("CARAMORAN", R.array.array_caramoran_52004_barangays);
        cities.put("GIGMOTO", R.array.array_gigmoto_52005_barangays);
        cities.put("CATANDUANES-PANDAN", R.array.array_pandan_52006_barangays);
        cities.put("PANGANIBAN (PAYO)", R.array.array_panganiban_payo_52007_barangays);
        cities.put("SAN ANDRES (CALOLBON)", R.array.array_san_andres_calolbon_52008_barangays);
        cities.put("CATANDUANES-SAN MIGUEL", R.array.array_san_miguel_52009_barangays);
        cities.put("VIGA", R.array.array_viga_52010_barangays);
        cities.put("VIRAC (Capital)", R.array.array_virac_capital_52011_barangays);
        cities.put("AROROY", R.array.array_aroroy_54101_barangays);
        cities.put("BALENO", R.array.array_baleno_54102_barangays);
        cities.put("BALUD", R.array.array_balud_54103_barangays);
        cities.put("MASBATE-BATUAN", R.array.array_batuan_54104_barangays);
        cities.put("CATAINGAN", R.array.array_cataingan_54105_barangays);
        cities.put("CAWAYAN", R.array.array_cawayan_54106_barangays);
        cities.put("MASBATE-CLAVERIA", R.array.array_claveria_54107_barangays);
        cities.put("DIMASALANG", R.array.array_dimasalang_54108_barangays);
        cities.put("MASBATE-ESPERANZA", R.array.array_esperanza_54109_barangays);
        cities.put("MANDAON", R.array.array_mandaon_54110_barangays);
        cities.put("CITY OF MASBATE (Capital)", R.array.array_city_of_masbate_capital_54111_barangays);
        cities.put("MILAGROS", R.array.array_milagros_54112_barangays);
        cities.put("MOBO", R.array.array_mobo_54113_barangays);
        cities.put("MONREAL", R.array.array_monreal_54114_barangays);
        cities.put("PALANAS", R.array.array_palanas_54115_barangays);
        cities.put("PIO V. CORPUZ (LIMBUHAN)", R.array.array_pio_v_corpuz_limbuhan_54116_barangays);
        cities.put("MASBATE-PLACER", R.array.array_placer_54117_barangays);
        cities.put("MASBATE-SAN FERNANDO", R.array.array_san_fernando_54118_barangays);
        cities.put("MASBATE-SAN JACINTO", R.array.array_san_jacinto_54119_barangays);
        cities.put("MASBATE-SAN PASCUAL", R.array.array_san_pascual_54120_barangays);
        cities.put("USON", R.array.array_uson_54121_barangays);
        cities.put("BARCELONA", R.array.array_barcelona_56202_barangays);
        cities.put("BULAN", R.array.array_bulan_56203_barangays);
        cities.put("BULUSAN", R.array.array_bulusan_56204_barangays);
        cities.put("SORSOGON-CASIGURAN", R.array.array_casiguran_56205_barangays);
        cities.put("CASTILLA", R.array.array_castilla_56206_barangays);
        cities.put("DONSOL", R.array.array_donsol_56207_barangays);
        cities.put("GUBAT", R.array.array_gubat_56208_barangays);
        cities.put("IROSIN", R.array.array_irosin_56209_barangays);
        cities.put("JUBAN", R.array.array_juban_56210_barangays);
        cities.put("SORSOGON-MAGALLANES", R.array.array_magallanes_56211_barangays);
        cities.put("MATNOG", R.array.array_matnog_56212_barangays);
        cities.put("SORSOGON-PILAR", R.array.array_pilar_56213_barangays);
        cities.put("PRIETO DIAZ", R.array.array_prieto_diaz_56214_barangays);
        cities.put("SANTA MAGDALENA", R.array.array_santa_magdalena_56215_barangays);
        cities.put("CITY OF SORSOGON (Capital)", R.array.array_city_of_sorsogon_capital_56216_barangays);
        cities.put("ALTAVAS", R.array.array_altavas_60401_barangays);
        cities.put("AKLAN-BALETE", R.array.array_balete_60402_barangays);
        cities.put("AKLAN-BANGA", R.array.array_banga_60403_barangays);
        cities.put("BATAN", R.array.array_batan_60404_barangays);
        cities.put("BURUANGA", R.array.array_buruanga_60405_barangays);
        cities.put("IBAJAY", R.array.array_ibajay_60406_barangays);
        cities.put("KALIBO (Capital)", R.array.array_kalibo_capital_60407_barangays);
        cities.put("LEZO", R.array.array_lezo_60408_barangays);
        cities.put("LIBACAO", R.array.array_libacao_60409_barangays);
        cities.put("MADALAG", R.array.array_madalag_60410_barangays);
        cities.put("MAKATO", R.array.array_makato_60411_barangays);
        cities.put("MALAY", R.array.array_malay_60412_barangays);
        cities.put("AKLAN-MALINAO", R.array.array_malinao_60413_barangays);
        cities.put("NABAS", R.array.array_nabas_60414_barangays);
        cities.put("NEW WASHINGTON", R.array.array_new_washington_60415_barangays);
        cities.put("NUMANCIA", R.array.array_numancia_60416_barangays);
        cities.put("TANGALAN", R.array.array_tangalan_60417_barangays);
        cities.put("ANINI-Y", R.array.array_anini_y_60601_barangays);
        cities.put("BARBAZA", R.array.array_barbaza_60602_barangays);
        cities.put("BELISON", R.array.array_belison_60603_barangays);
        cities.put("BUGASONG", R.array.array_bugasong_60604_barangays);
        cities.put("CALUYA", R.array.array_caluya_60605_barangays);
        cities.put("CULASI", R.array.array_culasi_60606_barangays);
        cities.put("TOBIAS FORNIER (DAO)", R.array.array_tobias_fornier_dao_60607_barangays);
        cities.put("HAMTIC", R.array.array_hamtic_60608_barangays);
        cities.put("LAUA-AN", R.array.array_laua_an_60609_barangays);
        cities.put("ANTIQUE-LIBERTAD", R.array.array_libertad_60610_barangays);
        cities.put("ANTIQUE-PANDAN", R.array.array_pandan_60611_barangays);
        cities.put("PATNONGON", R.array.array_patnongon_60612_barangays);
        cities.put("ANTIQUE-SAN JOSE (Capital)", R.array.array_san_jose_capital_60613_barangays);
        cities.put("ANTIQUE-SAN REMIGIO", R.array.array_san_remigio_60614_barangays);
        cities.put("SEBASTE", R.array.array_sebaste_60615_barangays);
        cities.put("SIBALOM", R.array.array_sibalom_60616_barangays);
        cities.put("TIBIAO", R.array.array_tibiao_60617_barangays);
        cities.put("VALDERRAMA", R.array.array_valderrama_60618_barangays);
        cities.put("CUARTERO", R.array.array_cuartero_61901_barangays);
        cities.put("DAO", R.array.array_dao_61902_barangays);
        cities.put("DUMALAG", R.array.array_dumalag_61903_barangays);
        cities.put("DUMARAO", R.array.array_dumarao_61904_barangays);
        cities.put("IVISAN", R.array.array_ivisan_61905_barangays);
        cities.put("JAMINDAN", R.array.array_jamindan_61906_barangays);
        cities.put("MA-AYON", R.array.array_ma_ayon_61907_barangays);
        cities.put("MAMBUSAO", R.array.array_mambusao_61908_barangays);
        cities.put("PANAY", R.array.array_panay_61909_barangays);
        cities.put("PANITAN", R.array.array_panitan_61910_barangays);
        cities.put("CAPIZ-PILAR", R.array.array_pilar_61911_barangays);
        cities.put("CAPIZ-PONTEVEDRA", R.array.array_pontevedra_61912_barangays);
        cities.put("CAPIZ-PRESIDENT ROXAS", R.array.array_president_roxas_61913_barangays);
        cities.put("ROXAS CITY (Capital)", R.array.array_roxas_city_capital_61914_barangays);
        cities.put("SAPI-AN", R.array.array_sapi_an_61915_barangays);
        cities.put("SIGMA", R.array.array_sigma_61916_barangays);
        cities.put("TAPAZ", R.array.array_tapaz_61917_barangays);
        cities.put("AJUY", R.array.array_ajuy_63001_barangays);
        cities.put("ALIMODIAN", R.array.array_alimodian_63002_barangays);
        cities.put("ANILAO", R.array.array_anilao_63003_barangays);
        cities.put("BADIANGAN", R.array.array_badiangan_63004_barangays);
        cities.put("BALASAN", R.array.array_balasan_63005_barangays);
        cities.put("BANATE", R.array.array_banate_63006_barangays);
        cities.put("BAROTAC NUEVO", R.array.array_barotac_nuevo_63007_barangays);
        cities.put("BAROTAC VIEJO", R.array.array_barotac_viejo_63008_barangays);
        cities.put("BATAD", R.array.array_batad_63009_barangays);
        cities.put("BINGAWAN", R.array.array_bingawan_63010_barangays);
        cities.put("ILOILO-CABATUAN", R.array.array_cabatuan_63012_barangays);
        cities.put("CALINOG", R.array.array_calinog_63013_barangays);
        cities.put("CARLES", R.array.array_carles_63014_barangays);
        cities.put("ILOILO-CONCEPCION", R.array.array_concepcion_63015_barangays);
        cities.put("DINGLE", R.array.array_dingle_63016_barangays);
        cities.put("DUEÑAS", R.array.array_dueñas_63017_barangays);
        cities.put("DUMANGAS", R.array.array_dumangas_63018_barangays);
        cities.put("ESTANCIA", R.array.array_estancia_63019_barangays);
        cities.put("GUIMBAL", R.array.array_guimbal_63020_barangays);
        cities.put("IGBARAS", R.array.array_igbaras_63021_barangays);
        cities.put("ILOILO CITY (Capital)", R.array.array_iloilo_city_capital_63022_barangays);
        cities.put("JANIUAY", R.array.array_janiuay_63023_barangays);
        cities.put("LAMBUNAO", R.array.array_lambunao_63025_barangays);
        cities.put("LEGANES", R.array.array_leganes_63026_barangays);
        cities.put("ILOILO-LEMERY", R.array.array_lemery_63027_barangays);
        cities.put("LEON", R.array.array_leon_63028_barangays);
        cities.put("MAASIN", R.array.array_maasin_63029_barangays);
        cities.put("MIAGAO", R.array.array_miagao_63030_barangays);
        cities.put("MINA", R.array.array_mina_63031_barangays);
        cities.put("NEW LUCENA", R.array.array_new_lucena_63032_barangays);
        cities.put("OTON", R.array.array_oton_63034_barangays);
        cities.put("CITY OF PASSI", R.array.array_city_of_passi_63035_barangays);
        cities.put("PAVIA", R.array.array_pavia_63036_barangays);
        cities.put("POTOTAN", R.array.array_pototan_63037_barangays);
        cities.put("SAN DIONISIO", R.array.array_san_dionisio_63038_barangays);
        cities.put("ILOILO-SAN ENRIQUE", R.array.array_san_enrique_63039_barangays);
        cities.put("SAN JOAQUIN", R.array.array_san_joaquin_63040_barangays);
        cities.put("ILOILO-SAN MIGUEL", R.array.array_san_miguel_63041_barangays);
        cities.put("ILOILO-SAN RAFAEL", R.array.array_san_rafael_63042_barangays);
        cities.put("ILOILO-SANTA BARBARA", R.array.array_santa_barbara_63043_barangays);
        cities.put("SARA", R.array.array_sara_63044_barangays);
        cities.put("TIGBAUAN", R.array.array_tigbauan_63045_barangays);
        cities.put("TUBUNGAN", R.array.array_tubungan_63046_barangays);
        cities.put("ZARRAGA", R.array.array_zarraga_63047_barangays);
        cities.put("BACOLOD CITY (Capital)", R.array.array_bacolod_city_capital_64501_barangays);
        cities.put("BAGO CITY", R.array.array_bago_city_64502_barangays);
        cities.put("BINALBAGAN", R.array.array_binalbagan_64503_barangays);
        cities.put("CADIZ CITY", R.array.array_cadiz_city_64504_barangays);
        cities.put("NEGROS OCCIDENTAL-CALATRAVA", R.array.array_calatrava_64505_barangays);
        cities.put("CANDONI", R.array.array_candoni_64506_barangays);
        cities.put("CAUAYAN", R.array.array_cauayan_64507_barangays);
        cities.put("ENRIQUE B. MAGALONA (SARAVIA)", R.array.array_enrique_b_magalona_saravia_64508_barangays);
        cities.put("CITY OF ESCALANTE", R.array.array_city_of_escalante_64509_barangays);
        cities.put("CITY OF HIMAMAYLAN", R.array.array_city_of_himamaylan_64510_barangays);
        cities.put("HINIGARAN", R.array.array_hinigaran_64511_barangays);
        cities.put("HINOBA-AN (ASIA)", R.array.array_hinoba_an_asia_64512_barangays);
        cities.put("ILOG", R.array.array_ilog_64513_barangays);
        cities.put("ISABELA", R.array.array_isabela_64514_barangays);
        cities.put("CITY OF KABANKALAN", R.array.array_city_of_kabankalan_64515_barangays);
        cities.put("LA CARLOTA CITY", R.array.array_la_carlota_city_64516_barangays);
        cities.put("LA CASTELLANA", R.array.array_la_castellana_64517_barangays);
        cities.put("MANAPLA", R.array.array_manapla_64518_barangays);
        cities.put("MOISES PADILLA (MAGALLON)", R.array.array_moises_padilla_magallon_64519_barangays);
        cities.put("MURCIA", R.array.array_murcia_64520_barangays);
        cities.put("NEGROS OCCIDENTAL-PONTEVEDRA", R.array.array_pontevedra_64521_barangays);
        cities.put("PULUPANDAN", R.array.array_pulupandan_64522_barangays);
        cities.put("SAGAY CITY", R.array.array_sagay_city_64523_barangays);
        cities.put("NEGROS OCCIDENTAL-SAN CARLOS CITY", R.array.array_san_carlos_city_64524_barangays);
        cities.put("NEGROS OCCIDENTAL-SAN ENRIQUE", R.array.array_san_enrique_64525_barangays);
        cities.put("SILAY CITY", R.array.array_silay_city_64526_barangays);
        cities.put("CITY OF SIPALAY", R.array.array_city_of_sipalay_64527_barangays);
        cities.put("NEGROS OCCIDENTAL-CITY OF TALISAY", R.array.array_city_of_talisay_64528_barangays);
        cities.put("TOBOSO", R.array.array_toboso_64529_barangays);
        cities.put("VALLADOLID", R.array.array_valladolid_64530_barangays);
        cities.put("CITY OF VICTORIAS", R.array.array_city_of_victorias_64531_barangays);
        cities.put("SALVADOR BENEDICTO", R.array.array_salvador_benedicto_64532_barangays);
        cities.put("GUIMARAS-BUENAVISTA", R.array.array_buenavista_67901_barangays);
        cities.put("JORDAN (Capital)", R.array.array_jordan_capital_67902_barangays);
        cities.put("NUEVA VALENCIA", R.array.array_nueva_valencia_67903_barangays);
        cities.put("SAN LORENZO", R.array.array_san_lorenzo_67904_barangays);
        cities.put("SIBUNAG", R.array.array_sibunag_67905_barangays);
        cities.put("ALBURQUERQUE", R.array.array_alburquerque_71201_barangays);
        cities.put("BOHOL-ALICIA", R.array.array_alicia_71202_barangays);
        cities.put("BOHOL-ANDA", R.array.array_anda_71203_barangays);
        cities.put("ANTEQUERA", R.array.array_antequera_71204_barangays);
        cities.put("BACLAYON", R.array.array_baclayon_71205_barangays);
        cities.put("BALILIHAN", R.array.array_balilihan_71206_barangays);
        cities.put("BOHOL-BATUAN", R.array.array_batuan_71207_barangays);
        cities.put("BILAR", R.array.array_bilar_71208_barangays);
        cities.put("BOHOL-BUENAVISTA", R.array.array_buenavista_71209_barangays);
        cities.put("CALAPE", R.array.array_calape_71210_barangays);
        cities.put("CANDIJAY", R.array.array_candijay_71211_barangays);
        cities.put("BOHOL-CARMEN", R.array.array_carmen_71212_barangays);
        cities.put("CATIGBIAN", R.array.array_catigbian_71213_barangays);
        cities.put("BOHOL-CLARIN", R.array.array_clarin_71214_barangays);
        cities.put("CORELLA", R.array.array_corella_71215_barangays);
        cities.put("BOHOL-CORTES", R.array.array_cortes_71216_barangays);
        cities.put("DAGOHOY", R.array.array_dagohoy_71217_barangays);
        cities.put("DANAO", R.array.array_danao_71218_barangays);
        cities.put("DAUIS", R.array.array_dauis_71219_barangays);
        cities.put("DIMIAO", R.array.array_dimiao_71220_barangays);
        cities.put("DUERO", R.array.array_duero_71221_barangays);
        cities.put("GARCIA HERNANDEZ", R.array.array_garcia_hernandez_71222_barangays);
        cities.put("GUINDULMAN", R.array.array_guindulman_71223_barangays);
        cities.put("INABANGA", R.array.array_inabanga_71224_barangays);
        cities.put("JAGNA", R.array.array_jagna_71225_barangays);
        cities.put("JETAFE", R.array.array_jetafe_71226_barangays);
        cities.put("LILA", R.array.array_lila_71227_barangays);
        cities.put("LOAY", R.array.array_loay_71228_barangays);
        cities.put("LOBOC", R.array.array_loboc_71229_barangays);
        cities.put("LOON", R.array.array_loon_71230_barangays);
        cities.put("BOHOL-MABINI", R.array.array_mabini_71231_barangays);
        cities.put("MARIBOJOC", R.array.array_maribojoc_71232_barangays);
        cities.put("PANGLAO", R.array.array_panglao_71233_barangays);
        cities.put("BOHOL-PILAR", R.array.array_pilar_71234_barangays);
        cities.put("PRES. CARLOS P. GARCIA (PITOGO)", R.array.array_pres_carlos_p_garcia_pitogo_71235_barangays);
        cities.put("SAGBAYAN (BORJA)", R.array.array_sagbayan_borja_71236_barangays);
        cities.put("BOHOL-SAN ISIDRO", R.array.array_san_isidro_71237_barangays);
        cities.put("BOHOL-SAN MIGUEL", R.array.array_san_miguel_71238_barangays);
        cities.put("SEVILLA", R.array.array_sevilla_71239_barangays);
        cities.put("SIERRA BULLONES", R.array.array_sierra_bullones_71240_barangays);
        cities.put("SIKATUNA", R.array.array_sikatuna_71241_barangays);
        cities.put("TAGBILARAN CITY (Capital)", R.array.array_tagbilaran_city_capital_71242_barangays);
        cities.put("TALIBON", R.array.array_talibon_71243_barangays);
        cities.put("TRINIDAD", R.array.array_trinidad_71244_barangays);
        cities.put("TUBIGON", R.array.array_tubigon_71245_barangays);
        cities.put("UBAY", R.array.array_ubay_71246_barangays);
        cities.put("VALENCIA", R.array.array_valencia_71247_barangays);
        cities.put("BIEN UNIDO", R.array.array_bien_unido_71248_barangays);
        cities.put("CEBU-ALCANTARA", R.array.array_alcantara_72201_barangays);
        cities.put("ALCOY", R.array.array_alcoy_72202_barangays);
        cities.put("CEBU-ALEGRIA", R.array.array_alegria_72203_barangays);
        cities.put("ALOGUINSAN", R.array.array_aloguinsan_72204_barangays);
        cities.put("ARGAO", R.array.array_argao_72205_barangays);
        cities.put("ASTURIAS", R.array.array_asturias_72206_barangays);
        cities.put("BADIAN", R.array.array_badian_72207_barangays);
        cities.put("BALAMBAN", R.array.array_balamban_72208_barangays);
        cities.put("BANTAYAN", R.array.array_bantayan_72209_barangays);
        cities.put("BARILI", R.array.array_barili_72210_barangays);
        cities.put("CITY OF BOGO", R.array.array_city_of_bogo_72211_barangays);
        cities.put("BOLJOON", R.array.array_boljoon_72212_barangays);
        cities.put("BORBON", R.array.array_borbon_72213_barangays);
        cities.put("CITY OF CARCAR", R.array.array_city_of_carcar_72214_barangays);
        cities.put("CEBU-CARMEN", R.array.array_carmen_72215_barangays);
        cities.put("CATMON", R.array.array_catmon_72216_barangays);
        cities.put("CEBU CITY (Capital)", R.array.array_cebu_city_capital_72217_barangays);
        cities.put("CEBU-COMPOSTELA", R.array.array_compostela_72218_barangays);
        cities.put("CONSOLACION", R.array.array_consolacion_72219_barangays);
        cities.put("CORDOVA", R.array.array_cordova_72220_barangays);
        cities.put("DAANBANTAYAN", R.array.array_daanbantayan_72221_barangays);
        cities.put("DALAGUETE", R.array.array_dalaguete_72222_barangays);
        cities.put("DANAO CITY", R.array.array_danao_city_72223_barangays);
        cities.put("DUMANJUG", R.array.array_dumanjug_72224_barangays);
        cities.put("GINATILAN", R.array.array_ginatilan_72225_barangays);
        cities.put("LAPU-LAPU CITY (OPON)", R.array.array_lapu_lapu_city_opon_72226_barangays);
        cities.put("CEBU-LILOAN", R.array.array_liloan_72227_barangays);
        cities.put("MADRIDEJOS", R.array.array_madridejos_72228_barangays);
        cities.put("MALABUYOC", R.array.array_malabuyoc_72229_barangays);
        cities.put("MANDAUE CITY", R.array.array_mandaue_city_72230_barangays);
        cities.put("MEDELLIN", R.array.array_medellin_72231_barangays);
        cities.put("MINGLANILLA", R.array.array_minglanilla_72232_barangays);
        cities.put("MOALBOAL", R.array.array_moalboal_72233_barangays);
        cities.put("CITY OF NAGA", R.array.array_city_of_naga_72234_barangays);
        cities.put("OSLOB", R.array.array_oslob_72235_barangays);
        cities.put("CEBU-PILAR", R.array.array_pilar_72236_barangays);
        cities.put("PINAMUNGAHAN", R.array.array_pinamungahan_72237_barangays);
        cities.put("PORO", R.array.array_poro_72238_barangays);
        cities.put("RONDA", R.array.array_ronda_72239_barangays);
        cities.put("SAMBOAN", R.array.array_samboan_72240_barangays);
        cities.put("CEBU-SAN FERNANDO", R.array.array_san_fernando_72241_barangays);
        cities.put("CEBU-SAN FRANCISCO", R.array.array_san_francisco_72242_barangays);
        cities.put("CEBU-SAN REMIGIO", R.array.array_san_remigio_72243_barangays);
        cities.put("CEBU-SANTA FE", R.array.array_santa_fe_72244_barangays);
        cities.put("SANTANDER", R.array.array_santander_72245_barangays);
        cities.put("SIBONGA", R.array.array_sibonga_72246_barangays);
        cities.put("CEBU-SOGOD", R.array.array_sogod_72247_barangays);
        cities.put("TABOGON", R.array.array_tabogon_72248_barangays);
        cities.put("TABUELAN", R.array.array_tabuelan_72249_barangays);
        cities.put("CEBU-CITY OF TALISAY", R.array.array_city_of_talisay_72250_barangays);
        cities.put("TOLEDO CITY", R.array.array_toledo_city_72251_barangays);
        cities.put("CEBU-TUBURAN", R.array.array_tuburan_72252_barangays);
        cities.put("CEBU-TUDELA", R.array.array_tudela_72253_barangays);
        cities.put("AMLAN (AYUQUITAN)", R.array.array_amlan_ayuquitan_74601_barangays);
        cities.put("AYUNGON", R.array.array_ayungon_74602_barangays);
        cities.put("BACONG", R.array.array_bacong_74603_barangays);
        cities.put("BAIS CITY", R.array.array_bais_city_74604_barangays);
        cities.put("BASAY", R.array.array_basay_74605_barangays);
        cities.put("CITY OF BAYAWAN (TULONG)", R.array.array_city_of_bayawan_tulong_74606_barangays);
        cities.put("BINDOY (PAYABON)", R.array.array_bindoy_payabon_74607_barangays);
        cities.put("CANLAON CITY", R.array.array_canlaon_city_74608_barangays);
        cities.put("DAUIN", R.array.array_dauin_74609_barangays);
        cities.put("DUMAGUETE CITY (Capital)", R.array.array_dumaguete_city_capital_74610_barangays);
        cities.put("CITY OF GUIHULNGAN", R.array.array_city_of_guihulngan_74611_barangays);
        cities.put("JIMALALUD", R.array.array_jimalalud_74612_barangays);
        cities.put("NEGROS ORIENTAL-LA LIBERTAD", R.array.array_la_libertad_74613_barangays);
        cities.put("MABINAY", R.array.array_mabinay_74614_barangays);
        cities.put("MANJUYOD", R.array.array_manjuyod_74615_barangays);
        cities.put("NEGROS ORIENTAL-PAMPLONA", R.array.array_pamplona_74616_barangays);
        cities.put("NEGROS ORIENTAL-SAN JOSE", R.array.array_san_jose_74617_barangays);
        cities.put("NEGROS ORIENTAL-SANTA CATALINA", R.array.array_santa_catalina_74618_barangays);
        cities.put("SIATON", R.array.array_siaton_74619_barangays);
        cities.put("SIBULAN", R.array.array_sibulan_74620_barangays);
        cities.put("CITY OF TANJAY", R.array.array_city_of_tanjay_74621_barangays);
        cities.put("TAYASAN", R.array.array_tayasan_74622_barangays);
        cities.put("VALENCIA (LUZURRIAGA)", R.array.array_valencia_luzurriaga_74623_barangays);
        cities.put("VALLEHERMOSO", R.array.array_vallehermoso_74624_barangays);
        cities.put("ZAMBOANGUITA", R.array.array_zamboanguita_74625_barangays);
        cities.put("ENRIQUE VILLANUEVA", R.array.array_enrique_villanueva_76101_barangays);
        cities.put("LARENA", R.array.array_larena_76102_barangays);
        cities.put("LAZI", R.array.array_lazi_76103_barangays);
        cities.put("MARIA", R.array.array_maria_76104_barangays);
        cities.put("SIQUIJOR-SAN JUAN", R.array.array_san_juan_76105_barangays);
        cities.put("SIQUIJOR (Capital)", R.array.array_siquijor_capital_76106_barangays);
        cities.put("ARTECHE", R.array.array_arteche_82601_barangays);
        cities.put("BALANGIGA", R.array.array_balangiga_82602_barangays);
        cities.put("BALANGKAYAN", R.array.array_balangkayan_82603_barangays);
        cities.put("CITY OF BORONGAN (Capital)", R.array.array_city_of_borongan_capital_82604_barangays);
        cities.put("CAN-AVID", R.array.array_can_avid_82605_barangays);
        cities.put("EASTERN SAMAR-DOLORES", R.array.array_dolores_82606_barangays);
        cities.put("GENERAL MACARTHUR", R.array.array_general_macarthur_82607_barangays);
        cities.put("GIPORLOS", R.array.array_giporlos_82608_barangays);
        cities.put("GUIUAN", R.array.array_guiuan_82609_barangays);
        cities.put("HERNANI", R.array.array_hernani_82610_barangays);
        cities.put("JIPAPAD", R.array.array_jipapad_82611_barangays);
        cities.put("LAWAAN", R.array.array_lawaan_82612_barangays);
        cities.put("LLORENTE", R.array.array_llorente_82613_barangays);
        cities.put("MASLOG", R.array.array_maslog_82614_barangays);
        cities.put("MAYDOLONG", R.array.array_maydolong_82615_barangays);
        cities.put("EASTERN SAMAR-MERCEDES", R.array.array_mercedes_82616_barangays);
        cities.put("ORAS", R.array.array_oras_82617_barangays);
        cities.put("QUINAPONDAN", R.array.array_quinapondan_82618_barangays);
        cities.put("SALCEDO", R.array.array_salcedo_82619_barangays);
        cities.put("SAN JULIAN", R.array.array_san_julian_82620_barangays);
        cities.put("SAN POLICARPO", R.array.array_san_policarpo_82621_barangays);
        cities.put("SULAT", R.array.array_sulat_82622_barangays);
        cities.put("TAFT", R.array.array_taft_82623_barangays);
        cities.put("ABUYOG", R.array.array_abuyog_83701_barangays);
        cities.put("ALANGALANG", R.array.array_alangalang_83702_barangays);
        cities.put("ALBUERA", R.array.array_albuera_83703_barangays);
        cities.put("BABATNGON", R.array.array_babatngon_83705_barangays);
        cities.put("BARUGO", R.array.array_barugo_83706_barangays);
        cities.put("LEYTE-BATO", R.array.array_bato_83707_barangays);
        cities.put("CITY OF BAYBAY", R.array.array_city_of_baybay_83708_barangays);
        cities.put("BURAUEN", R.array.array_burauen_83710_barangays);
        cities.put("CALUBIAN", R.array.array_calubian_83713_barangays);
        cities.put("CAPOOCAN", R.array.array_capoocan_83714_barangays);
        cities.put("CARIGARA", R.array.array_carigara_83715_barangays);
        cities.put("DAGAMI", R.array.array_dagami_83717_barangays);
        cities.put("DULAG", R.array.array_dulag_83718_barangays);
        cities.put("HILONGOS", R.array.array_hilongos_83719_barangays);
        cities.put("HINDANG", R.array.array_hindang_83720_barangays);
        cities.put("INOPACAN", R.array.array_inopacan_83721_barangays);
        cities.put("ISABEL", R.array.array_isabel_83722_barangays);
        cities.put("JARO", R.array.array_jaro_83723_barangays);
        cities.put("JAVIER (BUGHO)", R.array.array_javier_bugho_83724_barangays);
        cities.put("JULITA", R.array.array_julita_83725_barangays);
        cities.put("KANANGA", R.array.array_kananga_83726_barangays);
        cities.put("LEYTE-LA PAZ", R.array.array_la_paz_83728_barangays);
        cities.put("LEYTE", R.array.array_leyte_83729_barangays);
        cities.put("MACARTHUR", R.array.array_macarthur_83730_barangays);
        cities.put("MAHAPLAG", R.array.array_mahaplag_83731_barangays);
        cities.put("MATAG-OB", R.array.array_matag_ob_83733_barangays);
        cities.put("MATALOM", R.array.array_matalom_83734_barangays);
        cities.put("MAYORGA", R.array.array_mayorga_83735_barangays);
        cities.put("MERIDA", R.array.array_merida_83736_barangays);
        cities.put("ORMOC CITY", R.array.array_ormoc_city_83738_barangays);
        cities.put("PALO", R.array.array_palo_83739_barangays);
        cities.put("PALOMPON", R.array.array_palompon_83740_barangays);
        cities.put("PASTRANA", R.array.array_pastrana_83741_barangays);
        cities.put("LEYTE-SAN ISIDRO", R.array.array_san_isidro_83742_barangays);
        cities.put("LEYTE-SAN MIGUEL", R.array.array_san_miguel_83743_barangays);
        cities.put("LEYTE-SANTA FE", R.array.array_santa_fe_83744_barangays);
        cities.put("TABANGO", R.array.array_tabango_83745_barangays);
        cities.put("TABONTABON", R.array.array_tabontabon_83746_barangays);
        cities.put("TACLOBAN CITY (Capital)", R.array.array_tacloban_city_capital_83747_barangays);
        cities.put("TANAUAN", R.array.array_tanauan_83748_barangays);
        cities.put("TOLOSA", R.array.array_tolosa_83749_barangays);
        cities.put("TUNGA", R.array.array_tunga_83750_barangays);
        cities.put("VILLABA", R.array.array_villaba_83751_barangays);
        cities.put("ALLEN", R.array.array_allen_84801_barangays);
        cities.put("BIRI", R.array.array_biri_84802_barangays);
        cities.put("BOBON", R.array.array_bobon_84803_barangays);
        cities.put("CAPUL", R.array.array_capul_84804_barangays);
        cities.put("CATARMAN (Capital)", R.array.array_catarman_capital_84805_barangays);
        cities.put("CATUBIG", R.array.array_catubig_84806_barangays);
        cities.put("GAMAY", R.array.array_gamay_84807_barangays);
        cities.put("LAOANG", R.array.array_laoang_84808_barangays);
        cities.put("LAPINIG", R.array.array_lapinig_84809_barangays);
        cities.put("LAS NAVAS", R.array.array_las_navas_84810_barangays);
        cities.put("LAVEZARES", R.array.array_lavezares_84811_barangays);
        cities.put("MAPANAS", R.array.array_mapanas_84812_barangays);
        cities.put("MONDRAGON", R.array.array_mondragon_84813_barangays);
        cities.put("PALAPAG", R.array.array_palapag_84814_barangays);
        cities.put("PAMBUJAN", R.array.array_pambujan_84815_barangays);
        cities.put("NORTHERN SAMAR-ROSARIO", R.array.array_rosario_84816_barangays);
        cities.put("NORTHERN SAMAR-SAN ANTONIO", R.array.array_san_antonio_84817_barangays);
        cities.put("NORTHERN SAMAR-SAN ISIDRO", R.array.array_san_isidro_84818_barangays);
        cities.put("NORTHERN SAMAR-SAN JOSE", R.array.array_san_jose_84819_barangays);
        cities.put("SAN ROQUE", R.array.array_san_roque_84820_barangays);
        cities.put("NORTHERN SAMAR-SAN VICENTE", R.array.array_san_vicente_84821_barangays);
        cities.put("SILVINO LOBOS", R.array.array_silvino_lobos_84822_barangays);
        cities.put("NORTHERN SAMAR-VICTORIA", R.array.array_victoria_84823_barangays);
        cities.put("LOPE DE VEGA", R.array.array_lope_de_vega_84824_barangays);
        cities.put("ALMAGRO", R.array.array_almagro_86001_barangays);
        cities.put("BASEY", R.array.array_basey_86002_barangays);
        cities.put("CALBAYOG CITY", R.array.array_calbayog_city_86003_barangays);
        cities.put("CALBIGA", R.array.array_calbiga_86004_barangays);
        cities.put("CITY OF CATBALOGAN (Capital)", R.array.array_city_of_catbalogan_capital_86005_barangays);
        cities.put("DARAM", R.array.array_daram_86006_barangays);
        cities.put("GANDARA", R.array.array_gandara_86007_barangays);
        cities.put("HINABANGAN", R.array.array_hinabangan_86008_barangays);
        cities.put("JIABONG", R.array.array_jiabong_86009_barangays);
        cities.put("MARABUT", R.array.array_marabut_86010_barangays);
        cities.put("MATUGUINAO", R.array.array_matuguinao_86011_barangays);
        cities.put("MOTIONG", R.array.array_motiong_86012_barangays);
        cities.put("PINABACDAO", R.array.array_pinabacdao_86013_barangays);
        cities.put("SAN JOSE DE BUAN", R.array.array_san_jose_de_buan_86014_barangays);
        cities.put("SAN SEBASTIAN", R.array.array_san_sebastian_86015_barangays);
        cities.put("SANTA MARGARITA", R.array.array_santa_margarita_86016_barangays);
        cities.put("SAMAR (WESTERN SAMAR)-SANTA RITA", R.array.array_santa_rita_86017_barangays);
        cities.put("SAMAR (WESTERN SAMAR)-SANTO NIÑO", R.array.array_santo_niño_86018_barangays);
        cities.put("TALALORA", R.array.array_talalora_86019_barangays);
        cities.put("TARANGNAN", R.array.array_tarangnan_86020_barangays);
        cities.put("VILLAREAL", R.array.array_villareal_86021_barangays);
        cities.put("PARANAS (WRIGHT)", R.array.array_paranas_wright_86022_barangays);
        cities.put("ZUMARRAGA", R.array.array_zumarraga_86023_barangays);
        cities.put("TAGAPUL-AN", R.array.array_tagapul_an_86024_barangays);
        cities.put("SAN JORGE", R.array.array_san_jorge_86025_barangays);
        cities.put("PAGSANGHAN", R.array.array_pagsanghan_86026_barangays);
        cities.put("ANAHAWAN", R.array.array_anahawan_86401_barangays);
        cities.put("BONTOC", R.array.array_bontoc_86402_barangays);
        cities.put("HINUNANGAN", R.array.array_hinunangan_86403_barangays);
        cities.put("HINUNDAYAN", R.array.array_hinundayan_86404_barangays);
        cities.put("LIBAGON", R.array.array_libagon_86405_barangays);
        cities.put("SOUTHERN LEYTE-LILOAN", R.array.array_liloan_86406_barangays);
        cities.put("CITY OF MAASIN (Capital)", R.array.array_city_of_maasin_capital_86407_barangays);
        cities.put("MACROHON", R.array.array_macrohon_86408_barangays);
        cities.put("SOUTHERN LEYTE-MALITBOG", R.array.array_malitbog_86409_barangays);
        cities.put("SOUTHERN LEYTE-PADRE BURGOS", R.array.array_padre_burgos_86410_barangays);
        cities.put("PINTUYAN", R.array.array_pintuyan_86411_barangays);
        cities.put("SAINT BERNARD", R.array.array_saint_bernard_86412_barangays);
        cities.put("SOUTHERN LEYTE-SAN FRANCISCO", R.array.array_san_francisco_86413_barangays);
        cities.put("SAN JUAN (CABALIAN)", R.array.array_san_juan_cabalian_86414_barangays);
        cities.put("SAN RICARDO", R.array.array_san_ricardo_86415_barangays);
        cities.put("SILAGO", R.array.array_silago_86416_barangays);
        cities.put("SOUTHERN LEYTE-SOGOD", R.array.array_sogod_86417_barangays);
        cities.put("TOMAS OPPUS", R.array.array_tomas_oppus_86418_barangays);
        cities.put("LIMASAWA", R.array.array_limasawa_86419_barangays);
        cities.put("ALMERIA", R.array.array_almeria_87801_barangays);
        cities.put("BILIRAN", R.array.array_biliran_87802_barangays);
        cities.put("CABUCGAYAN", R.array.array_cabucgayan_87803_barangays);
        cities.put("CAIBIRAN", R.array.array_caibiran_87804_barangays);
        cities.put("CULABA", R.array.array_culaba_87805_barangays);
        cities.put("KAWAYAN", R.array.array_kawayan_87806_barangays);
        cities.put("MARIPIPI", R.array.array_maripipi_87807_barangays);
        cities.put("NAVAL (Capital)", R.array.array_naval_capital_87808_barangays);
        cities.put("DAPITAN CITY", R.array.array_dapitan_city_97201_barangays);
        cities.put("DIPOLOG CITY (Capital)", R.array.array_dipolog_city_capital_97202_barangays);
        cities.put("KATIPUNAN", R.array.array_katipunan_97203_barangays);
        cities.put("ZAMBOANGA DEL NORTE-LA LIBERTAD", R.array.array_la_libertad_97204_barangays);
        cities.put("LABASON", R.array.array_labason_97205_barangays);
        cities.put("LILOY", R.array.array_liloy_97206_barangays);
        cities.put("MANUKAN", R.array.array_manukan_97207_barangays);
        cities.put("MUTIA", R.array.array_mutia_97208_barangays);
        cities.put("PIÑAN (NEW PIÑAN)", R.array.array_piñan_new_piñan_97209_barangays);
        cities.put("POLANCO", R.array.array_polanco_97210_barangays);
        cities.put("PRES. MANUEL A. ROXAS", R.array.array_pres_manuel_a_roxas_97211_barangays);
        cities.put("ZAMBOANGA DEL NORTE-RIZAL", R.array.array_rizal_97212_barangays);
        cities.put("SALUG", R.array.array_salug_97213_barangays);
        cities.put("SERGIO OSMEÑA SR.", R.array.array_sergio_osmeña_sr_97214_barangays);
        cities.put("SIAYAN", R.array.array_siayan_97215_barangays);
        cities.put("SIBUCO", R.array.array_sibuco_97216_barangays);
        cities.put("SIBUTAD", R.array.array_sibutad_97217_barangays);
        cities.put("SINDANGAN", R.array.array_sindangan_97218_barangays);
        cities.put("SIOCON", R.array.array_siocon_97219_barangays);
        cities.put("SIRAWAI", R.array.array_sirawai_97220_barangays);
        cities.put("TAMPILISAN", R.array.array_tampilisan_97221_barangays);
        cities.put("JOSE DALMAN (PONOT)", R.array.array_jose_dalman_ponot_97222_barangays);
        cities.put("GUTALAC", R.array.array_gutalac_97223_barangays);
        cities.put("BALIGUIAN", R.array.array_baliguian_97224_barangays);
        cities.put("GODOD", R.array.array_godod_97225_barangays);
        cities.put("BACUNGAN (Leon T. Postigo)", R.array.array_bacungan_leon_t_postigo_97226_barangays);
        cities.put("KALAWIT", R.array.array_kalawit_97227_barangays);
        cities.put("ZAMBOANGA DEL SUR-AURORA", R.array.array_aurora_97302_barangays);
        cities.put("BAYOG", R.array.array_bayog_97303_barangays);
        cities.put("DIMATALING", R.array.array_dimataling_97305_barangays);
        cities.put("DINAS", R.array.array_dinas_97306_barangays);
        cities.put("DUMALINAO", R.array.array_dumalinao_97307_barangays);
        cities.put("DUMINGAG", R.array.array_dumingag_97308_barangays);
        cities.put("KUMALARANG", R.array.array_kumalarang_97311_barangays);
        cities.put("LABANGAN", R.array.array_labangan_97312_barangays);
        cities.put("LAPUYAN", R.array.array_lapuyan_97313_barangays);
        cities.put("MAHAYAG", R.array.array_mahayag_97315_barangays);
        cities.put("MARGOSATUBIG", R.array.array_margosatubig_97317_barangays);
        cities.put("MIDSALIP", R.array.array_midsalip_97318_barangays);
        cities.put("MOLAVE", R.array.array_molave_97319_barangays);
        cities.put("PAGADIAN CITY (Capital)", R.array.array_pagadian_city_capital_97322_barangays);
        cities.put("RAMON MAGSAYSAY (LIARGO)", R.array.array_ramon_magsaysay_liargo_97323_barangays);
        cities.put("ZAMBOANGA DEL SUR-SAN MIGUEL", R.array.array_san_miguel_97324_barangays);
        cities.put("ZAMBOANGA DEL SUR-SAN PABLO", R.array.array_san_pablo_97325_barangays);
        cities.put("TABINA", R.array.array_tabina_97327_barangays);
        cities.put("TAMBULIG", R.array.array_tambulig_97328_barangays);
        cities.put("TUKURAN", R.array.array_tukuran_97330_barangays);
        cities.put("ZAMBOANGA CITY", R.array.array_zamboanga_city_97332_barangays);
        cities.put("LAKEWOOD", R.array.array_lakewood_97333_barangays);
        cities.put("JOSEFINA", R.array.array_josefina_97337_barangays);
        cities.put("ZAMBOANGA DEL SUR-PITOGO", R.array.array_pitogo_97338_barangays);
        cities.put("SOMINOT (DON MARIANO MARCOS)", R.array.array_sominot_don_mariano_marcos_97340_barangays);
        cities.put("VINCENZO A. SAGUN", R.array.array_vincenzo_a_sagun_97341_barangays);
        cities.put("GUIPOS", R.array.array_guipos_97343_barangays);
        cities.put("TIGBAO", R.array.array_tigbao_97344_barangays);
        cities.put("ZAMBOANGA SIBUGAY-ALICIA", R.array.array_alicia_98301_barangays);
        cities.put("BUUG", R.array.array_buug_98302_barangays);
        cities.put("DIPLAHAN", R.array.array_diplahan_98303_barangays);
        cities.put("IMELDA", R.array.array_imelda_98304_barangays);
        cities.put("IPIL (Capital)", R.array.array_ipil_capital_98305_barangays);
        cities.put("KABASALAN", R.array.array_kabasalan_98306_barangays);
        cities.put("MABUHAY", R.array.array_mabuhay_98307_barangays);
        cities.put("MALANGAS", R.array.array_malangas_98308_barangays);
        cities.put("NAGA", R.array.array_naga_98309_barangays);
        cities.put("OLUTANGA", R.array.array_olutanga_98310_barangays);
        cities.put("PAYAO", R.array.array_payao_98311_barangays);
        cities.put("ROSELLER LIM", R.array.array_roseller_lim_98312_barangays);
        cities.put("SIAY", R.array.array_siay_98313_barangays);
        cities.put("TALUSAN", R.array.array_talusan_98314_barangays);
        cities.put("TITAY", R.array.array_titay_98315_barangays);
        cities.put("TUNGAWAN", R.array.array_tungawan_98316_barangays);
        cities.put("CITY OF ISABELA", R.array.array_city_of_isabela_99701_barangays);
        cities.put("BAUNGON", R.array.array_baungon_101301_barangays);
        cities.put("DAMULOG", R.array.array_damulog_101302_barangays);
        cities.put("DANGCAGAN", R.array.array_dangcagan_101303_barangays);
        cities.put("DON CARLOS", R.array.array_don_carlos_101304_barangays);
        cities.put("IMPASUG-ONG", R.array.array_impasug_ong_101305_barangays);
        cities.put("KADINGILAN", R.array.array_kadingilan_101306_barangays);
        cities.put("KALILANGAN", R.array.array_kalilangan_101307_barangays);
        cities.put("KIBAWE", R.array.array_kibawe_101308_barangays);
        cities.put("KITAOTAO", R.array.array_kitaotao_101309_barangays);
        cities.put("LANTAPAN", R.array.array_lantapan_101310_barangays);
        cities.put("LIBONA", R.array.array_libona_101311_barangays);
        cities.put("CITY OF MALAYBALAY (Capital)", R.array.array_city_of_malaybalay_capital_101312_barangays);
        cities.put("BUKIDNON-MALITBOG", R.array.array_malitbog_101313_barangays);
        cities.put("MANOLO FORTICH", R.array.array_manolo_fortich_101314_barangays);
        cities.put("MARAMAG", R.array.array_maramag_101315_barangays);
        cities.put("PANGANTUCAN", R.array.array_pangantucan_101316_barangays);
        cities.put("BUKIDNON-QUEZON", R.array.array_quezon_101317_barangays);
        cities.put("BUKIDNON-SAN FERNANDO", R.array.array_san_fernando_101318_barangays);
        cities.put("SUMILAO", R.array.array_sumilao_101319_barangays);
        cities.put("TALAKAG", R.array.array_talakag_101320_barangays);
        cities.put("CITY OF VALENCIA", R.array.array_city_of_valencia_101321_barangays);
        cities.put("CABANGLASAN", R.array.array_cabanglasan_101322_barangays);
        cities.put("CATARMAN", R.array.array_catarman_101801_barangays);
        cities.put("GUINSILIBAN", R.array.array_guinsiliban_101802_barangays);
        cities.put("MAHINOG", R.array.array_mahinog_101803_barangays);
        cities.put("MAMBAJAO (Capital)", R.array.array_mambajao_capital_101804_barangays);
        cities.put("SAGAY", R.array.array_sagay_101805_barangays);
        cities.put("BACOLOD", R.array.array_bacolod_103501_barangays);
        cities.put("BALOI", R.array.array_baloi_103502_barangays);
        cities.put("BAROY", R.array.array_baroy_103503_barangays);
        cities.put("ILIGAN CITY", R.array.array_iligan_city_103504_barangays);
        cities.put("LANAO DEL NORTE-KAPATAGAN", R.array.array_kapatagan_103505_barangays);
        cities.put("SULTAN NAGA DIMAPORO (KAROMATAN)", R.array.array_sultan_naga_dimaporo_karomatan_103506_barangays);
        cities.put("KAUSWAGAN", R.array.array_kauswagan_103507_barangays);
        cities.put("KOLAMBUGAN", R.array.array_kolambugan_103508_barangays);
        cities.put("LALA", R.array.array_lala_103509_barangays);
        cities.put("LINAMON", R.array.array_linamon_103510_barangays);
        cities.put("LANAO DEL NORTE-MAGSAYSAY", R.array.array_magsaysay_103511_barangays);
        cities.put("MAIGO", R.array.array_maigo_103512_barangays);
        cities.put("MATUNGAO", R.array.array_matungao_103513_barangays);
        cities.put("MUNAI", R.array.array_munai_103514_barangays);
        cities.put("NUNUNGAN", R.array.array_nunungan_103515_barangays);
        cities.put("PANTAO RAGAT", R.array.array_pantao_ragat_103516_barangays);
        cities.put("POONA PIAGAPO", R.array.array_poona_piagapo_103517_barangays);
        cities.put("SALVADOR", R.array.array_salvador_103518_barangays);
        cities.put("SAPAD", R.array.array_sapad_103519_barangays);
        cities.put("LANAO DEL NORTE-TAGOLOAN", R.array.array_tagoloan_103520_barangays);
        cities.put("TANGCAL", R.array.array_tangcal_103521_barangays);
        cities.put("TUBOD (Capital)", R.array.array_tubod_capital_103522_barangays);
        cities.put("PANTAR", R.array.array_pantar_103523_barangays);
        cities.put("ALORAN", R.array.array_aloran_104201_barangays);
        cities.put("BALIANGAO", R.array.array_baliangao_104202_barangays);
        cities.put("BONIFACIO", R.array.array_bonifacio_104203_barangays);
        cities.put("CALAMBA", R.array.array_calamba_104204_barangays);
        cities.put("MISAMIS OCCIDENTAL-CLARIN", R.array.array_clarin_104205_barangays);
        cities.put("MISAMIS OCCIDENTAL-CONCEPCION", R.array.array_concepcion_104206_barangays);
        cities.put("JIMENEZ", R.array.array_jimenez_104207_barangays);
        cities.put("LOPEZ JAENA", R.array.array_lopez_jaena_104208_barangays);
        cities.put("OROQUIETA CITY (Capital)", R.array.array_oroquieta_city_capital_104209_barangays);
        cities.put("OZAMIS CITY", R.array.array_ozamis_city_104210_barangays);
        cities.put("PANAON", R.array.array_panaon_104211_barangays);
        cities.put("MISAMIS OCCIDENTAL-PLARIDEL", R.array.array_plaridel_104212_barangays);
        cities.put("SAPANG DALAGA", R.array.array_sapang_dalaga_104213_barangays);
        cities.put("SINACABAN", R.array.array_sinacaban_104214_barangays);
        cities.put("TANGUB CITY", R.array.array_tangub_city_104215_barangays);
        cities.put("MISAMIS OCCIDENTAL-TUDELA", R.array.array_tudela_104216_barangays);
        cities.put("DON VICTORIANO CHIONGBIAN  (DON MARIANO MARCOS)", R.array.array_don_victoriano_chiongbian__don_mariano_marcos_104217_barangays);
        cities.put("ALUBIJID", R.array.array_alubijid_104301_barangays);
        cities.put("BALINGASAG", R.array.array_balingasag_104302_barangays);
        cities.put("BALINGOAN", R.array.array_balingoan_104303_barangays);
        cities.put("BINUANGAN", R.array.array_binuangan_104304_barangays);
        cities.put("CAGAYAN DE ORO CITY (Capital)", R.array.array_cagayan_de_oro_city_capital_104305_barangays);
        cities.put("MISAMIS ORIENTAL-CLAVERIA", R.array.array_claveria_104306_barangays);
        cities.put("CITY OF EL SALVADOR", R.array.array_city_of_el_salvador_104307_barangays);
        cities.put("GINGOOG CITY", R.array.array_gingoog_city_104308_barangays);
        cities.put("GITAGUM", R.array.array_gitagum_104309_barangays);
        cities.put("INITAO", R.array.array_initao_104310_barangays);
        cities.put("JASAAN", R.array.array_jasaan_104311_barangays);
        cities.put("KINOGUITAN", R.array.array_kinoguitan_104312_barangays);
        cities.put("LAGONGLONG", R.array.array_lagonglong_104313_barangays);
        cities.put("LAGUINDINGAN", R.array.array_laguindingan_104314_barangays);
        cities.put("MISAMIS ORIENTAL-LIBERTAD", R.array.array_libertad_104315_barangays);
        cities.put("LUGAIT", R.array.array_lugait_104316_barangays);
        cities.put("MAGSAYSAY (LINUGOS)", R.array.array_magsaysay_linugos_104317_barangays);
        cities.put("MANTICAO", R.array.array_manticao_104318_barangays);
        cities.put("MEDINA", R.array.array_medina_104319_barangays);
        cities.put("NAAWAN", R.array.array_naawan_104320_barangays);
        cities.put("OPOL", R.array.array_opol_104321_barangays);
        cities.put("SALAY", R.array.array_salay_104322_barangays);
        cities.put("SUGBONGCOGON", R.array.array_sugbongcogon_104323_barangays);
        cities.put("MISAMIS ORIENTAL-TAGOLOAN", R.array.array_tagoloan_104324_barangays);
        cities.put("TALISAYAN", R.array.array_talisayan_104325_barangays);
        cities.put("VILLANUEVA", R.array.array_villanueva_104326_barangays);
        cities.put("ASUNCION (SAUG)", R.array.array_asuncion_saug_112301_barangays);
        cities.put("DAVAO DEL NORTE-CARMEN", R.array.array_carmen_112303_barangays);
        cities.put("KAPALONG", R.array.array_kapalong_112305_barangays);
        cities.put("NEW CORELLA", R.array.array_new_corella_112314_barangays);
        cities.put("CITY OF PANABO", R.array.array_city_of_panabo_112315_barangays);
        cities.put("ISLAND GARDEN CITY OF SAMAL", R.array.array_island_garden_city_of_samal_112317_barangays);
        cities.put("DAVAO DEL NORTE-SANTO TOMAS", R.array.array_santo_tomas_112318_barangays);
        cities.put("CITY OF TAGUM (Capital)", R.array.array_city_of_tagum_capital_112319_barangays);
        cities.put("TALAINGOD", R.array.array_talaingod_112322_barangays);
        cities.put("BRAULIO E. DUJALI", R.array.array_braulio_e_dujali_112323_barangays);
        cities.put("DAVAO DEL NORTE-SAN ISIDRO", R.array.array_san_isidro_112324_barangays);
        cities.put("BANSALAN", R.array.array_bansalan_112401_barangays);
        cities.put("DAVAO CITY", R.array.array_davao_city_112402_barangays);
        cities.put("CITY OF DIGOS (Capital)", R.array.array_city_of_digos_capital_112403_barangays);
        cities.put("DAVAO DEL SUR-HAGONOY", R.array.array_hagonoy_112404_barangays);
        cities.put("KIBLAWAN", R.array.array_kiblawan_112406_barangays);
        cities.put("DAVAO DEL SUR-MAGSAYSAY", R.array.array_magsaysay_112407_barangays);
        cities.put("MALALAG", R.array.array_malalag_112408_barangays);
        cities.put("MATANAO", R.array.array_matanao_112410_barangays);
        cities.put("PADADA", R.array.array_padada_112411_barangays);
        cities.put("DAVAO DEL SUR-SANTA CRUZ", R.array.array_santa_cruz_112412_barangays);
        cities.put("SULOP", R.array.array_sulop_112414_barangays);
        cities.put("BAGANGA", R.array.array_baganga_112501_barangays);
        cities.put("BANAYBANAY", R.array.array_banaybanay_112502_barangays);
        cities.put("BOSTON", R.array.array_boston_112503_barangays);
        cities.put("CARAGA", R.array.array_caraga_112504_barangays);
        cities.put("CATEEL", R.array.array_cateel_112505_barangays);
        cities.put("GOVERNOR GENEROSO", R.array.array_governor_generoso_112506_barangays);
        cities.put("LUPON", R.array.array_lupon_112507_barangays);
        cities.put("MANAY", R.array.array_manay_112508_barangays);
        cities.put("CITY OF MATI (Capital)", R.array.array_city_of_mati_capital_112509_barangays);
        cities.put("DAVAO ORIENTAL-SAN ISIDRO", R.array.array_san_isidro_112510_barangays);
        cities.put("TARRAGONA", R.array.array_tarragona_112511_barangays);
        cities.put("COMPOSTELA VALLEY-COMPOSTELA", R.array.array_compostela_118201_barangays);
        cities.put("LAAK (SAN VICENTE)", R.array.array_laak_san_vicente_118202_barangays);
        cities.put("MABINI (DOÑA ALICIA)", R.array.array_mabini_doña_alicia_118203_barangays);
        cities.put("MACO", R.array.array_maco_118204_barangays);
        cities.put("MARAGUSAN (SAN MARIANO)", R.array.array_maragusan_san_mariano_118205_barangays);
        cities.put("MAWAB", R.array.array_mawab_118206_barangays);
        cities.put("MONKAYO", R.array.array_monkayo_118207_barangays);
        cities.put("MONTEVISTA", R.array.array_montevista_118208_barangays);
        cities.put("NABUNTURAN (Capital)", R.array.array_nabunturan_capital_118209_barangays);
        cities.put("NEW BATAAN", R.array.array_new_bataan_118210_barangays);
        cities.put("PANTUKAN", R.array.array_pantukan_118211_barangays);
        cities.put("DON MARCELINO", R.array.array_don_marcelino_118601_barangays);
        cities.put("JOSE ABAD SANTOS (TRINIDAD)", R.array.array_jose_abad_santos_trinidad_118602_barangays);
        cities.put("MALITA", R.array.array_malita_118603_barangays);
        cities.put("DAVAO OCCIDENTAL-SANTA MARIA", R.array.array_santa_maria_118604_barangays);
        cities.put("SARANGANI", R.array.array_sarangani_118605_barangays);
        cities.put("ALAMADA", R.array.array_alamada_124701_barangays);
        cities.put("COTABATO (NORTH COTABATO)-CARMEN", R.array.array_carmen_124702_barangays);
        cities.put("KABACAN", R.array.array_kabacan_124703_barangays);
        cities.put("CITY OF KIDAPAWAN (Capital)", R.array.array_city_of_kidapawan_capital_124704_barangays);
        cities.put("LIBUNGAN", R.array.array_libungan_124705_barangays);
        cities.put("MAGPET", R.array.array_magpet_124706_barangays);
        cities.put("MAKILALA", R.array.array_makilala_124707_barangays);
        cities.put("MATALAM", R.array.array_matalam_124708_barangays);
        cities.put("MIDSAYAP", R.array.array_midsayap_124709_barangays);
        cities.put("M LANG", R.array.array_mlang_124710_barangays);
        cities.put("PIGKAWAYAN", R.array.array_pigkawayan_124711_barangays);
        cities.put("PIKIT", R.array.array_pikit_124712_barangays);
        cities.put("COTABATO (NORTH COTABATO)-PRESIDENT ROXAS", R.array.array_president_roxas_124713_barangays);
        cities.put("TULUNAN", R.array.array_tulunan_124714_barangays);
        cities.put("ANTIPAS", R.array.array_antipas_124715_barangays);
        cities.put("BANISILAN", R.array.array_banisilan_124716_barangays);
        cities.put("ALEOSAN", R.array.array_aleosan_124717_barangays);
        cities.put("ARAKAN", R.array.array_arakan_124718_barangays);
        cities.put("SOUTH COTABATO-BANGA", R.array.array_banga_126302_barangays);
        cities.put("GENERAL SANTOS CITY (DADIANGAS)", R.array.array_general_santos_city_dadiangas_126303_barangays);
        cities.put("CITY OF KORONADAL (Capital)", R.array.array_city_of_koronadal_capital_126306_barangays);
        cities.put("NORALA", R.array.array_norala_126311_barangays);
        cities.put("POLOMOLOK", R.array.array_polomolok_126312_barangays);
        cities.put("SURALLAH", R.array.array_surallah_126313_barangays);
        cities.put("TAMPAKAN", R.array.array_tampakan_126314_barangays);
        cities.put("TANTANGAN", R.array.array_tantangan_126315_barangays);
        cities.put("T BOLI", R.array.array_tboli_126316_barangays);
        cities.put("TUPI", R.array.array_tupi_126317_barangays);
        cities.put("SOUTH COTABATO-SANTO NIÑO", R.array.array_santo_niño_126318_barangays);
        cities.put("LAKE SEBU", R.array.array_lake_sebu_126319_barangays);
        cities.put("BAGUMBAYAN", R.array.array_bagumbayan_126501_barangays);
        cities.put("COLUMBIO", R.array.array_columbio_126502_barangays);
        cities.put("SULTAN KUDARAT-ESPERANZA", R.array.array_esperanza_126503_barangays);
        cities.put("ISULAN (Capital)", R.array.array_isulan_capital_126504_barangays);
        cities.put("KALAMANSIG", R.array.array_kalamansig_126505_barangays);
        cities.put("LEBAK", R.array.array_lebak_126506_barangays);
        cities.put("LUTAYAN", R.array.array_lutayan_126507_barangays);
        cities.put("LAMBAYONG (MARIANO MARCOS)", R.array.array_lambayong_mariano_marcos_126508_barangays);
        cities.put("PALIMBANG", R.array.array_palimbang_126509_barangays);
        cities.put("PRESIDENT QUIRINO", R.array.array_president_quirino_126510_barangays);
        cities.put("CITY OF TACURONG", R.array.array_city_of_tacurong_126511_barangays);
        cities.put("SEN. NINOY AQUINO", R.array.array_sen_ninoy_aquino_126512_barangays);
        cities.put("ALABEL (Capital)", R.array.array_alabel_capital_128001_barangays);
        cities.put("GLAN", R.array.array_glan_128002_barangays);
        cities.put("KIAMBA", R.array.array_kiamba_128003_barangays);
        cities.put("MAASIM", R.array.array_maasim_128004_barangays);
        cities.put("MAITUM", R.array.array_maitum_128005_barangays);
        cities.put("MALAPATAN", R.array.array_malapatan_128006_barangays);
        cities.put("MALUNGON", R.array.array_malungon_128007_barangays);
        cities.put("COTABATO CITY", R.array.array_cotabato_city_129804_barangays);
        cities.put("TONDO I / II", R.array.array_tondo_133901_barangays);
        cities.put("BINONDO", R.array.array_binondo_133902_barangays);
        cities.put("QUIAPO", R.array.array_quiapo_133903_barangays);
        cities.put("NCR, CITY OF MANILA, FIRST DISTRICT-SAN NICOLAS", R.array.array_san_nicolas_133904_barangays);
        cities.put("NCR, CITY OF MANILA, FIRST DISTRICT-SANTA CRUZ", R.array.array_santa_cruz_133905_barangays);
        cities.put("NCR, CITY OF MANILA, FIRST DISTRICT-SAMPALOC", R.array.array_sampaloc_133906_barangays);
        cities.put("NCR, CITY OF MANILA, FIRST DISTRICT-SAN MIGUEL", R.array.array_san_miguel_133907_barangays);
        cities.put("ERMITA", R.array.array_ermita_133908_barangays);
        cities.put("INTRAMUROS", R.array.array_intramuros_133909_barangays);
        cities.put("MALATE", R.array.array_malate_133910_barangays);
        cities.put("PACO", R.array.array_paco_133911_barangays);
        cities.put("PANDACAN", R.array.array_pandacan_133912_barangays);
        cities.put("PORT AREA", R.array.array_port_area_133913_barangays);
        cities.put("NCR, CITY OF MANILA, FIRST DISTRICT-SANTA ANA", R.array.array_santa_ana_133914_barangays);
        cities.put("CITY OF MANDALUYONG", R.array.array_city_of_mandaluyong_137401_barangays);
        cities.put("CITY OF MARIKINA", R.array.array_city_of_marikina_137402_barangays);
        cities.put("CITY OF PASIG", R.array.array_city_of_pasig_137403_barangays);
        cities.put("QUEZON CITY", R.array.array_quezon_city_137404_barangays);
        cities.put("CITY OF SAN JUAN", R.array.array_city_of_san_juan_137405_barangays);
        cities.put("CALOOCAN CITY", R.array.array_caloocan_city_137501_barangays);
        cities.put("CITY OF MALABON", R.array.array_city_of_malabon_137502_barangays);
        cities.put("CITY OF NAVOTAS", R.array.array_city_of_navotas_137503_barangays);
        cities.put("CITY OF VALENZUELA", R.array.array_city_of_valenzuela_137504_barangays);
        cities.put("CITY OF LAS PIÑAS", R.array.array_city_of_las_piñas_137601_barangays);
        cities.put("CITY OF MAKATI", R.array.array_city_of_makati_137602_barangays);
        cities.put("CITY OF MUNTINLUPA", R.array.array_city_of_muntinlupa_137603_barangays);
        cities.put("CITY OF PARAÑAQUE", R.array.array_city_of_parañaque_137604_barangays);
        cities.put("PASAY CITY", R.array.array_pasay_city_137605_barangays);
        cities.put("PATEROS", R.array.array_pateros_137606_barangays);
        cities.put("TAGUIG CITY", R.array.array_taguig_city_137607_barangays);
        cities.put("BANGUED (Capital)", R.array.array_bangued_capital_140101_barangays);
        cities.put("BOLINEY", R.array.array_boliney_140102_barangays);
        cities.put("BUCAY", R.array.array_bucay_140103_barangays);
        cities.put("BUCLOC", R.array.array_bucloc_140104_barangays);
        cities.put("DAGUIOMAN", R.array.array_daguioman_140105_barangays);
        cities.put("DANGLAS", R.array.array_danglas_140106_barangays);
        cities.put("ABRA-DOLORES", R.array.array_dolores_140107_barangays);
        cities.put("ABRA-LA PAZ", R.array.array_la_paz_140108_barangays);
        cities.put("LACUB", R.array.array_lacub_140109_barangays);
        cities.put("LAGANGILANG", R.array.array_lagangilang_140110_barangays);
        cities.put("LAGAYAN", R.array.array_lagayan_140111_barangays);
        cities.put("LANGIDEN", R.array.array_langiden_140112_barangays);
        cities.put("LICUAN-BAAY (LICUAN)", R.array.array_licuan_baay_licuan_140113_barangays);
        cities.put("LUBA", R.array.array_luba_140114_barangays);
        cities.put("MALIBCONG", R.array.array_malibcong_140115_barangays);
        cities.put("MANABO", R.array.array_manabo_140116_barangays);
        cities.put("PEÑARRUBIA", R.array.array_peñarrubia_140117_barangays);
        cities.put("PIDIGAN", R.array.array_pidigan_140118_barangays);
        cities.put("ABRA-PILAR", R.array.array_pilar_140119_barangays);
        cities.put("SALLAPADAN", R.array.array_sallapadan_140120_barangays);
        cities.put("ABRA-SAN ISIDRO", R.array.array_san_isidro_140121_barangays);
        cities.put("ABRA-SAN JUAN", R.array.array_san_juan_140122_barangays);
        cities.put("ABRA-SAN QUINTIN", R.array.array_san_quintin_140123_barangays);
        cities.put("TAYUM", R.array.array_tayum_140124_barangays);
        cities.put("TINEG", R.array.array_tineg_140125_barangays);
        cities.put("TUBO", R.array.array_tubo_140126_barangays);
        cities.put("VILLAVICIOSA", R.array.array_villaviciosa_140127_barangays);
        cities.put("ATOK", R.array.array_atok_141101_barangays);
        cities.put("BAGUIO CITY", R.array.array_baguio_city_141102_barangays);
        cities.put("BAKUN", R.array.array_bakun_141103_barangays);
        cities.put("BOKOD", R.array.array_bokod_141104_barangays);
        cities.put("BUGUIAS", R.array.array_buguias_141105_barangays);
        cities.put("ITOGON", R.array.array_itogon_141106_barangays);
        cities.put("KABAYAN", R.array.array_kabayan_141107_barangays);
        cities.put("KAPANGAN", R.array.array_kapangan_141108_barangays);
        cities.put("KIBUNGAN", R.array.array_kibungan_141109_barangays);
        cities.put("LA TRINIDAD (Capital)", R.array.array_la_trinidad_capital_141110_barangays);
        cities.put("MANKAYAN", R.array.array_mankayan_141111_barangays);
        cities.put("SABLAN", R.array.array_sablan_141112_barangays);
        cities.put("TUBA", R.array.array_tuba_141113_barangays);
        cities.put("TUBLAY", R.array.array_tublay_141114_barangays);
        cities.put("BANAUE", R.array.array_banaue_142701_barangays);
        cities.put("HUNGDUAN", R.array.array_hungduan_142702_barangays);
        cities.put("KIANGAN", R.array.array_kiangan_142703_barangays);
        cities.put("LAGAWE (Capital)", R.array.array_lagawe_capital_142704_barangays);
        cities.put("LAMUT", R.array.array_lamut_142705_barangays);
        cities.put("MAYOYAO", R.array.array_mayoyao_142706_barangays);
        cities.put("ALFONSO LISTA (POTIA)", R.array.array_alfonso_lista_potia_142707_barangays);
        cities.put("AGUINALDO", R.array.array_aguinaldo_142708_barangays);
        cities.put("HINGYON", R.array.array_hingyon_142709_barangays);
        cities.put("TINOC", R.array.array_tinoc_142710_barangays);
        cities.put("ASIPULO", R.array.array_asipulo_142711_barangays);
        cities.put("BALBALAN", R.array.array_balbalan_143201_barangays);
        cities.put("LUBUAGAN", R.array.array_lubuagan_143206_barangays);
        cities.put("PASIL", R.array.array_pasil_143208_barangays);
        cities.put("PINUKPUK", R.array.array_pinukpuk_143209_barangays);
        cities.put("RIZAL (LIWAN)", R.array.array_rizal_liwan_143211_barangays);
        cities.put("CITY OF TABUK (Capital)", R.array.array_city_of_tabuk_capital_143213_barangays);
        cities.put("TANUDAN", R.array.array_tanudan_143214_barangays);
        cities.put("TINGLAYAN", R.array.array_tinglayan_143215_barangays);
        cities.put("BARLIG", R.array.array_barlig_144401_barangays);
        cities.put("BAUKO", R.array.array_bauko_144402_barangays);
        cities.put("BESAO", R.array.array_besao_144403_barangays);
        cities.put("BONTOC (Capital)", R.array.array_bontoc_capital_144404_barangays);
        cities.put("NATONIN", R.array.array_natonin_144405_barangays);
        cities.put("PARACELIS", R.array.array_paracelis_144406_barangays);
        cities.put("SABANGAN", R.array.array_sabangan_144407_barangays);
        cities.put("SADANGA", R.array.array_sadanga_144408_barangays);
        cities.put("SAGADA", R.array.array_sagada_144409_barangays);
        cities.put("TADIAN", R.array.array_tadian_144410_barangays);
        cities.put("CALANASAN (BAYAG)", R.array.array_calanasan_bayag_148101_barangays);
        cities.put("CONNER", R.array.array_conner_148102_barangays);
        cities.put("FLORA", R.array.array_flora_148103_barangays);
        cities.put("KABUGAO (Capital)", R.array.array_kabugao_capital_148104_barangays);
        cities.put("APAYAO-LUNA", R.array.array_luna_148105_barangays);
        cities.put("PUDTOL", R.array.array_pudtol_148106_barangays);
        cities.put("SANTA MARCELA", R.array.array_santa_marcela_148107_barangays);
        cities.put("CITY OF LAMITAN", R.array.array_city_of_lamitan_150702_barangays);
        cities.put("LANTAWAN", R.array.array_lantawan_150703_barangays);
        cities.put("MALUSO", R.array.array_maluso_150704_barangays);
        cities.put("SUMISIP", R.array.array_sumisip_150705_barangays);
        cities.put("TIPO-TIPO", R.array.array_tipo_tipo_150706_barangays);
        cities.put("BASILAN-TUBURAN", R.array.array_tuburan_150707_barangays);
        cities.put("AKBAR", R.array.array_akbar_150708_barangays);
        cities.put("AL-BARKA", R.array.array_al_barka_150709_barangays);
        cities.put("HADJI MOHAMMAD AJUL", R.array.array_hadji_mohammad_ajul_150710_barangays);
        cities.put("UNGKAYA PUKAN", R.array.array_ungkaya_pukan_150711_barangays);
        cities.put("HADJI MUHTAMAD", R.array.array_hadji_muhtamad_150712_barangays);
        cities.put("TABUAN-LASA", R.array.array_tabuan_lasa_150713_barangays);
        cities.put("BACOLOD-KALAWI (BACOLOD GRANDE)", R.array.array_bacolod_kalawi_bacolod_grande_153601_barangays);
        cities.put("BALABAGAN", R.array.array_balabagan_153602_barangays);
        cities.put("BALINDONG (WATU)", R.array.array_balindong_watu_153603_barangays);
        cities.put("BAYANG", R.array.array_bayang_153604_barangays);
        cities.put("BINIDAYAN", R.array.array_binidayan_153605_barangays);
        cities.put("BUBONG", R.array.array_bubong_153606_barangays);
        cities.put("BUTIG", R.array.array_butig_153607_barangays);
        cities.put("GANASSI", R.array.array_ganassi_153609_barangays);
        cities.put("KAPAI", R.array.array_kapai_153610_barangays);
        cities.put("LUMBA-BAYABAO (MAGUING)", R.array.array_lumba_bayabao_maguing_153611_barangays);
        cities.put("LUMBATAN", R.array.array_lumbatan_153612_barangays);
        cities.put("MADALUM", R.array.array_madalum_153613_barangays);
        cities.put("MADAMBA", R.array.array_madamba_153614_barangays);
        cities.put("MALABANG", R.array.array_malabang_153615_barangays);
        cities.put("MARANTAO", R.array.array_marantao_153616_barangays);
        cities.put("MARAWI CITY (Capital)", R.array.array_marawi_city_capital_153617_barangays);
        cities.put("MASIU", R.array.array_masiu_153618_barangays);
        cities.put("MULONDO", R.array.array_mulondo_153619_barangays);
        cities.put("PAGAYAWAN (TATARIKAN)", R.array.array_pagayawan_tatarikan_153620_barangays);
        cities.put("PIAGAPO", R.array.array_piagapo_153621_barangays);
        cities.put("POONA BAYABAO (GATA)", R.array.array_poona_bayabao_gata_153622_barangays);
        cities.put("PUALAS", R.array.array_pualas_153623_barangays);
        cities.put("DITSAAN-RAMAIN", R.array.array_ditsaan_ramain_153624_barangays);
        cities.put("SAGUIARAN", R.array.array_saguiaran_153625_barangays);
        cities.put("TAMPARAN", R.array.array_tamparan_153626_barangays);
        cities.put("TARAKA", R.array.array_taraka_153627_barangays);
        cities.put("TUBARAN", R.array.array_tubaran_153628_barangays);
        cities.put("TUGAYA", R.array.array_tugaya_153629_barangays);
        cities.put("WAO", R.array.array_wao_153630_barangays);
        cities.put("MAROGONG", R.array.array_marogong_153631_barangays);
        cities.put("CALANOGAS", R.array.array_calanogas_153632_barangays);
        cities.put("BUADIPOSO-BUNTONG", R.array.array_buadiposo_buntong_153633_barangays);
        cities.put("MAGUING", R.array.array_maguing_153634_barangays);
        cities.put("PICONG (SULTAN GUMANDER)", R.array.array_picong_sultan_gumander_153635_barangays);
        cities.put("LUMBAYANAGUE", R.array.array_lumbayanague_153636_barangays);
        cities.put("BUMBARAN", R.array.array_bumbaran_153637_barangays);
        cities.put("TAGOLOAN II", R.array.array_tagoloan_ii_153638_barangays);
        cities.put("LANAO DEL SUR-KAPATAGAN", R.array.array_kapatagan_153639_barangays);
        cities.put("SULTAN DUMALONDONG", R.array.array_sultan_dumalondong_153640_barangays);
        cities.put("LUMBACA-UNAYAN", R.array.array_lumbaca_unayan_153641_barangays);
        cities.put("AMPATUAN", R.array.array_ampatuan_153801_barangays);
        cities.put("BULDON", R.array.array_buldon_153802_barangays);
        cities.put("BULUAN", R.array.array_buluan_153803_barangays);
        cities.put("DATU PAGLAS", R.array.array_datu_paglas_153805_barangays);
        cities.put("DATU PIANG", R.array.array_datu_piang_153806_barangays);
        cities.put("DATU ODIN SINSUAT (DINAIG)", R.array.array_datu_odin_sinsuat_dinaig_153807_barangays);
        cities.put("SHARIFF AGUAK (MAGANOY) (Capital)", R.array.array_shariff_aguak_maganoy_capital_153808_barangays);
        cities.put("MATANOG", R.array.array_matanog_153809_barangays);
        cities.put("PAGALUNGAN", R.array.array_pagalungan_153810_barangays);
        cities.put("MAGUINDANAO-PARANG", R.array.array_parang_153811_barangays);
        cities.put("SULTAN KUDARAT (NULING)", R.array.array_sultan_kudarat_nuling_153812_barangays);
        cities.put("SULTAN SA BARONGIS (LAMBAYONG)", R.array.array_sultan_sa_barongis_lambayong_153813_barangays);
        cities.put("KABUNTALAN (TUMBAO)", R.array.array_kabuntalan_tumbao_153814_barangays);
        cities.put("UPI", R.array.array_upi_153815_barangays);
        cities.put("TALAYAN", R.array.array_talayan_153816_barangays);
        cities.put("SOUTH UPI", R.array.array_south_upi_153817_barangays);
        cities.put("BARIRA", R.array.array_barira_153818_barangays);
        cities.put("GEN. S. K. PENDATUN", R.array.array_gen_s_k_pendatun_153819_barangays);
        cities.put("MAMASAPANO", R.array.array_mamasapano_153820_barangays);
        cities.put("TALITAY", R.array.array_talitay_153821_barangays);
        cities.put("PAGAGAWAN", R.array.array_pagagawan_153822_barangays);
        cities.put("PAGLAT", R.array.array_paglat_153823_barangays);
        cities.put("SULTAN MASTURA", R.array.array_sultan_mastura_153824_barangays);
        cities.put("GUINDULUNGAN", R.array.array_guindulungan_153825_barangays);
        cities.put("DATU SAUDI-AMPATUAN", R.array.array_datu_saudi_ampatuan_153826_barangays);
        cities.put("DATU UNSAY", R.array.array_datu_unsay_153827_barangays);
        cities.put("DATU ABDULLAH SANGKI", R.array.array_datu_abdullah_sangki_153828_barangays);
        cities.put("RAJAH BUAYAN", R.array.array_rajah_buayan_153829_barangays);
        cities.put("DATU BLAH T. SINSUAT", R.array.array_datu_blah_t_sinsuat_153830_barangays);
        cities.put("DATU ANGGAL MIDTIMBANG", R.array.array_datu_anggal_midtimbang_153831_barangays);
        cities.put("MANGUDADATU", R.array.array_mangudadatu_153832_barangays);
        cities.put("PANDAG", R.array.array_pandag_153833_barangays);
        cities.put("NORTHERN KABUNTALAN", R.array.array_northern_kabuntalan_153834_barangays);
        cities.put("DATU HOFFER AMPATUAN", R.array.array_datu_hoffer_ampatuan_153835_barangays);
        cities.put("DATU SALIBO", R.array.array_datu_salibo_153836_barangays);
        cities.put("SHARIFF SAYDONA MUSTAPHA", R.array.array_shariff_saydona_mustapha_153837_barangays);
        cities.put("INDANAN", R.array.array_indanan_156601_barangays);
        cities.put("JOLO (Capital)", R.array.array_jolo_capital_156602_barangays);
        cities.put("KALINGALAN CALUANG", R.array.array_kalingalan_caluang_156603_barangays);
        cities.put("LUUK", R.array.array_luuk_156604_barangays);
        cities.put("MAIMBUNG", R.array.array_maimbung_156605_barangays);
        cities.put("HADJI PANGLIMA TAHIL (MARUNGGAS)", R.array.array_hadji_panglima_tahil_marunggas_156606_barangays);
        cities.put("OLD PANAMAO", R.array.array_old_panamao_156607_barangays);
        cities.put("PANGUTARAN", R.array.array_pangutaran_156608_barangays);
        cities.put("SULU-PARANG", R.array.array_parang_156609_barangays);
        cities.put("PATA", R.array.array_pata_156610_barangays);
        cities.put("PATIKUL", R.array.array_patikul_156611_barangays);
        cities.put("SIASI", R.array.array_siasi_156612_barangays);
        cities.put("TALIPAO", R.array.array_talipao_156613_barangays);
        cities.put("TAPUL", R.array.array_tapul_156614_barangays);
        cities.put("TONGKIL", R.array.array_tongkil_156615_barangays);
        cities.put("PANGLIMA ESTINO (NEW PANAMAO)", R.array.array_panglima_estino_new_panamao_156616_barangays);
        cities.put("LUGUS", R.array.array_lugus_156617_barangays);
        cities.put("PANDAMI", R.array.array_pandami_156618_barangays);
        cities.put("OMAR", R.array.array_omar_156619_barangays);
        cities.put("PANGLIMA SUGALA (BALIMBING)", R.array.array_panglima_sugala_balimbing_157001_barangays);
        cities.put("BONGAO (Capital)", R.array.array_bongao_capital_157002_barangays);
        cities.put("MAPUN (CAGAYAN DE TAWI-TAWI)", R.array.array_mapun_cagayan_de_tawi_tawi_157003_barangays);
        cities.put("SIMUNUL", R.array.array_simunul_157004_barangays);
        cities.put("SITANGKAI", R.array.array_sitangkai_157005_barangays);
        cities.put("SOUTH UBIAN", R.array.array_south_ubian_157006_barangays);
        cities.put("TANDUBAS", R.array.array_tandubas_157007_barangays);
        cities.put("TURTLE ISLANDS", R.array.array_turtle_islands_157008_barangays);
        cities.put("LANGUYAN", R.array.array_languyan_157009_barangays);
        cities.put("SAPA-SAPA", R.array.array_sapa_sapa_157010_barangays);
        cities.put("SIBUTU", R.array.array_sibutu_157011_barangays);
        cities.put("AGUSAN DEL NORTE-BUENAVISTA", R.array.array_buenavista_160201_barangays);
        cities.put("BUTUAN CITY (Capital)", R.array.array_butuan_city_capital_160202_barangays);
        cities.put("CITY OF CABADBARAN", R.array.array_city_of_cabadbaran_160203_barangays);
        cities.put("AGUSAN DEL NORTE-CARMEN", R.array.array_carmen_160204_barangays);
        cities.put("JABONGA", R.array.array_jabonga_160205_barangays);
        cities.put("KITCHARAO", R.array.array_kitcharao_160206_barangays);
        cities.put("LAS NIEVES", R.array.array_las_nieves_160207_barangays);
        cities.put("AGUSAN DEL NORTE-MAGALLANES", R.array.array_magallanes_160208_barangays);
        cities.put("NASIPIT", R.array.array_nasipit_160209_barangays);
        cities.put("AGUSAN DEL NORTE-SANTIAGO", R.array.array_santiago_160210_barangays);
        cities.put("TUBAY", R.array.array_tubay_160211_barangays);
        cities.put("REMEDIOS T. ROMUALDEZ", R.array.array_remedios_t_romualdez_160212_barangays);
        cities.put("CITY OF BAYUGAN", R.array.array_city_of_bayugan_160301_barangays);
        cities.put("BUNAWAN", R.array.array_bunawan_160302_barangays);
        cities.put("AGUSAN DEL SUR-ESPERANZA", R.array.array_esperanza_160303_barangays);
        cities.put("AGUSAN DEL SUR-LA PAZ", R.array.array_la_paz_160304_barangays);
        cities.put("AGUSAN DEL SUR-LORETO", R.array.array_loreto_160305_barangays);
        cities.put("PROSPERIDAD (Capital)", R.array.array_prosperidad_capital_160306_barangays);
        cities.put("AGUSAN DEL SUR-ROSARIO", R.array.array_rosario_160307_barangays);
        cities.put("AGUSAN DEL SUR-SAN FRANCISCO", R.array.array_san_francisco_160308_barangays);
        cities.put("AGUSAN DEL SUR-SAN LUIS", R.array.array_san_luis_160309_barangays);
        cities.put("SANTA JOSEFA", R.array.array_santa_josefa_160310_barangays);
        cities.put("TALACOGON", R.array.array_talacogon_160311_barangays);
        cities.put("TRENTO", R.array.array_trento_160312_barangays);
        cities.put("VERUELA", R.array.array_veruela_160313_barangays);
        cities.put("SIBAGAT", R.array.array_sibagat_160314_barangays);
        cities.put("SURIGAO DEL NORTE-ALEGRIA", R.array.array_alegria_166701_barangays);
        cities.put("BACUAG", R.array.array_bacuag_166702_barangays);
        cities.put("SURIGAO DEL NORTE-BURGOS", R.array.array_burgos_166704_barangays);
        cities.put("CLAVER", R.array.array_claver_166706_barangays);
        cities.put("DAPA", R.array.array_dapa_166707_barangays);
        cities.put("DEL CARMEN", R.array.array_del_carmen_166708_barangays);
        cities.put("SURIGAO DEL NORTE-GENERAL LUNA", R.array.array_general_luna_166710_barangays);
        cities.put("GIGAQUIT", R.array.array_gigaquit_166711_barangays);
        cities.put("MAINIT", R.array.array_mainit_166714_barangays);
        cities.put("MALIMONO", R.array.array_malimono_166715_barangays);
        cities.put("SURIGAO DEL NORTE-PILAR", R.array.array_pilar_166716_barangays);
        cities.put("SURIGAO DEL NORTE-PLACER", R.array.array_placer_166717_barangays);
        cities.put("SAN BENITO", R.array.array_san_benito_166718_barangays);
        cities.put("SAN FRANCISCO (ANAO-AON)", R.array.array_san_francisco_anao_aon_166719_barangays);
        cities.put("SURIGAO DEL NORTE-SAN ISIDRO", R.array.array_san_isidro_166720_barangays);
        cities.put("SANTA MONICA (SAPAO)", R.array.array_santa_monica_sapao_166721_barangays);
        cities.put("SURIGAO DEL NORTE-SISON", R.array.array_sison_166722_barangays);
        cities.put("SURIGAO DEL NORTE-SOCORRO", R.array.array_socorro_166723_barangays);
        cities.put("SURIGAO CITY (Capital)", R.array.array_surigao_city_capital_166724_barangays);
        cities.put("TAGANA-AN", R.array.array_tagana_an_166725_barangays);
        cities.put("TUBOD", R.array.array_tubod_166727_barangays);
        cities.put("BAROBO", R.array.array_barobo_166801_barangays);
        cities.put("BAYABAS", R.array.array_bayabas_166802_barangays);
        cities.put("CITY OF BISLIG", R.array.array_city_of_bislig_166803_barangays);
        cities.put("CAGWAIT", R.array.array_cagwait_166804_barangays);
        cities.put("CANTILAN", R.array.array_cantilan_166805_barangays);
        cities.put("SURIGAO DEL SUR-CARMEN", R.array.array_carmen_166806_barangays);
        cities.put("CARRASCAL", R.array.array_carrascal_166807_barangays);
        cities.put("SURIGAO DEL SUR-CORTES", R.array.array_cortes_166808_barangays);
        cities.put("HINATUAN", R.array.array_hinatuan_166809_barangays);
        cities.put("LANUZA", R.array.array_lanuza_166810_barangays);
        cities.put("LIANGA", R.array.array_lianga_166811_barangays);
        cities.put("LINGIG", R.array.array_lingig_166812_barangays);
        cities.put("MADRID", R.array.array_madrid_166813_barangays);
        cities.put("MARIHATAG", R.array.array_marihatag_166814_barangays);
        cities.put("SURIGAO DEL SUR-SAN AGUSTIN", R.array.array_san_agustin_166815_barangays);
        cities.put("SURIGAO DEL SUR-SAN MIGUEL", R.array.array_san_miguel_166816_barangays);
        cities.put("TAGBINA", R.array.array_tagbina_166817_barangays);
        cities.put("TAGO", R.array.array_tago_166818_barangays);
        cities.put("CITY OF TANDAG (Capital)", R.array.array_city_of_tandag_capital_166819_barangays);
        cities.put("BASILISA (RIZAL)", R.array.array_basilisa_rizal_168501_barangays);
        cities.put("CAGDIANAO", R.array.array_cagdianao_168502_barangays);
        cities.put("DINAGAT", R.array.array_dinagat_168503_barangays);
        cities.put("LIBJO (ALBOR)", R.array.array_libjo_albor_168504_barangays);
        cities.put("DINAGAT ISLANDS-LORETO", R.array.array_loreto_168505_barangays);
        cities.put("DINAGAT ISLANDS-SAN JOSE (Capital)", R.array.array_san_jose_capital_168506_barangays);
        cities.put("TUBAJON", R.array.array_tubajon_168507_barangays);
    }

    public void mapProvinces() {
        provinces.put("Select your province", R.array.array_default_cities);
        provinces.put("ILOCOS NORTE", R.array.array_ilocos_norte_128_cities);
        provinces.put("ILOCOS SUR", R.array.array_ilocos_sur_129_cities);
        provinces.put("LA UNION", R.array.array_la_union_133_cities);
        provinces.put("PANGASINAN", R.array.array_pangasinan_155_cities);
        provinces.put("BATANES", R.array.array_batanes_209_cities);
        provinces.put("CAGAYAN", R.array.array_cagayan_215_cities);
        provinces.put("ISABELA", R.array.array_isabela_231_cities);
        provinces.put("NUEVA VIZCAYA", R.array.array_nueva_vizcaya_250_cities);
        provinces.put("QUIRINO", R.array.array_quirino_257_cities);
        provinces.put("BATAAN", R.array.array_bataan_308_cities);
        provinces.put("BULACAN", R.array.array_bulacan_314_cities);
        provinces.put("NUEVA ECIJA", R.array.array_nueva_ecija_349_cities);
        provinces.put("PAMPANGA", R.array.array_pampanga_354_cities);
        provinces.put("TARLAC", R.array.array_tarlac_369_cities);
        provinces.put("ZAMBALES", R.array.array_zambales_371_cities);
        provinces.put("AURORA", R.array.array_aurora_377_cities);
        provinces.put("BATANGAS", R.array.array_batangas_410_cities);
        provinces.put("CAVITE", R.array.array_cavite_421_cities);
        provinces.put("LAGUNA", R.array.array_laguna_434_cities);
        provinces.put("QUEZON", R.array.array_quezon_456_cities);
        provinces.put("RIZAL", R.array.array_rizal_458_cities);
        provinces.put("MARINDUQUE", R.array.array_marinduque_1740_cities);
        provinces.put("OCCIDENTAL MINDORO", R.array.array_occidental_mindoro_1751_cities);
        provinces.put("ORIENTAL MINDORO", R.array.array_oriental_mindoro_1752_cities);
        provinces.put("PALAWAN", R.array.array_palawan_1753_cities);
        provinces.put("ROMBLON", R.array.array_romblon_1759_cities);
        provinces.put("ALBAY", R.array.array_albay_505_cities);
        provinces.put("CAMARINES NORTE", R.array.array_camarines_norte_516_cities);
        provinces.put("CAMARINES SUR", R.array.array_camarines_sur_517_cities);
        provinces.put("CATANDUANES", R.array.array_catanduanes_520_cities);
        provinces.put("MASBATE", R.array.array_masbate_541_cities);
        provinces.put("SORSOGON", R.array.array_sorsogon_562_cities);
        provinces.put("AKLAN", R.array.array_aklan_604_cities);
        provinces.put("ANTIQUE", R.array.array_antique_606_cities);
        provinces.put("CAPIZ", R.array.array_capiz_619_cities);
        provinces.put("ILOILO", R.array.array_iloilo_630_cities);
        provinces.put("NEGROS OCCIDENTAL", R.array.array_negros_occidental_645_cities);
        provinces.put("GUIMARAS", R.array.array_guimaras_679_cities);
        provinces.put("BOHOL", R.array.array_bohol_712_cities);
        provinces.put("CEBU", R.array.array_cebu_722_cities);
        provinces.put("NEGROS ORIENTAL", R.array.array_negros_oriental_746_cities);
        provinces.put("SIQUIJOR", R.array.array_siquijor_761_cities);
        provinces.put("EASTERN SAMAR", R.array.array_eastern_samar_826_cities);
        provinces.put("LEYTE", R.array.array_leyte_837_cities);
        provinces.put("NORTHERN SAMAR", R.array.array_northern_samar_848_cities);
        provinces.put("SAMAR (WESTERN SAMAR)", R.array.array_samar_western_samar_860_cities);
        provinces.put("SOUTHERN LEYTE", R.array.array_southern_leyte_864_cities);
        provinces.put("BILIRAN", R.array.array_biliran_878_cities);
        provinces.put("ZAMBOANGA DEL NORTE", R.array.array_zamboanga_del_norte_972_cities);
        provinces.put("ZAMBOANGA DEL SUR", R.array.array_zamboanga_del_sur_973_cities);
        provinces.put("ZAMBOANGA SIBUGAY", R.array.array_zamboanga_sibugay_983_cities);
        provinces.put("CITY OF ISABELA", R.array.array_city_of_isabela_997_cities);
        provinces.put("BUKIDNON", R.array.array_bukidnon_1013_cities);
        provinces.put("CAMIGUIN", R.array.array_camiguin_1018_cities);
        provinces.put("LANAO DEL NORTE", R.array.array_lanao_del_norte_1035_cities);
        provinces.put("MISAMIS OCCIDENTAL", R.array.array_misamis_occidental_1042_cities);
        provinces.put("MISAMIS ORIENTAL", R.array.array_misamis_oriental_1043_cities);
        provinces.put("DAVAO DEL NORTE", R.array.array_davao_del_norte_1123_cities);
        provinces.put("DAVAO DEL SUR", R.array.array_davao_del_sur_1124_cities);
        provinces.put("DAVAO ORIENTAL", R.array.array_davao_oriental_1125_cities);
        provinces.put("COMPOSTELA VALLEY", R.array.array_compostela_valley_1182_cities);
        provinces.put("DAVAO OCCIDENTAL", R.array.array_davao_occidental_1186_cities);
        provinces.put("COTABATO (NORTH COTABATO)", R.array.array_cotabato_north_cotabato_1247_cities);
        provinces.put("SOUTH COTABATO", R.array.array_south_cotabato_1263_cities);
        provinces.put("SULTAN KUDARAT", R.array.array_sultan_kudarat_1265_cities);
        provinces.put("SARANGANI", R.array.array_sarangani_1280_cities);
        provinces.put("COTABATO CITY", R.array.array_cotabato_city_1298_cities);
        provinces.put("NCR, CITY OF MANILA, FIRST DISTRICT", R.array.array_ncr_city_of_manila_first_district_1339_cities);
        provinces.put("CITY OF MANILA", R.array.array_city_of_manila_1339_cities);
        provinces.put("NCR, SECOND DISTRICT", R.array.array_ncr_second_district_1374_cities);
        provinces.put("NCR, THIRD DISTRICT", R.array.array_ncr_third_district_1375_cities);
        provinces.put("NCR, FOURTH DISTRICT", R.array.array_ncr_fourth_district_1376_cities);
        provinces.put("ABRA", R.array.array_abra_1401_cities);
        provinces.put("BENGUET", R.array.array_benguet_1411_cities);
        provinces.put("IFUGAO", R.array.array_ifugao_1427_cities);
        provinces.put("KALINGA", R.array.array_kalinga_1432_cities);
        provinces.put("MOUNTAIN PROVINCE", R.array.array_mountain_province_1444_cities);
        provinces.put("APAYAO", R.array.array_apayao_1481_cities);
        provinces.put("BASILAN", R.array.array_basilan_1507_cities);
        provinces.put("LANAO DEL SUR", R.array.array_lanao_del_sur_1536_cities);
        provinces.put("MAGUINDANAO", R.array.array_maguindanao_1538_cities);
        provinces.put("SULU", R.array.array_sulu_1566_cities);
        provinces.put("TAWI-TAWI", R.array.array_tawi_tawi_1570_cities);
        provinces.put("AGUSAN DEL NORTE", R.array.array_agusan_del_norte_1602_cities);
        provinces.put("AGUSAN DEL SUR", R.array.array_agusan_del_sur_1603_cities);
        provinces.put("SURIGAO DEL NORTE", R.array.array_surigao_del_norte_1667_cities);
        provinces.put("SURIGAO DEL SUR", R.array.array_surigao_del_sur_1668_cities);
        provinces.put("DINAGAT ISLANDS", R.array.array_dinagat_islands_1685_cities);
    }

}