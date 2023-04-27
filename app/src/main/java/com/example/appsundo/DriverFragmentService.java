package com.example.appsundo;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.util.ArrayList;
import com.google.android.material.button.MaterialButton;

public class DriverFragmentService extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "service_to_display";
    private static final String ARG_PARAM2 = "param2";

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private VPAdapter vpAdapter;

    private String message;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_service, container, false);

        Bundle args = getArguments();
        if (args != null) {
//            message = args.getString("service_to_display");
        }

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        vpAdapter = new VPAdapter(getActivity());

        vpAdapter.addFragment(new DriverFragmentPickUp(), "Pickup");
        vpAdapter.addFragment(new DriverFragmentDropOff(), "Drop Off");
        vpAdapter.addFragment(new DriverFragmentAssignStudent(), "Add student");

        viewPager.setAdapter(vpAdapter);

        String[] titles = new String[vpAdapter.getFragmentTitle().size()];

        titles = vpAdapter.getFragmentTitle().toArray(titles);

        String[] finalTitles = titles;

        new TabLayoutMediator(tabLayout, viewPager, ((tab, position) -> tab.setText(finalTitles[position]))).attach();

        if (args != null && args.containsKey("service_to_display") && args.getString("service_to_display").equals("add_student")) {
            viewPager.setCurrentItem(2);
        }

        return view;
    }
}