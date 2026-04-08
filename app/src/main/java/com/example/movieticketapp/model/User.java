package com.example.movieticketapp.model;

public class User {
    private String uid;
    private String email;
    private String fullName;
    private String profilePicUrl;

    public User() {}

    public User(String uid, String email, String fullName, String profilePicUrl) {
        this.uid = uid;
        this.email = email;
        this.fullName = fullName;
        this.profilePicUrl = profilePicUrl;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getProfilePicUrl() { return profilePicUrl; }
    public void setProfilePicUrl(String profilePicUrl) { this.profilePicUrl = profilePicUrl; }
}
