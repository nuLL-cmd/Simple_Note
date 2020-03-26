package com.example.simplenote.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.simplenote.fragments.InactiveFragment;
import com.example.simplenote.fragments.ActiveFragment;

public class AdapterFragment extends FragmentPagerAdapter {
    int tabs;
    public AdapterFragment(@NonNull FragmentManager fm, int tabs) {
        super(fm);
        this.tabs = tabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ActiveFragment();
            case 1:
                return new InactiveFragment();
            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return tabs;
    }
}
