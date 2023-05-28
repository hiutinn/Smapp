package com.hiutin.smapp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hiutin.smapp.MainActivity;
import com.hiutin.smapp.adapter.UserFollowingAdapter;
import com.hiutin.smapp.data.model.UserModel;
import com.hiutin.smapp.databinding.FragmentLoginBinding;
import com.hiutin.smapp.databinding.FragmentProfileBinding;
import com.hiutin.smapp.databinding.FragmentUserFollowBinding;
import com.hiutin.smapp.viewModel.ProfileFragmentViewModel;

import java.util.ArrayList;
import java.util.List;


public class UserFollowFragment extends Fragment {


    FragmentUserFollowBinding binding;
    private List<UserModel> users;
    private UserFollowingAdapter adapter;
    private ProfileFragmentViewModel viewModel;
    public static boolean check = false;

    public UserFollowFragment() {
        // Required empty public constructor
    }


    public static UserFollowFragment newInstance() {
        UserFollowFragment fragment = new UserFollowFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserFollowBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileFragmentViewModel.class);
        users = new ArrayList<>();
        adapter = new UserFollowingAdapter(requireContext(), users);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
        loadInitData();
        adapter.onItemClick(uid -> {
            ProfileFragmentViewModel viewModel = new ViewModelProvider(requireActivity()).get(ProfileFragmentViewModel.class);
            viewModel.setUserId(uid);
            MainActivity.setFragment(4);
            ProfileFragment.binding.childViewPager.setVisibility(View.GONE);
            ProfileFragment.binding.relative.setVisibility(View.VISIBLE);
            if(ProfileFragment.index == 1){
                ProfileFragment.index++;
            }
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    private void loadInitData() {
        if(check == false){
            viewModel.getAllFollowingUser().observe(getViewLifecycleOwner(), allUser -> {
                users.clear();
                users.addAll(allUser);
                adapter.notifyDataSetChanged();
            });
        }else{
            viewModel.getAllFollowerUser().observe(getViewLifecycleOwner(), allUser -> {
                users.clear();
                users.addAll(allUser);
                adapter.notifyDataSetChanged();
            });
        }
    }
}