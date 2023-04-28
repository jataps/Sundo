package com.example.appsundo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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

    private int position = 0;

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
        vpAdapter.addFragment(new DriverFragmentOnBoard(), "On Board");
        vpAdapter.addFragment(new DriverFragmentArrived(), "Arrived");
        vpAdapter.addFragment(new DriverFragmentAssignStudent(), "Add student");

        viewPager.setAdapter(vpAdapter);

        String[] titles = new String[vpAdapter.getFragmentTitle().size()];

        titles = vpAdapter.getFragmentTitle().toArray(titles);

        String[] finalTitles = titles;

        new TabLayoutMediator(tabLayout, viewPager, ((tab, position) -> tab.setText(finalTitles[position]))).attach();

        if (args != null && args.containsKey("service_to_display")) {

            String message = args.getString("service_to_display");

            switch (message) {

                case "pickup_student":
                    position = 0;
                    break;

                case "onboard_student":
                    position = 1;
                    break;

                case "arrived_student":
                    position = 2;
                    break;

                case "add_student":
                    position = 3;
                    break;

            }

            viewPager.setCurrentItem(position);

        }

        return view;
    }
}