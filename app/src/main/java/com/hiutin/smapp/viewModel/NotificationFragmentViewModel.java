package com.hiutin.smapp.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.hiutin.smapp.data.model.NotificationModel;
import com.hiutin.smapp.data.model.UserModel;
import com.hiutin.smapp.data.repository.NotificationRepository;


import java.util.List;

public class NotificationFragmentViewModel extends AndroidViewModel {
    private final NotificationRepository notificationRepository;
    private MutableLiveData<List<NotificationModel>> notificationMutableLiveData;


    public NotificationFragmentViewModel(@NonNull Application application) {
        super(application);
        notificationRepository = new NotificationRepository();
    }

    public MutableLiveData<List<NotificationModel>> getAllNotification() {
        if (notificationMutableLiveData == null)
            notificationMutableLiveData = new MutableLiveData<>();
        notificationRepository.getNotification().observeForever(notification -> {
            notificationMutableLiveData.setValue(notification);
        });
        return notificationMutableLiveData;
    }
}
