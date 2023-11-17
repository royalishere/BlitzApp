package com.example.blitz.Models;

public class Users {

    String profilePicture, userName, mail, password, userId, lastMessage, status;

    public Users(String profilePicture, String userName, String mail, String password, String userId, String lastMessage, String status) {
        this.profilePicture = profilePicture;
        this.userName = userName;
        this.mail = mail;
        this.password = password;
        this.userId = userId;
        this.lastMessage = lastMessage;
        this.status = status;
    }

    // Empty Constructor
    public Users() {
    }

    // Sign Up Constructor
    public Users(String userName, String mail, String password) {
        this.userName = userName;
        this.mail = mail;
        this.password = password;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }
    public String getProfilePic() {
        return profilePicture;
    }

    public void setProfilePic(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId(String key) {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
