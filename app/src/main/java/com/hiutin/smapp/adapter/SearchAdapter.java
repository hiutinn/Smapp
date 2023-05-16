package com.hiutin.smapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hiutin.smapp.R;
import com.hiutin.smapp.databinding.SearchItemBinding;
import com.hiutin.smapp.data.model.UserModel;
import com.hiutin.smapp.viewModel.ProfileFragmentViewModel;
import com.hiutin.smapp.viewModel.SearchFragmentViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private final Context context;
    private final List<UserModel> users;
    private IOnItemClick iOnItemClick;
    public SearchAdapter(Context context, List<UserModel> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchViewHolder(LayoutInflater.from(context).inflate(R.layout.search_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        UserModel userModel = users.get(position);
        Glide.with(context)
                .load(userModel.getAvatar())
                .placeholder(R.drawable.user)
                .into(holder.binding.imgUserAvatar);
        holder.binding.tvUserName.setText(userModel.getName());
        holder.itemView.setOnClickListener(v -> {
            iOnItemClick.onItemClick(userModel.getUid());
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        private final SearchItemBinding binding;
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SearchItemBinding.bind(itemView);
        }
    }

    public interface IOnItemClick {
        void onItemClick(String uid);
    }

    public void onItemClick(IOnItemClick iOnItemClick) {
        this.iOnItemClick = iOnItemClick;
    }
}
