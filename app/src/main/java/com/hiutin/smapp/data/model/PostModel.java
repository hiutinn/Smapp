package com.hiutin.smapp.data.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;

public class PostModel {
    private String id, postImage, uid, caption, postVideo;
    @ServerTimestamp
    private Date timestamp;
    private ArrayList<String> likes;

    public PostModel() {
    }

    public PostModel(String id, String postImage, String uid, String caption, String postVideo, Date timestamp, ArrayList<String> likes) {
        this.id = id;
        this.postImage = postImage;
        this.uid = uid;
        this.caption = caption;
        this.postVideo = postVideo;
        this.timestamp = timestamp;
        this.likes = likes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<String> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getPostVideo() {
        return postVideo;
    }

    public void setPostVideo(String postVideo) {
        this.postVideo = postVideo;
    }
}
