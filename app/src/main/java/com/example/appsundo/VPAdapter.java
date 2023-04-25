package com.example.appsundo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class VPAdapter extends FragmentStateAdapter {


    private String [] titles = new String[] {"PICKUP", "DROPOFF", "ADD"};

    private final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();

    public ArrayList<String> getFragmentTitle() {

        return fragmentTitle;
    }

    private final ArrayList<String> fragmentTitle = new ArrayList<>();

    public VPAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        return fragmentArrayList.get(position);

    }

    @Override
    public int getItemCount() {
        return fragmentArrayList.size();
    }

    public void addFragment(Fragment fragment, String title) {

        fragmentArrayList.add(fragment);
        fragmentTitle.add(title);

    }

}
