package com.example.appsundo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.os.Bundle;

import com.example.appsundo.databinding.ActivityContainerDriverBinding;

public class ContainerDriver extends AppCompatActivity {

    ActivityContainerDriverBinding binding;

    int x; //added for sample
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityContainerDriverBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        if (getIntent().hasExtra("fragment_to_display")) {
            String fragmentToDisplay = getIntent().getStringExtra("fragment_to_display");

            switch (fragmentToDisplay) {
                case "fragment_profile":
                    replaceFragment(new DriverFragmentProfile());
                    break;

                case "fragment_service":
                    replaceFragment(new DriverFragmentService());
                    break;

                case "fragment_records":
                    replaceFragment(new DriverFragmentRecords());
                    break;

                default:
                    break;
            }
        } else {
            replaceFragment(new DriverFragmentHome());
        }

        binding.bottomNavigationViewDriver.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.navHomeDriver:
                    replaceFragment(new DriverFragmentHome());
                    break;

                case R.id.navProfileDriver:
                    replaceFragment(new DriverFragmentProfile());
                    break;

                case R.id.navServiceDriver:
                    replaceFragment(new DriverFragmentService());
                    break;

                case R.id.navRecordsDriver:
                    replaceFragment(new DriverFragmentRecords());
                    break;
            }
            return true;

        });

    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_driver, fragment);
        fragmentTransaction.commit();

/*
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.container_fragment, new Fragment1(), "Fragment1");
        fragmentTransaction.commit();

        if (getIntent().hasExtra("fragment_to_display")) {
            String fragmentToDisplay = getIntent().getStringExtra("fragment_to_display");
            if (fragmentToDisplay.equals("fragment2")) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container_fragment, new Fragment2(), "Fragment2");
                transaction.commit();
            }
        }
*/

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ContainerDriver.super.onBackPressed();
                    }
                }).create().show();
    }
}