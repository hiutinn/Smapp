package com.hiutin.smapp;

import static com.hiutin.smapp.utils.TimeHelper.getTime;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hiutin.smapp.adapter.PostCommentAdapter;
import com.hiutin.smapp.data.model.NotificationModel;
import com.hiutin.smapp.data.model.PostModel;
import com.hiutin.smapp.data.repository.NotificationRepository;
import com.hiutin.smapp.databinding.ActivityPostDetailBinding;
import com.hiutin.smapp.data.model.CommentModel;
import com.hiutin.smapp.dialog.ConfirmDialog;
import com.hiutin.smapp.viewModel.PostDetailActivityViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class PostDetailActivity extends AppCompatActivity {
    private ActivityPostDetailBinding binding;
    private String postId;
    private ArrayList<CommentModel> comments;
    private PostCommentAdapter commentAdapter;
    private PostDetailActivityViewModel viewModel;
    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        hideStatusBar();
        setSupportActionBar(binding.toolBar);
        viewModel = new ViewModelProvider(this).get(PostDetailActivityViewModel.class);
        postId = getIntent().getStringExtra("postId");
        viewModel.getPostMutableLiveData(postId).observe(this, this::loadPostInformation);
        binding.btnSendComment.setOnClickListener(v -> {
            addComment();
            NotificationRepository notificationRepository = new NotificationRepository();
            FirebaseFirestore.getInstance().collection("posts")
                    .document(postId)
                    .get()
                    .addOnCompleteListener(task -> {
                        List<String> listUser = new ArrayList<>();
                        listUser.add(Objects.requireNonNull(task.getResult().get("uid")).toString());
                        Timestamp timestamp = Timestamp.now();
                        NotificationModel model = new NotificationModel(FirebaseAuth.getInstance().getUid(), "đã bình luận bài viết: ", postId, listUser, timestamp);
                        notificationRepository.addNotification(getApplication(),model,"đã bình luận bài viết ");
                    });
        });

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
        binding.btnOption.setOnClickListener(this::showPopUpMenu);

    }

    private void showPopUpMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.inflate(R.menu.post_detail_menu);
        popup.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getItemId() == R.id.post_detail_delete) {
                deletePost();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void deletePost() {
        new ConfirmDialog(this, "Are you sure ?", () -> {
            viewModel.deletePost(postId);
            Toast.makeText(PostDetailActivity.this, "Post has been deleted", Toast.LENGTH_SHORT).show();
            finish();
        }).show();
    }

    private void addComment() {
        if (binding.edtComment.getText().toString().isEmpty()) return;

        CollectionReference commentRef = FirebaseFirestore.getInstance()
                .collection("posts")
                .document(postId)
                .collection("comments");

        String id = commentRef.document().getId();
        CommentModel comment = new CommentModel();
        comment.setId(id);
        comment.setContent(binding.edtComment.getText().toString());
        comment.setUid(FirebaseAuth.getInstance().getUid());
        Timestamp timestamp = Timestamp.now();
        comment.setTimestamp(timestamp.toDate());

        viewModel.addComment(postId, comment);
        binding.edtComment.setText("");
    }

    @SuppressLint("SetTextI18n")
    private void loadPostInformation(PostModel post) {
        viewModel.getUserMutableLiveData(post.getUid()).observe(this, user -> {
            binding.tvUserName.setText(user.getName());
            Glide.with(this)
                    .load(user.getAvatar())
                    .placeholder(R.drawable.user)
                    .into(binding.imgUserAvatar);
            if (user.getUid().equals(FirebaseAuth.getInstance().getUid())) {
                binding.btnOption.setVisibility(View.VISIBLE);
            }
        });
        // Time
        binding.tvTime.setText(getTime(post.getTimestamp()));
        // Post image
        if (post.getPostImage() != null) {
            binding.vdPost.setVisibility(View.GONE);
            binding.imgPost.setVisibility(View.VISIBLE);
            Random random = new Random();
            int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
            Glide
                    .with(this)
                    .load(post.getPostImage())
                    .centerCrop()
                    .timeout(7000)
                    .placeholder(new ColorDrawable(color))
                    .into(binding.imgPost);
        }

        if (post.getPostVideo() != null) {
            binding.imgPost.setVisibility(View.GONE);
            binding.vdPost.setVisibility(View.VISIBLE);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(binding.vdPost);
            binding.vdPost.setVideoPath(post.getPostVideo());
            binding.vdPost.setMediaController(mediaController);
        }
        // Set caption text
        binding.tvCaption.setText(post.getCaption());
        // Handle like button
        ArrayList<String> likes = post.getLikes();
        assert likes != null;
        if (likes.size() == 0) {
            binding.tvLikeCount.setText("");
        } else if (likes.size() == 1) {
            binding.tvLikeCount.setText(likes.size() + " like");
        } else {
            binding.tvLikeCount.setText(likes.size() + " likes");
        }

        boolean liked = false;
        for (String likedUid : likes) {
            if (likedUid.equals(FirebaseAuth.getInstance().getUid())) {
                liked = true;
                break;
            }
        }
        if (liked) {
            binding.btnLike.setBackgroundResource(R.drawable.heart_red);
            binding.btnLike.setOnClickListener(v -> {
                likes.remove(FirebaseAuth.getInstance().getUid());
                FirebaseFirestore.getInstance()
                        .collection("posts")
                        .document(postId)
                        .update("likes", likes);
            });
        } else {
            binding.btnLike.setBackgroundResource(R.drawable.heart);
            binding.btnLike.setOnClickListener(v -> {
                likes.add(FirebaseAuth.getInstance().getUid());
                FirebaseFirestore.getInstance()
                        .collection("posts")
                        .document(postId)
                        .update("likes", likes);
            });
        }

        loadPostComments(post.getId());
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadPostComments(String id) {
        comments = new ArrayList<>();
        commentAdapter = new PostCommentAdapter(this,comments);
        binding.commentRecyclerView.setAdapter(commentAdapter);
        viewModel.getCommentsMutableLiveData(id).observe(this, mComments -> {
            comments.clear();
            comments.addAll(mComments);
            commentAdapter.notifyDataSetChanged();
        });
    }

    public void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}