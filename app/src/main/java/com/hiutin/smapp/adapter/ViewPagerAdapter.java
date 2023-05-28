package com.hiutin.smapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hiutin.smapp.fragment.AddFragment;
import com.hiutin.smapp.fragment.HomeFragment;
import com.hiutin.smapp.fragment.NotificationFragment;
import com.hiutin.smapp.fragment.ProfileFragment;
import com.hiutin.smapp.fragment.SearchFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new SearchFragment();
            case 2:
                return new AddFragment();
            case 3:
                return new NotificationFragment();
            case 4:
                return new ProfileFragment();

        }
        return new HomeFragment();
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
