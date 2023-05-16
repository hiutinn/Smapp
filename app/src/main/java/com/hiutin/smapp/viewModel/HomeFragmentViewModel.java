package com.hiutin.smapp.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hiutin.smapp.data.model.CommentModel;
import com.hiutin.smapp.data.model.PostModel;
import com.hiutin.smapp.data.repository.PostRepository;
import com.hiutin.smapp.data.repository.UserRepository;

import java.util.List;

public class HomeFragmentViewModel extends AndroidViewModel {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private MutableLiveData<List<PostModel>> postsLiveData;
    private MutableLiveData<List<CommentModel>> postCommentsLiveData;

    public HomeFragmentViewModel(@NonNull Application application) {
        super(application);
        postRepository = new PostRepository();
        userRepository = new UserRepository();
    }

    public LiveData<List<PostModel>> getPostsLiveData(String uid) {
        if (postsLiveData == null) {
            postsLiveData = new MutableLiveData<>();
        }
        loadPostsByFollowing(uid);
        return postsLiveData;
    }

    public void loadPostsByFollowing(String uid) {
        userRepository.getUserFollow(uid).observeForever(following -> {
            if (following != null && following.size() > 0) {
                postRepository.getPostsByFollowing(following).observeForever(postList -> {
                    if (postList != null && postList.size() > 0) {
                        postsLiveData.setValue(postList);
                    }
                });
            }
        });
    }

    public MutableLiveData<List<CommentModel>> getPostCommentsLiveData(String postId) {
        if (postCommentsLiveData == null) {
            postCommentsLiveData = new MutableLiveData<>();
        }
        loadPostComments(postId);
        return postCommentsLiveData;
    }

    private void loadPostComments(String postId) {
       postRepository.getPostComments(postId).observeForever(postComments -> {
           if (postComments != null && postComments.size() > 0) {
               postCommentsLiveData.setValue(postComments);
           }
       });
    }

    public void addPost(PostModel post) {
        postRepository.addPost(post);
    }

    public void addComment(String postId, CommentModel comment) {
        postRepository.addComment(postId, comment);
    }
}
