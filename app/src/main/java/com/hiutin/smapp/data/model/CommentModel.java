package com.hiutin.smapp.data.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class CommentModel {
    private String id, uid , content;
    @ServerTimestamp
    private Date timestamp;

    public CommentModel() {
    }

    public CommentModel(String id, String uid, String content, Date timestamp) {
        this.id = id;
        this.uid = uid;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
