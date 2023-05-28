package com.hiutin.smapp.data.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.List;

public class NotificationModel {
    private String id,uidA,content,idPost;
    private List<String> uidB;

    @ServerTimestamp
    private Timestamp timestamp;

    public NotificationModel() {
    }

    public NotificationModel(String id, String uidA, String content, String idPost, List<String> uidB, Timestamp timestamp) {
        this.id = id;
        this.uidA = uidA;
        this.content = content;
        this.idPost = idPost;
        this.uidB = uidB;
        this.timestamp = timestamp;
    }

    public NotificationModel(String uidA, String content, String idPost, List<String> uidB, Timestamp timestamp) {
        this.uidA = uidA;
        this.content = content;
        this.idPost = idPost;
        this.uidB = uidB;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUidA() {
        return uidA;
    }

    public void setUidA(String uidA) {
        this.uidA = uidA;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public List<String> getUidB() {
        return uidB;
    }

    public void setUidB(List<String> uidB) {
        this.uidB = uidB;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
