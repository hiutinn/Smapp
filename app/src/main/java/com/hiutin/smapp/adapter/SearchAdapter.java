package com.hiutin.smapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hiutin.smapp.R;
import com.hiutin.smapp.databinding.SearchItemBinding;
import com.hiutin.smapp.data.model.UserModel;
import com.hiutin.smapp.viewModel.ProfileFragmentViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private Context context;
    private List<UserModel> users;
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
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .addSnapshotListener((value, error) -> {
                    boolean followed = false;
                    ArrayList<String> following = (ArrayList<String>) value.get("following");
                    if (following != null) {
                        for (String uid : following) {
                            if (uid.equals(userModel.getUid())) {
                                followed = true;
                                break;
                            }
                        }
                    }
                    if (followed) {
                        holder.binding.btnFollow.setBackgroundResource(R.drawable.ic_check_box_24);
                    } else {
                        holder.binding.btnFollow.setBackgroundResource(R.drawable.ic_baseline_add_box_24);
                    }
                });
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
