package com.hiutin.smapp.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.hiutin.smapp.data.model.UserModel;
import com.hiutin.smapp.data.repository.UserRepository;

import java.util.List;

public class SearchFragmentViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private MutableLiveData<List<UserModel>> allUsersMutableLiveData;
    private MutableLiveData<List<UserModel>> searchResultMutableLiveData;
    private MutableLiveData<Boolean> isFollowing;
    public SearchFragmentViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository();
    }

    public MutableLiveData<List<UserModel>> getSearchResultMutableLiveData(String keyWord) {
        if (searchResultMutableLiveData == null) {
            searchResultMutableLiveData = new MutableLiveData<>();
        }
        if (keyWord.equals("")) {
            userRepository.getUsersOrderByFollowing().observeForever(users -> {
                searchResultMutableLiveData.setValue(users);
            });
        } else {
            userRepository.search(keyWord).observeForever(users -> {
                searchResultMutableLiveData.setValue(users);
            });
        }
        return searchResultMutableLiveData;
    }

    public MutableLiveData<List<UserModel>> getAllUsersMutableLiveData() {
        if (allUsersMutableLiveData == null)
            allUsersMutableLiveData = new MutableLiveData<>();
        userRepository.getUsersOrderByFollowing().observeForever(users -> {
            allUsersMutableLiveData.setValue(users);
        });
        return allUsersMutableLiveData;
    }

    public MutableLiveData<Boolean> getIsFollowing(String uid) {
        if (isFollowing == null) {
            isFollowing = new MutableLiveData<>();
        }
        userRepository.checkFollowing(uid).observeForever(check -> {
            isFollowing.setValue(check);
        });
        return isFollowing;
    }

    public void follow(String uid) {
        userRepository.follow(uid);
    }

    public void unfollow(String uid) {
        userRepository.unfollow(uid);
    }
}
