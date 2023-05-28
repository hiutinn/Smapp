package com.hiutin.smapp.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.hiutin.smapp.data.model.UserModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserRepository {
    private static final String COLLECTION_NAME = "users";
    private final FirebaseFirestore db;

    public UserRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    // lấy user
    public LiveData<UserModel> getUserByUid(String uid) {
        MutableLiveData<UserModel> userMutableLiveData = new MutableLiveData<>();

        db.collection(COLLECTION_NAME)
                .document(uid)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("UserRepository", "Error fetching users", error);
                        return;
                    }

                    assert value != null;

                    UserModel user = new UserModel();
                    user.setUid(value.getString("uid"));
                    user.setEmail(value.getString("email"));
                    user.setAvatar(value.getString("avatar"));
                    user.setStatus(value.getString("status"));
                    user.setName(value.getString("name"));
                    user.setFollowers((ArrayList<String>) value.get("followers"));
                    user.setFollowing((ArrayList<String>) value.get("following"));
                    userMutableLiveData.setValue(user);
                });

        return userMutableLiveData;
    }

    // lấy user follow
    public LiveData<List<String>> getUserFollow(String uid) {
        MutableLiveData<List<String>> followingMutableLiveData = new MutableLiveData<>();

        // Truy xuất dữ liệu từ Firestore
        db.collection(COLLECTION_NAME)
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> following = (List<String>) documentSnapshot.get("following");
                        followingMutableLiveData.setValue(following);
                    }
                });

        return followingMutableLiveData;
    }

    // check user following
    public LiveData<Boolean> checkFollowing(String uid) {
        MutableLiveData<Boolean> followingMutableLiveData = new MutableLiveData<>();
        db.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("UserRepository", "Error fetching users", error);
                        return;
                    }
                    boolean check = false;

                    ArrayList<String> following = (ArrayList<String>) value.get("following");
                    if (following == null) following = new ArrayList<>();
                    for (String followingUid : following) {
                        if (followingUid.equals(uid)) {
                            check = true;
                            break;
                        }
                    }

                    followingMutableLiveData.setValue(check);
                });

        return followingMutableLiveData;
    }

    //sắp xếp user by following
    public LiveData<List<UserModel>> getUsersOrderByFollowing() {
        MutableLiveData<List<UserModel>> usersMutableLiveData = new MutableLiveData<>();
        db.collection(COLLECTION_NAME)
                .whereNotIn("uid", Collections.singletonList(FirebaseAuth.getInstance().getUid()))
                .orderBy("uid")
                .orderBy("following")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<UserModel> users = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            UserModel userModel = documentSnapshot.toObject(UserModel.class);
                            users.add(userModel);
                        }
                        usersMutableLiveData.setValue(users);
                    }
                });
        return usersMutableLiveData;
    }

    //tìm kiếm user
    public LiveData<List<UserModel>> search(String keyWord) {
        MutableLiveData<List<UserModel>> usersMutableLiveData = new MutableLiveData<>();
        FirebaseFirestore.getInstance()
                .collection("users")
                .orderBy("name")
                .startAt(keyWord)
                .endAt(keyWord + "\uf8ff")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<UserModel> users = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            UserModel userModel = documentSnapshot.toObject(UserModel.class);
                            if (userModel.getUid().equals(FirebaseAuth.getInstance().getUid()))
                                continue;
                            users.add(userModel);
                        }
                        usersMutableLiveData.setValue(users);
                    }
                });
        return usersMutableLiveData;
    }

    // follow user
    public void follow(String uid) {
        db.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserModel userModel = task.getResult().toObject(UserModel.class);
                        assert userModel != null;
                        ArrayList<String> following = userModel.getFollowing();
                        if (following == null)
                            following = new ArrayList<>();
                        if (!following.contains(uid))
                            following.add(uid);
                        db.collection("users")
                                .document(FirebaseAuth.getInstance().getUid())
                                .update("following", following)
                                .addOnSuccessListener(unused -> Log.e("UserRepository", "Following successful"));
                    } else {
                        Log.e("UserRepository", "Error fetching users", task.getException());
                    }
                });
    }

    public void unfollow(String uid) {
        db.collection("users")
                .document(FirebaseAuth.getInstance().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserModel userModel = task.getResult().toObject(UserModel.class);
                        assert userModel != null;
                        ArrayList<String> following = userModel.getFollowing();
                        for (String followingUid : following) {
                            if (followingUid.equals(uid)) {
                                following.remove(uid);
                                break;
                            }
                        }
                        db.collection("users")
                                .document(FirebaseAuth.getInstance().getUid())
                                .update("following", following)
                                .addOnSuccessListener(unused -> Log.e("UserRepository", "Unfollowing successful"));
                    } else {
                        Log.e("UserRepository", "Error fetching users", task.getException());
                    }
                });
    }

    public LiveData<List<UserModel>> getUserByFollowing() {
        MutableLiveData<List<UserModel>> usersMutableLiveData = new MutableLiveData<>();
        db.collection(COLLECTION_NAME)
                .document(FirebaseAuth.getInstance().getUid())
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e("UserRepository", "Error fetching users" + e.getMessage());
                        return;
                    }
                    List<String> following = (ArrayList<String>) snapshot.get("following");
                    if (following != null && !following.isEmpty()) {
                        db.collection(COLLECTION_NAME)
                                .whereIn("uid", following)
                                .get()
                                .addOnCompleteListener(queryTask -> {
                                    if (queryTask.isSuccessful()) {
                                        List<UserModel> users = new ArrayList<>();
                                        for (DocumentSnapshot documentSnapshot : queryTask.getResult().getDocuments()) {
                                            UserModel userModel = documentSnapshot.toObject(UserModel.class);
                                            users.add(userModel);
                                        }
                                        usersMutableLiveData.setValue(users);

                                    }
                                });
                    }
                });
        return usersMutableLiveData;
    }

    public LiveData<List<UserModel>> getUserByFollowers() {
        MutableLiveData<List<UserModel>> usersMutableLiveData = new MutableLiveData<>();
        DocumentReference documentRef = db.collection(COLLECTION_NAME).document(FirebaseAuth.getInstance().getUid());
        documentRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.e("UserRepository", "Error fetching users" + e.getMessage());
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                List<String> followers = (ArrayList<String>) snapshot.get("followers");
                if (followers != null && !followers.isEmpty()) {
                    db.collection(COLLECTION_NAME)
                            .whereIn("uid", followers)
                            .get()
                            .addOnCompleteListener(queryTask -> {
                                if (queryTask.isSuccessful()) {
                                    List<UserModel> users = new ArrayList<>();
                                    for (DocumentSnapshot documentSnapshot : queryTask.getResult().getDocuments()) {
                                        UserModel userModel = documentSnapshot.toObject(UserModel.class);
                                        users.add(userModel);
                                    }
                                    usersMutableLiveData.setValue(users);

                                }
                            });
                }
            }

        });
        return usersMutableLiveData;
    }

}
