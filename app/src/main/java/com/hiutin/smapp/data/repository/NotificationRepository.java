package com.hiutin.smapp.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.RemoteMessage;
import com.hiutin.smapp.data.model.NotificationModel;
import com.hiutin.smapp.data.model.UserModel;
import com.hiutin.smapp.notification.fcm.FMCSend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationRepository {

    private static final String COLLECTION_NAME = "notifications";
    private FirebaseFirestore db;

    public NotificationRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void addNotification(Context context,NotificationModel model,String content) {

        Map<String, Object> data = new HashMap<>();
        data.put("id", "");
        data.put("content", model.getContent());
        data.put("idPost", model.getIdPost());
        data.put("uidA", model.getUidA());
        data.put("uidB", model.getUidB());
        data.put("timestamp", model.getTimestamp());
        db.collection(COLLECTION_NAME).add(data)
                .addOnSuccessListener(documentReference -> {
                    documentReference.update("id", documentReference.getId())
                                    .addOnSuccessListener(runnable -> {
                                            sendNotification(context,content,documentReference.getId());
                                    });
                });
    }

    public void sendNotification(Context context, String content,String id) {
        db.collection(COLLECTION_NAME)
                .document(id)
                .get()
                .addOnCompleteListener(documentSnapshot -> {
                    NotificationModel notificationModel = documentSnapshot.getResult().toObject(NotificationModel.class);
                    db.collection("users")
                            .document(FirebaseAuth.getInstance().getUid())
                            .addSnapshotListener((value, error) -> {
                                if (value != null && value.exists()) {
                                    List<String> followers = (ArrayList<String>) notificationModel.getUidB();
                                    Log.e("size",followers.size()+"");
                                    if (followers != null && !followers.isEmpty()) {
                                        db.collection("users")
                                                .whereIn("uid", followers)
                                                .get()
                                                .addOnCompleteListener(queryTask -> {
                                                    if (queryTask.isSuccessful()) {
                                                        for (DocumentSnapshot snapshot : queryTask.getResult().getDocuments()) {
                                                            UserModel userModel = snapshot.toObject(UserModel.class);
                                                            FMCSend.pushNotification(context, userModel.getToken(), "Bạn có thông báo mới", notificationModel.getIdPost() + "-" + value.get("name").toString() + " " + content);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            });
                });
    }

    public LiveData<List<NotificationModel>> getNotification() {
        MutableLiveData<List<NotificationModel>> listMutableLiveData = new MutableLiveData<>();
        db.collection(COLLECTION_NAME)
                .whereArrayContains("uidB", FirebaseAuth.getInstance().getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<NotificationModel> notificationModels = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            NotificationModel model = documentSnapshot.toObject(NotificationModel.class);
                            notificationModels.add(model);
                        }
                        listMutableLiveData.setValue(notificationModels);
                    }
                });
        return listMutableLiveData;
    }


}
