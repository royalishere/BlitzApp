package com.example.blitz.Models;

import java.util.HashMap;
import java.util.Map;

public class Users {

    String profilePicture, userName, mail, password, userId, lastMessage, status, address, mobile, token;
    Map<String, Boolean> friendList = new HashMap<>();
    public Users(String profilePicture, String userName, String mail, String password, String userId, String lastMessage, String status, String address, String mobile,String token) {
        this.profilePicture = profilePicture;
        this.userName = userName;
        this.mail = mail;
        this.password = password;
        this.userId = userId;
        this.lastMessage = lastMessage;
        this.status = status;
        this.address = address;
        this.mobile = mobile;
        this.token = token;
        this.friendList = friendList;
    }

    // Empty Constructor
    public Users() {
    }

    public Users(String userName, String mail, String password, String profilePicture, Map<String, Boolean> friendList) {
        this.userName = userName;
        this.mail = mail;
        this.password = password;
        this.profilePicture = profilePicture;
        this.friendList = friendList;
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

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
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

    public String getUserId() {
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

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String, Boolean> getFriendList() {
        return friendList;
    }

    public void addFriend(String friendId) {
        friendList.put(friendId, true);
    }

    public void removeFriend(String friendId) {
        friendList.remove(friendId);
    }
}