package com.example.appsundo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

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

    private MaterialButton signOutBtnDriver,btnEditProfile,btnSaveProfile;

    private TextView txtFName;

    private TextInputEditText txtfieldFirstName,txtfieldLastName,txtfieldPhone,txtAddressNote,txtPlateNumber,txtSeatingCapacity;

    private Spinner provinceSpinner,citySpinner,barangaySpinner;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_driver_profile, container, false);

        signOutBtnDriver = view.findViewById(R.id.signOutBtnDriver);

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
}