package com.hiutin.smapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hiutin.smapp.PostDetailActivity;
import com.hiutin.smapp.R;
import com.hiutin.smapp.databinding.PostImageItemBinding;
import com.hiutin.smapp.data.model.PostModel;

import java.util.List;

public class ProfileImageAdapter extends RecyclerView.Adapter<ProfileImageAdapter.ProfileImageViewHolder> {

    private final Context context;
    private final List<PostModel> posts;

    public ProfileImageAdapter(Context context, List<PostModel> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ProfileImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProfileImageViewHolder(LayoutInflater.from(context).inflate(R.layout.post_image_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileImageViewHolder holder, int position) {
        PostModel post = posts.get(position);

        if (post.getPostImage() != null) {
            Glide.with(context)
                    .load(post.getPostImage())
                    .into(holder.binding.postImage);
        }

        if (post.getPostVideo() != null) {

        }
        holder.binding.postImage.setOnClickListener(v -> {
            Intent postDetailIntent = new Intent(context, PostDetailActivity.class);
            postDetailIntent.putExtra("postId", post.getId());
            context.startActivity(postDetailIntent);
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class ProfileImageViewHolder extends RecyclerView.ViewHolder {
        private PostImageItemBinding binding;
        public ProfileImageViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = PostImageItemBinding.bind(itemView);
        }
    }
}
