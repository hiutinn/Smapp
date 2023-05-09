package com.hiutin.smapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.hiutin.smapp.databinding.ActivityFramentReplaceBinding;
import com.hiutin.smapp.fragment.CreateAccountFragment;
import com.hiutin.smapp.fragment.LoginFragment;

public class FragmentReplaceActivity extends AppCompatActivity {
    private ActivityFramentReplaceBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFramentReplaceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setFragment(new LoginFragment());
    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        if (fragment instanceof CreateAccountFragment) {
            transaction.addToBackStack(null);
        }

        transaction.replace(binding.frameLayout.getId(), fragment);
        transaction.commit();
    }
}