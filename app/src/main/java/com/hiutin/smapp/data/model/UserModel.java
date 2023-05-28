package com.hiutin.smapp.data.model;

import java.util.ArrayList;

public class UserModel {
    private String uid, email, name, avatar, status, token;
    private ArrayList<String> followers;
    private ArrayList<String> following;

    public UserModel() {
    }

    public UserModel(String uid, String email, String name, String avatar, String status, ArrayList<String> followers, ArrayList<String> followings) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.avatar = avatar;
        this.status = status;
        this.followers = followers;
        this.following = followings;
    }

    public UserModel(String uid, String email, String name, String avatar, String status, String token, ArrayList<String> followers, ArrayList<String> following) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.avatar = avatar;
        this.status = status;
        this.token = token;
        this.followers = followers;
        this.following = following;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<String> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<String> followers) {
        this.followers = followers;
    }

    public ArrayList<String> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<String> followings) {
        this.following = followings;
    }
}
