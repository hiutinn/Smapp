package com.hiutin.smapp.adapter;

import static com.hiutin.smapp.utils.TimeHelper.getTime;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hiutin.smapp.R;
import com.hiutin.smapp.databinding.CommentItemBinding;
import com.hiutin.smapp.data.model.CommentModel;

import java.util.ArrayList;

public class PostCommentAdapter extends RecyclerView.Adapter<PostCommentAdapter.PostCommentViewHolder> {
    private final Context context;
    private final ArrayList<CommentModel> comments;

    public PostCommentAdapter(Context context, ArrayList<CommentModel> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public PostCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostCommentViewHolder(LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostCommentViewHolder holder, int position) {
        CommentModel comment = comments.get(position);
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(comment.getUid())
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;

                    assert value != null;
                    Glide.with(context)
                            .load(value.getString("avatar"))
                            .placeholder(R.drawable.user)
                            .into( holder.binding.imgUserAvatar);
                    holder.binding.tvUserName.setText(value.getString("name"));
                });

        holder.binding.tvTime.setText(getTime(comment.getTimestamp()));
        holder.binding.tvContent.setText(comment.getContent());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }


    class PostCommentViewHolder extends RecyclerView.ViewHolder {
        private final CommentItemBinding binding;
        public PostCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CommentItemBinding.bind(itemView);
        }
    }
}
