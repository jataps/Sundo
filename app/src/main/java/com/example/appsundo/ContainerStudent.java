package com.example.appsundo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.os.Bundle;

import com.example.appsundo.databinding.ActivityContainerStudentBinding;

public class ContainerStudent extends AppCompatActivity {

    ActivityContainerStudentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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