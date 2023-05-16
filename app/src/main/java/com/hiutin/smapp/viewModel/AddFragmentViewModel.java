package com.hiutin.smapp.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.hiutin.smapp.data.model.PostModel;
import com.hiutin.smapp.data.model.UserModel;
import com.hiutin.smapp.data.repository.PostRepository;
import com.hiutin.smapp.data.repository.UserRepository;

public class AddFragmentViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    private PostRepository postRepository;
    private MutableLiveData<UserModel> currentUserLiveData;
    public AddFragmentViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository();
        postRepository = new PostRepository();
    }


    public MutableLiveData<UserModel> getCurrentUserLiveData() {
        if (currentUserLiveData == null)
            currentUserLiveData = new MutableLiveData<>();
        userRepository.getUserByUid(FirebaseAuth.getInstance().getUid()).observeForever(user -> {
            currentUserLiveData.setValue(user);
        });
        return currentUserLiveData;
    }

    public void addPost(PostModel post) {
        postRepository.addPost(post);
    }
}
