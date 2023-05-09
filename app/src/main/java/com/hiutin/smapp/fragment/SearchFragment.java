package com.hiutin.smapp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hiutin.smapp.MainActivity;
import com.hiutin.smapp.adapter.SearchAdapter;
import com.hiutin.smapp.databinding.FragmentSearchBinding;
import com.hiutin.smapp.data.model.UserModel;
import com.hiutin.smapp.viewModel.ProfileFragmentViewModel;
import com.hiutin.smapp.viewModel.SearchFragmentViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
    private List<UserModel> users;
    private SearchAdapter adapter;
    private SearchFragmentViewModel viewModel;
    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SearchFragmentViewModel.class);
        users = new ArrayList<>();
        adapter = new SearchAdapter(requireContext(), users);
        binding.userRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.userRecyclerView.setAdapter(adapter);
        loadInitData();
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.getSearchResultMutableLiveData(newText).observe(getViewLifecycleOwner(), results -> {
                    users.clear();
                    users.addAll(results);
                    adapter.notifyDataSetChanged();
                });
                return false;
            }
        });
        adapter.onItemClick(uid -> {
            ProfileFragmentViewModel viewModel = new ViewModelProvider(requireActivity()).get(ProfileFragmentViewModel.class);
            viewModel.setUserId(uid);
            MainActivity.setFragment(4);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadInitData() {
        viewModel.getAllUsersMutableLiveData().observe(getViewLifecycleOwner(), allUser -> {
            users.clear();
            users.addAll(allUser);
            adapter.notifyDataSetChanged();
        });
    }
}