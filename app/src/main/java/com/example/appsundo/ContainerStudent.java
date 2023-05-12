package com.example.appsundo;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.appsundo.databinding.ActivityContainerStudentBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContainerStudent extends AppCompatActivity {

    ActivityResultLauncher<String[]> mPermissionResultLauncher;
    private boolean isLocationPermissionGranted = false;
    ActivityContainerStudentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {

                if (result.get(Manifest.permission.ACCESS_FINE_LOCATION) != null) {
                    isLocationPermissionGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                }

            }
        });

        requestPermission();

        binding = ActivityContainerStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().hasExtra("fragment_to_display")) {
            String fragmentToDisplay = getIntent().getStringExtra("fragment_to_display");

            switch (fragmentToDisplay) {
                case "fragment_profile":
                    replaceFragment(new StudentFragmentProfile());
                    break;

                case "fragment_records":
                    replaceFragment(new StudentFragmentRecords());
                    break;

                case "fragment_track":
                    replaceFragment(new StudentFragmentTrack());
                    break;
                /*
                case "fragment_settings":
                    replaceFragment(new StudentFragmentSettings());
                    break;
                */
                default:
                    break;
            }
        } else {
            replaceFragment(new StudentFragmentHome());
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.navHome:
                    replaceFragment(new StudentFragmentHome());
                    break;

                case R.id.navTrack:
                    replaceFragment(new StudentFragmentTrack());
                    break;

                case R.id.navProfile:
                    replaceFragment(new StudentFragmentProfile());
                    break;

                /*case R.id.navSettings:
                    replaceFragment(new StudentFragmentSettings());
                    break;
                */
                case R.id.navRecords:
                    replaceFragment(new StudentFragmentRecords());
                    break;
            }
            return true;

        });

    }

    private void requestPermission() {
        isLocationPermissionGranted = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;

        List<String> permissionRequest = new ArrayList<>();

        if (!isLocationPermissionGranted) {
            permissionRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (!permissionRequest.isEmpty()) {
            mPermissionResultLauncher.launch(permissionRequest.toArray(new String[0]));
        }

    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ContainerStudent.super.onBackPressed();
                    }
                }).create().show();
    }
}