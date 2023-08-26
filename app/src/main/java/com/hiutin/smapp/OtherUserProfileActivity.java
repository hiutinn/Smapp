package com.hiutin.smapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.hiutin.smapp.adapter.ProfileImageAdapter;
import com.hiutin.smapp.data.model.PostModel;
import com.hiutin.smapp.databinding.ActivityOtherUserProfileBinding;
import com.hiutin.smapp.viewModel.ProfileFragmentViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OtherUserProfileActivity extends AppCompatActivity {
    private ActivityOtherUserProfileBinding binding;
    private static ProfileFragmentViewModel viewModel;
    ProfileImageAdapter adapter;
    List<PostModel> posts;
    private boolean checkFollowOnClick;
    public static int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtherUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        posts = new ArrayList<>();
        adapter = new ProfileImageAdapter(this, posts);
        binding.recyclerView.setAdapter(adapter);
        viewModel = new ViewModelProvider(this).get(ProfileFragmentViewModel.class);

        String uid = getIntent().getStringExtra("uid");
        if (uid != null) {
            loadBasicData(uid);
            loadPostImages(uid);
            binding.btnFollow.setVisibility(View.VISIBLE);
            binding.countLayout.setVisibility(View.GONE);
            binding.btnBack.setVisibility(View.VISIBLE);
            followButtonHandler(uid);
        }
        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadBasicData(String uid) {
        viewModel.getUserMutableLiveData(uid)
                .observe(this, user -> {
                    Glide.with(this).load(user.getAvatar()).centerCrop().timeout(7000).placeholder(R.drawable.user).into(binding.profileImage);
                    binding.tvUserName.setText(user.getName());
                    if (!Objects.equals(user.getStatus(), ""))
                        binding.tvStatus.setText(user.getStatus());
                    ArrayList<String> followers = user.getFollowers();
                    ArrayList<String> followings = user.getFollowing();
                    binding.tvFollowing.setText(String.valueOf(followings != null ? followings.size() : 0));
                    binding.tvFollowers.setText(String.valueOf(followers != null ? followers.size() : 0));
                });
    }

    private void followButtonHandler(String uid) {
        viewModel.getCheckFollowingMutableData(uid).observe(this, check -> {
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

    @SuppressLint("NotifyDataSetChanged")
    private void loadPostImages(String uid) {
        viewModel.getPostsMutableLiveData(uid).observe(this, mPosts -> {
            posts.clear();
            posts.addAll(mPosts);
            adapter.notifyDataSetChanged();
            binding.tvPosts.setText(String.valueOf(mPosts.size()));
        });
    }
}