package com.hiutin.smapp.adapter;

import static com.hiutin.smapp.utils.TimeHelper.getTime;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hiutin.smapp.R;
import com.hiutin.smapp.data.model.NotificationModel;
import com.hiutin.smapp.data.repository.NotificationRepository;
import com.hiutin.smapp.databinding.PostItemBinding;
import com.hiutin.smapp.data.model.PostModel;
import com.hiutin.smapp.data.model.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private final Context context;
    private final List<PostModel> posts;
    private IOnCommentClick iOnCommentClick;
    NotificationModel model;

    public PostAdapter(Context context, List<PostModel> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(LayoutInflater.from(context).inflate(R.layout.post_item, parent, false));
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {

        PostModel post = posts.get(position);

        DocumentReference reference = FirebaseFirestore.getInstance().collection("users").document(post.getUid());
        DocumentReference postRef = FirebaseFirestore.getInstance().collection("posts").document(post.getId());
        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UserModel userModel = task.getResult().toObject(UserModel.class);
                assert userModel != null;
                Glide
                        .with(context)
                        .load(userModel.getAvatar())
                        .centerCrop()
                        .timeout(7000)
                        .placeholder(R.drawable.user)
                        .into(holder.binding.imgUserAvatar);
                holder.binding.tvUserName.setText(userModel.getName());
            } else {
                Toast.makeText(context, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.binding.tvCaption.setText(post.getCaption());
        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        if (post.getPostImage() != null) {
            holder.binding.vdPost.setVisibility(View.GONE);
            holder.binding.imgPost.setVisibility(View.VISIBLE);
            Glide
                    .with(context)
                    .load(post.getPostImage())
                    .centerCrop()
                    .timeout(7000)
                    .placeholder(new ColorDrawable(color))
                    .into(holder.binding.imgPost);
        }

        if (post.getPostVideo() != null) {
            holder.binding.imgPost.setVisibility(View.GONE);
            holder.binding.vdPost.setVisibility(View.VISIBLE);
            MediaController mediaController = new MediaController(context);
            mediaController.setAnchorView(holder.binding.vdPost);
            holder.binding.vdPost.setVideoPath(post.getPostVideo());
            holder.binding.vdPost.setMediaController(mediaController);
        }

        holder.binding.tvTime.setText(getTime(post.getTimestamp()));
        // Handle like button and text
        ArrayList<String> likes = post.getLikes();
        if (likes.size() == 0) {
            holder.binding.tvLikeCount.setText("");
        } else if (likes.size() == 1) {
            holder.binding.tvLikeCount.setText(likes.size() + " like");
        } else {
            holder.binding.tvLikeCount.setText(likes.size() + " likes");
        }

        boolean liked = false;
        for (String uid : likes) {
            if (uid.equals(FirebaseAuth.getInstance().getUid())) {
                liked = true;
                break;
            }
        }
        if (liked) {
            holder.binding.btnLike.setBackgroundResource(R.drawable.heart_red);
            holder.binding.btnLike.setOnClickListener(v -> {
                likes.remove(FirebaseAuth.getInstance().getUid());
                postRef.update("likes", likes);
                notifyDataSetChanged();
            });
        } else {
            holder.binding.btnLike.setBackgroundResource(R.drawable.heart);
            holder.binding.btnLike.setOnClickListener(v -> {
                likes.add(FirebaseAuth.getInstance().getUid());
                postRef.update("likes", likes);
                notifyDataSetChanged();
                NotificationRepository notificationRepository = new NotificationRepository();
                List<String> listUser = new ArrayList<>();
                listUser.add(post.getUid());
                Timestamp timestamp = Timestamp.now();
                model = new NotificationModel(FirebaseAuth.getInstance().getUid(), "đã like bài viết: "+holder.binding.tvCaption.getText(), post.getId(), listUser, timestamp);
                notificationRepository.addNotification(context,model,"đã like bài viết của bạn");
            });
        }

        holder.binding.btnComment.setOnClickListener(v -> {
            iOnCommentClick.onComment(post.getId());
        });

//        holder.itemView.setOnClickListener(v -> {
//            Intent postDetailIntent = new Intent(context, PostDetailActivity.class);
//            postDetailIntent.putExtra("postId", post.getId());
//            context.startActivity(postDetailIntent);
//        });
    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        public PostItemBinding binding;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = PostItemBinding.bind(itemView);
        }
    }

    public interface IOnCommentClick {
        void onComment(String postId);
    }

    public void onComment(IOnCommentClick iOnCommentClick) {
        this.iOnCommentClick = iOnCommentClick;
    }
}
