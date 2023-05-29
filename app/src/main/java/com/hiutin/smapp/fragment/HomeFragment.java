package com.hiutin.smapp.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hiutin.smapp.adapter.PostAdapter;
import com.hiutin.smapp.adapter.PostCommentAdapter;
import com.hiutin.smapp.data.model.NotificationModel;
import com.hiutin.smapp.data.repository.NotificationRepository;
import com.hiutin.smapp.databinding.FragmentHomeBinding;
import com.hiutin.smapp.data.model.CommentModel;
import com.hiutin.smapp.data.model.PostModel;
import com.hiutin.smapp.viewModel.HomeFragmentViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private PostAdapter adapter;
    private List<PostModel> posts;
    private String selectedPostId;
    private ArrayList<CommentModel> mComments;
    private PostCommentAdapter commentAdapter;
    public static BottomSheetBehavior<RelativeLayout> bottomSheetBehavior;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolBar);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        binding.postRecyclerView.setHasFixedSize(true);
        binding.postRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        posts = new ArrayList<>();
        adapter = new PostAdapter(requireContext(), posts);

        binding.postRecyclerView.setAdapter(adapter);

        HomeFragmentViewModel viewModel = new ViewModelProvider(this).get(HomeFragmentViewModel.class);
        viewModel.getPostsLiveData(user.getUid())
                .observe(getViewLifecycleOwner(), mPosts -> {
                    posts.clear();
                    posts.addAll(mPosts);
                    adapter.notifyDataSetChanged();
                });

        mComments = new ArrayList<>();
        commentAdapter = new PostCommentAdapter(requireContext(), mComments);
        binding.commentRecyclerView.setAdapter(commentAdapter);
        bottomSheetBehavior = BottomSheetBehavior.from(binding.layoutBottomSheet);
        adapter.onComment((postId) -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

            viewModel.getPostCommentsLiveData(postId).observe(getViewLifecycleOwner(),comments -> {
                mComments.clear();
                mComments.addAll(comments);
                commentAdapter.notifyDataSetChanged();
            });
            selectedPostId = postId;
        });

        binding.btnComment.setOnClickListener(v -> {
            if (binding.edtComment.getText().toString().isEmpty())
                return;

            CollectionReference commentRef = FirebaseFirestore.getInstance()
                    .collection("posts")
                    .document(selectedPostId)
                    .collection("comments");

            String id = commentRef.document().getId();
            CommentModel comment = new CommentModel();
            comment.setId(id);
            comment.setContent(binding.edtComment.getText().toString());
            comment.setUid(FirebaseAuth.getInstance().getUid());
            Timestamp timestamp = Timestamp.now();
            comment.setTimestamp(timestamp.toDate());

            viewModel.addComment(selectedPostId, comment);
            binding.edtComment.setText(null);
            NotificationRepository notificationRepository = new NotificationRepository();
            FirebaseFirestore.getInstance().collection("posts")
                    .document(selectedPostId)
                    .get()
                    .addOnCompleteListener(task -> {
                        List<String> listUser = new ArrayList<>();
                        listUser.add(task.getResult().get("uid").toString());

                        NotificationModel model = new NotificationModel(FirebaseAuth.getInstance().getUid(), "đã bình luận bài viết: "+task.getResult().get("caption").toString(), selectedPostId, listUser, timestamp);
                        notificationRepository.addNotification(getContext(),model,"đã bình luận bài viết ");

                    });
        });

        // Play video when it appear
        binding.postRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (layoutManager == null) return;
                // Get the first visible item position
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                // Get the last visible item position
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                // Iterate through the visible items and check if they are appearing for the first time
                for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
                    PostAdapter.PostViewHolder viewHolder = (PostAdapter.PostViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                    if (viewHolder != null && viewHolder.itemView.getTag() == null) {
                        // This item is appearing for the first time, do something with it
                        viewHolder.itemView.setTag(true);
                        // Your code to handle appearance event of the item
                        if (viewHolder.binding.vdPost.getVisibility() == View.VISIBLE)
                            viewHolder.binding.vdPost.start();
                    }
                }
            }
        });
    }


}