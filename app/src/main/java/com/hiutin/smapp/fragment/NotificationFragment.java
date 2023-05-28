package com.hiutin.smapp.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hiutin.smapp.PostDetailActivity;
import com.hiutin.smapp.R;
import com.hiutin.smapp.adapter.NotificationAdapter;
import com.hiutin.smapp.adapter.SearchAdapter;
import com.hiutin.smapp.data.model.NotificationModel;
import com.hiutin.smapp.databinding.FragmentNotificationBinding;
import com.hiutin.smapp.viewModel.NotificationFragmentViewModel;
import com.hiutin.smapp.viewModel.SearchFragmentViewModel;

import java.util.ArrayList;
import java.util.List;


public class NotificationFragment extends Fragment {

    private NotificationFragmentViewModel viewModel;
    private List<NotificationModel> list ;
    private NotificationAdapter adapter;
    private FragmentNotificationBinding binding;
    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel =  new ViewModelProvider(requireActivity()).get(NotificationFragmentViewModel.class);
        list =  new ArrayList<>();
        adapter = new NotificationAdapter(getContext(), list);
        binding.recycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycleView.setAdapter(adapter);
        loadInitData();

        adapter.onItemClick(idPost->{
            Intent postDetailIntent = new Intent(getContext(), PostDetailActivity.class);
            postDetailIntent.putExtra("postId", idPost);
            getContext().startActivity(postDetailIntent);
        });

    }
    @SuppressLint("NotifyDataSetChanged")
    private void loadInitData() {
        viewModel.getAllNotification().observe(getViewLifecycleOwner(), notification -> {
            list.clear();
            list.addAll(notification);
            adapter.notifyDataSetChanged();
        });
    }
}