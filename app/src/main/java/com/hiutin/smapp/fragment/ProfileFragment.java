package com.hiutin.smapp.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hiutin.smapp.FragmentReplaceActivity;
import com.hiutin.smapp.MainActivity;
import com.hiutin.smapp.R;
import com.hiutin.smapp.adapter.ProfileImageAdapter;
import com.hiutin.smapp.databinding.FragmentProfileBinding;
import com.hiutin.smapp.data.model.PostModel;
import com.hiutin.smapp.dialog.ConfirmDialog;
import com.hiutin.smapp.viewModel.ProfileFragmentViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {
    private FragmentProfileBinding binding;
    private boolean isMyProfile;
    private static ProfileFragmentViewModel viewModel;
    ProfileImageAdapter adapter;
    List<PostModel> posts;
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
            viewModel.setUserId(FirebaseAuth.getInstance().getUid());
            MainActivity.setFragment(0);
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
                return true;
            case R.id.profile_opt_logout:
                logout();
                return true;
            default:
                return false;
        }
    }

    private void logout() {
        ConfirmDialog confirmDialog = new ConfirmDialog(requireActivity(), "You want to logout ?", () -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(requireActivity(), FragmentReplaceActivity.class));
            requireActivity().finish();
        });
        confirmDialog.show();
    }
}