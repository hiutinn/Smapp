package com.hiutin.smapp.data.repository;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.hiutin.smapp.data.model.CommentModel;
import com.hiutin.smapp.data.model.PostModel;

import java.util.ArrayList;
import java.util.List;

public class PostRepository {
    private static final String COLLECTION_NAME = "posts";
    private FirebaseFirestore db;

    public PostRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public LiveData<List<PostModel>> getPosts() {
        MutableLiveData<List<PostModel>> postsLiveData = new MutableLiveData<>();
        db.collection(COLLECTION_NAME).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("PostModelRepository", "Error fetching posts", error);
                return;
            }

            List<PostModel> posts = new ArrayList<>();
            for (DocumentSnapshot document : value.getDocuments()) {
                PostModel post = document.toObject(PostModel.class);
                posts.add(post);
            }
            postsLiveData.setValue(posts);
        });
        return postsLiveData;
    }

    public LiveData<PostModel> getPostById(String postId) {
        MutableLiveData<PostModel> postLiveData = new MutableLiveData<>();

        db.collection(COLLECTION_NAME)
                .document(postId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("PostModelRepository", "Error fetching post", error);
                        return;
                    }
                    assert value != null;
                    PostModel post = value.toObject(PostModel.class);
                    postLiveData.setValue(post);
                });

        return postLiveData;
    }

    public LiveData<List<PostModel>> getPostsByFollowing(List<String> following) {
        MutableLiveData<List<PostModel>> postsLiveData = new MutableLiveData<>();

        db.collection(COLLECTION_NAME)
                .whereIn("uid", following)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<PostModel> postList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        PostModel post = document.toObject(PostModel.class);
                        postList.add(post);
                    }
                    postsLiveData.setValue(postList);
                });

        return postsLiveData;
    }

    public LiveData<List<PostModel>> getPostsByUid(String uid) {
        MutableLiveData<List<PostModel>> postsLiveData = new MutableLiveData<>();

        db.collection(COLLECTION_NAME)
                .whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<PostModel> postList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        PostModel post = document.toObject(PostModel.class);
                        postList.add(post);
                    }
                    postsLiveData.setValue(postList);
                });

        return postsLiveData;
    }

    public void addPost(PostModel post) {
        db.collection(COLLECTION_NAME).document(post.getId()).set(post)
                .addOnSuccessListener(aVoid -> Log.d("PostRepository", "Post added successfully"))
                .addOnFailureListener(e -> Log.e("PostRepository", "Error adding post", e));
    }

    public void updatePost(PostModel post) {
        db.collection(COLLECTION_NAME).document(post.getId()).set(post, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d("PostRepository", "Post updated successfully"))
                .addOnFailureListener(e -> Log.e("PostRepository", "Error updating post", e));
    }

    public void deletePost(String postId) {
        db.collection(COLLECTION_NAME).document(postId).delete()
                .addOnSuccessListener(aVoid -> Log.d("PostRepository", "Post deleted successfully"))
                .addOnFailureListener(e -> Log.e("PostRepository", "Error deleting post", e));
    }

    public LiveData<List<CommentModel>> getPostComments(String postId) {
        MutableLiveData<List<CommentModel>> commentsMutableLiveData = new MutableLiveData<>();

        db.collection(COLLECTION_NAME)
                .document(postId)
                .collection("comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("PostRepository", "Error fetching comments", error);
                        return;
                    }

                    List<CommentModel> comments = new ArrayList<>();
                    assert value != null;
                    for (DocumentSnapshot document : value.getDocuments()) {
                        CommentModel comment = document.toObject(CommentModel.class);
                        comments.add(comment);
                    }

                    commentsMutableLiveData.setValue(comments);
                });

        return commentsMutableLiveData;
    }

    public void addComment(String postId, CommentModel comment) {
        db.collection(COLLECTION_NAME)
                .document(postId)
                .collection("comments")
                .document(comment.getId())
                .set(comment)
                .addOnSuccessListener(aVoid -> Log.d("PostRepository", "Comment added successfully"))
                .addOnFailureListener(e -> Log.e("PostRepository", "Error adding comment", e));
    }
}
