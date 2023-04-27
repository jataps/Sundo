package com.example.appsundo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.appsundo.databinding.ActivityContainerDriverBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ContainerDriver extends AppCompatActivity {
    
    ActivityContainerDriverBinding binding;

    BottomNavigationView bottomNavigationViewDriver;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityContainerDriverBinding.inflate(getLayoutInflater());

        bottomNavigationViewDriver = findViewById(R.id.bottomNavigationViewDriver);

        setContentView(binding.getRoot());

        if (getIntent().hasExtra("fragment_to_display")) {

            String fragmentToDisplay = getIntent().getStringExtra("fragment_to_display");

            switch (fragmentToDisplay) {
                case "fragment_profile":
                    replaceFragment(new DriverFragmentProfile());
                    break;

                case "fragment_service":
                    if (getIntent().hasExtra("service_to_display")) {
                        message = getIntent().getStringExtra("service_to_display");
                        replaceFragment(new DriverFragmentService(), "service_to_display", message );
                    } else
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

        Menu menu = binding.bottomNavigationViewDriver.getMenu();

        binding.bottomNavigationViewDriver.setOnItemSelectedListener(item -> {

            for (int i = 0; i < menu.size(); i++) {
                MenuItem menuItem = menu.getItem(i);
                if (menuItem.getItemId() != item.getItemId()) {
                    menuItem.setEnabled(true);
                } else {
                    menuItem.setEnabled(false);
                }
            }

            switch (item.getItemId()) {
                case R.id.navHomeDriver:

                    replaceFragment(new DriverFragmentHome());
                    break;

                case R.id.navProfileDriver:
                    replaceFragment(new DriverFragmentProfile());
                    break;

                case R.id.navServiceDriver:
                    if (getIntent().hasExtra("service_to_display")) {
                        message = getIntent().getStringExtra("service_to_display");
                        replaceFragment(new DriverFragmentService(), "service_to_display", message );
                    } else
                        replaceFragment(new DriverFragmentService());

                    break;
                case R.id.navRecordsDriver:
                    replaceFragment(new DriverFragmentRecords(), "service_to_display", message);
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

    }

    private void replaceFragment(Fragment fragment, String from, String mMessage) {
        Bundle args = new Bundle();
        args.putString(from, mMessage);

        getIntent().removeExtra(from);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment.setArguments(args);

        fragmentTransaction.replace(R.id.frame_layout_driver, fragment);
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
                        ContainerDriver.super.onBackPressed();
                    }
                }).create().show();
    }
}