package com.hiutin.smapp.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.hiutin.smapp.FragmentReplaceActivity;
import com.hiutin.smapp.MainActivity;
import com.hiutin.smapp.ProfileDetailActivity;
import com.hiutin.smapp.R;
import com.hiutin.smapp.adapter.ProfileImageAdapter;
import com.hiutin.smapp.adapter.SearchAdapter;
import com.hiutin.smapp.adapter.ChildViewPagerAdapter;
import com.hiutin.smapp.adapter.ViewPagerAdapter;
import com.hiutin.smapp.data.model.UserModel;
import com.hiutin.smapp.databinding.FragmentProfileBinding;
import com.hiutin.smapp.data.model.PostModel;
import com.hiutin.smapp.dialog.ConfirmDialog;
import com.hiutin.smapp.viewModel.ProfileFragmentViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {
    public static FragmentProfileBinding binding;
    private boolean isMyProfile;
    private static ProfileFragmentViewModel viewModel;
    ProfileImageAdapter adapter;
    List<PostModel> posts;
    private boolean checkFollowOnClick;
    public static int index=0;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolBar);
        posts = new ArrayList<>();
        adapter = new ProfileImageAdapter(requireContext(), posts);
        binding.recyclerView.setAdapter(adapter);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileFragmentViewModel.class);
        viewModel.getUserIdMutableLiveData().observe(getViewLifecycleOwner(), uid -> {
            isMyProfile = Objects.equals(uid, FirebaseAuth.getInstance().getUid());

            loadBasicData(uid);
            loadPostImages(uid);
            if (isMyProfile) {
                binding.btnFollow.setVisibility(View.GONE);
                binding.countLayout.setVisibility(View.VISIBLE);
                binding.linearFollowing.setOnClickListener(view1 -> {
                    usersFollow();
                    binding.childViewPager.setVisibility(view.VISIBLE);
                    binding.btnBack.setVisibility(view.VISIBLE);
                    binding.relative.setVisibility(view.GONE);
                    MainActivity.binding.viewPager2.setUserInputEnabled(false);
                    setFragment(0);
                    checkFollowOnClick = true;
                    if(index==0){
                        index++;
                    }
                });
                binding.linearFollower.setOnClickListener(view1 -> {
                    usersFollow();
                    binding.childViewPager.setVisibility(view.VISIBLE);
                    binding.relative.setVisibility(view.GONE);
                    binding.btnBack.setVisibility(view.VISIBLE);
                    MainActivity.binding.viewPager2.setUserInputEnabled(false);
                    setFragment(1);
                    checkFollowOnClick = true;
                    if(index==0){
                        index++;
                    }
                });
                binding.btnBack.setVisibility(View.GONE);
                binding.imgMenu.setVisibility(View.VISIBLE);
            } else {
                binding.btnFollow.setVisibility(View.VISIBLE);
                binding.countLayout.setVisibility(View.GONE);
                binding.btnBack.setVisibility(View.VISIBLE);
                binding.imgMenu.setVisibility(View.GONE);
                followButtonHandler(uid);
            }
        });
        binding.btnBack.setOnClickListener(v -> {
            back(index);
            index--;
        });

        binding.imgMenu.setOnClickListener(this::showPopUpMenu);
    }

    private void followButtonHandler(String uid) {
        viewModel.getCheckFollowingMutableData(uid).observe(getViewLifecycleOwner(), check -> {
            if (check) {
                binding.btnFollow.setText("Following");
                binding.btnFollow.setOnClickListener(v -> {
                    viewModel.unfollow(uid);
                });
            } else {
                binding.btnFollow.setText("Follow");
                binding.btnFollow.setOnClickListener(v -> {
                    viewModel.follow(uid);
                });
            }
        });
    }


    public static void resetProfileScreen() {
        viewModel.setUserId(FirebaseAuth.getInstance().getUid());
    }

    private void loadBasicData(String uid) {
        viewModel.getUserMutableLiveData(uid)
                .observe(getViewLifecycleOwner(), user -> {
                    Glide.with(requireActivity()).load(user.getAvatar()).centerCrop().timeout(7000).placeholder(R.drawable.user).into(binding.profileImage);
                    binding.tvUserName.setText(user.getName());
                    if (!Objects.equals(user.getStatus(), "")) binding.tvStatus.setText(user.getStatus());
                    ArrayList<String> followers = user.getFollowers();
                    ArrayList<String> followings = user.getFollowing();
                    binding.tvFollowing.setText(String.valueOf(followings != null ? followings.size() : 0));
                    binding.tvFollowers.setText(String.valueOf(followers != null ? followers.size() : 0));
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadPostImages(String uid) {
        viewModel.getPostsMutableLiveData(uid).observe(getViewLifecycleOwner(), mPosts -> {
            posts.clear();
            posts.addAll(mPosts);
            adapter.notifyDataSetChanged();
            binding.tvPosts.setText(String.valueOf(mPosts.size()));
        });
    }

    private void showPopUpMenu(View v) {
        PopupMenu popup = new PopupMenu(requireContext(), v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.profile_option_menu);
        popup.show();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.profile_opt_edit:
                editProfile();
                return true;
            case R.id.profile_opt_logout:
                logout();
                return true;
            default:
                return false;
        }
    }

    private void editProfile() {
        Intent intent = new Intent(requireActivity(), ProfileDetailActivity.class);
        startActivity(intent);

    }

    private void logout() {
        ConfirmDialog confirmDialog = new ConfirmDialog(requireActivity(), "You want to logout ?", () -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(requireActivity(), FragmentReplaceActivity.class));
            requireActivity().finish();
        });
        confirmDialog.show();
    }

    private void usersFollow(){
        binding.viewPager2.setAdapter(new ChildViewPagerAdapter(getActivity()));
        TabLayoutMediator mediator = new TabLayoutMediator(binding.tabLayoutProfile, binding.viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText("Following");
                        break;
                    case 1:
                        tab.setText("Follower");
                        break;
                }
            }
        });
        mediator.attach();
    }
    private void setFragment(int position) {
        binding.viewPager2.setCurrentItem(position);
    }

    @Override
    public void onResume() {
        super.onResume();
//        if(checkFollowOnClick == true){
//            binding.btnBack.setVisibility(View.VISIBLE);
//        }
        if(index == 0){
            viewModel.setUserId(FirebaseAuth.getInstance().getUid());
            MainActivity.setFragment(4);
            binding.childViewPager.setVisibility(View.GONE);
            MainActivity.binding.viewPager2.setUserInputEnabled(true);
            binding.relative.setVisibility(View.VISIBLE);
            binding.btnBack.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        index = 0;
    }
    private void back(int position){
        switch (position){
            case 1:
                checkFollowOnClick = false;
                viewModel.setUserId(FirebaseAuth.getInstance().getUid());
                MainActivity.setFragment(4);
                binding.childViewPager.setVisibility(View.GONE);
                MainActivity.binding.viewPager2.setUserInputEnabled(true);
                binding.relative.setVisibility(View.VISIBLE);
                binding.btnBack.setVisibility(View.GONE);
                break;
            case 2:
                binding.childViewPager.setVisibility(View.VISIBLE);
                binding.relative.setVisibility(View.GONE);
                break;
        }
    }
}