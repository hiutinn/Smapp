package com.hiutin.smapp;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hiutin.smapp.adapter.ViewPagerAdapter;
import com.hiutin.smapp.data.model.UserModel;
import com.hiutin.smapp.databinding.ActivityMainBinding;
import com.hiutin.smapp.fragment.AddFragment;
import com.hiutin.smapp.fragment.HomeFragment;
import com.hiutin.smapp.fragment.ProfileFragment;
import com.hiutin.smapp.viewModel.ProfileFragmentViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static ActivityMainBinding binding;
    private FirebaseFirestore db;
    public static String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        hideStatusBar();
        binding.viewPager2.setAdapter(new ViewPagerAdapter(MainActivity.this));
        new TabLayoutMediator(binding.tabLayout, binding.viewPager2, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setIcon(R.drawable.home);
                    break;
                case 1:
                    tab.setIcon(R.drawable.search);
                    break;
                case 2:
                    tab.setIcon(R.drawable.add);
                    break;
                case 3:
                    tab.setIcon(R.drawable.heart);
                    break;
                case 4:
                    tab.setIcon(R.drawable.user);
                    break;
            }
        }).attach();
//        binding.tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
//        binding.tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        tab.setIcon(R.drawable.home_fill);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.search);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.add);
                        break;
                    case 3:
                        tab.setIcon(R.drawable.heart_fill);
                        break;
                    case 4:
                        tab.setIcon(R.drawable.user_fill);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        tab.setIcon(R.drawable.home);
                        if (HomeFragment.bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                            HomeFragment.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                        break;
                    case 1:
                        tab.setIcon(R.drawable.search);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.add);
                        AddFragment.resetInput();
                        break;
                    case 3:
                        tab.setIcon(R.drawable.heart);
                        break;
                    case 4:
                        tab.setIcon(R.drawable.user);
                        ProfileFragment.resetProfileScreen();
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("MainActivity", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        token = task.getResult();
                        db.collection("users")
                                .document(FirebaseAuth.getInstance().getUid())
                                .update("token", token)
                                .addOnSuccessListener(aVoid -> {
                                        }
                                )
                                .addOnFailureListener(e -> {
                                            Log.e("Update", "update token fail");
                                        }
                                );
                    }
                });

    }

    //
    public static void setFragment(int position) {
        binding.viewPager2.setCurrentItem(position);
    }

    public void hideStatusBar() {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }


}