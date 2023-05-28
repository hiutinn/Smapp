package com.hiutin.smapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hiutin.smapp.R;
import com.hiutin.smapp.data.model.UserModel;
import com.hiutin.smapp.databinding.SearchItemBinding;


import java.util.List;

public class UserFollowingAdapter extends RecyclerView.Adapter<UserFollowingAdapter.usersFollowingViewHolder> {

    private Context context;
    private List<UserModel> users;
    private IOnItemClick iOnItemClick;

    public UserFollowingAdapter(Context context, List<UserModel> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public usersFollowingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new usersFollowingViewHolder(LayoutInflater.from(context).inflate(R.layout.search_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull usersFollowingViewHolder holder, int position) {
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


    class usersFollowingViewHolder extends RecyclerView.ViewHolder {
        private final SearchItemBinding binding;
        public usersFollowingViewHolder(@NonNull View itemView) {
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
