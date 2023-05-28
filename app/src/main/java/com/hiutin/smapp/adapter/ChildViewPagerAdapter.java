package com.hiutin.smapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.hiutin.smapp.fragment.UserFollowFragment;

public class ChildViewPagerAdapter extends FragmentStateAdapter {

    public ChildViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                UserFollowFragment.check = false;
                return new UserFollowFragment();
            case 1:
                UserFollowFragment.check = true;
                return new UserFollowFragment();
        }
        return new UserFollowFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
