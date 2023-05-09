package com.hiutin.smapp.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.hiutin.smapp.data.model.CommentModel;
import com.hiutin.smapp.data.model.PostModel;
import com.hiutin.smapp.data.repository.PostRepository;
import com.hiutin.smapp.data.repository.UserRepository;

import java.util.List;

public class PostDetailActivityViewModel extends AndroidViewModel {
    private final PostRepository postRepository;
    private UserRepository userRepository;
    private MutableLiveData<List<CommentModel>> commentsMutableLiveData;
    private MutableLiveData<PostModel> postMutableLiveData;

    public PostDetailActivityViewModel(@NonNull Application application) {
        super(application);
        postRepository = new PostRepository();
        userRepository = new UserRepository();
    }

    public MutableLiveData<List<CommentModel>> getCommentsMutableLiveData(String postId) {
        if (commentsMutableLiveData == null) {
            commentsMutableLiveData = new MutableLiveData<>();
            postRepository.getPostComments(postId).observeForever(postComments -> {
                if (postComments != null && postComments.size() > 0) {
                    commentsMutableLiveData.setValue(postComments);
                }
            });
        }
        return commentsMutableLiveData;
    }

    public MutableLiveData<PostModel> getPostMutableLiveData(String postId) {
        if (postMutableLiveData == null) {
            postMutableLiveData = new MutableLiveData<>();
            postRepository.getPostById(postId).observeForever(post -> {
                if (post != null) {
                    postMutableLiveData.setValue(post);
                }
            });
        }
        return postMutableLiveData;
    }

    public void addComment(String postId, CommentModel comment) {
        postRepository.addComment(postId, comment);
    }
}
