package com.hiutin.smapp.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hiutin.smapp.data.model.PostModel;
import com.hiutin.smapp.data.model.UserModel;
import com.hiutin.smapp.data.repository.PostRepository;
import com.hiutin.smapp.data.repository.UserRepository;

import java.util.List;

public class ProfileFragmentViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private MutableLiveData<UserModel> userMutableLiveData;
    private MutableLiveData<List<PostModel>> postsMutableLiveData;
    private MutableLiveData<String> userIdMutableLiveData;
    private MutableLiveData<Boolean> checkFollowingMutableData;
    private MutableLiveData<List<UserModel>> getAllFollowingUser;

    private MutableLiveData<List<UserModel>> getAllFollowerUser;
    public ProfileFragmentViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository();
        postRepository = new PostRepository();
    }

    public MutableLiveData<UserModel> getUserMutableLiveData(String uid) {
        if (userMutableLiveData == null) {
            userMutableLiveData = new MutableLiveData<>();
        }
        userRepository.getUserByUid(uid).observeForever(user -> userMutableLiveData.setValue(user));
        return userMutableLiveData;
    }

    public MutableLiveData<List<PostModel>> getPostsMutableLiveData(String uid) {
        if (postsMutableLiveData == null) {
            postsMutableLiveData = new MutableLiveData<>();
        }
        postRepository.getPostsByUid(uid).observeForever(posts -> {
            postsMutableLiveData.setValue(posts);
        });
        return postsMutableLiveData;
    }

    public MutableLiveData<String> getUserIdMutableLiveData() {
        if (userIdMutableLiveData == null) {
            userIdMutableLiveData = new MutableLiveData<>();
            userIdMutableLiveData.setValue(FirebaseAuth.getInstance().getUid());

        }
        return userIdMutableLiveData;
    }

    public MutableLiveData<Boolean> getCheckFollowingMutableData(String uid) {
        if (checkFollowingMutableData == null) {
            checkFollowingMutableData = new MutableLiveData<>();
        }
        userRepository.checkFollowing(uid).observeForever(check -> {
            checkFollowingMutableData.setValue(check);
        });
        return checkFollowingMutableData;
    }

    public void setUserId(String uid) {
        if (userIdMutableLiveData == null) {
            userIdMutableLiveData = new MutableLiveData<>();
        }
        userIdMutableLiveData.setValue(uid);
    }



    public void follow(String uid) {
        userRepository.follow(uid);
    }

    public void unfollow(String uid) {
        userRepository.unfollow(uid);
    }
    public MutableLiveData<List<UserModel>> getAllFollowingUser() {
        if(getAllFollowingUser == null){
            getAllFollowingUser = new MutableLiveData<>();
            userRepository.getUserByFollowing().observeForever(listUser ->{
                getAllFollowingUser.setValue(listUser);
            });
        }
        return getAllFollowingUser;
    }
    public MutableLiveData<List<UserModel>> getAllFollowerUser() {
        if(getAllFollowerUser == null){
            getAllFollowerUser = new MutableLiveData<>();
            userRepository.getUserByFollowers().observeForever(listUser ->{
                getAllFollowerUser.setValue(listUser);
            });
        }
        return getAllFollowerUser;
    }
}
