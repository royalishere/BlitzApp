package com.example.blitz.Models;

public class Message {
    String uId, message;
    Long timestamp;

    public Message(String uId, String message, Long timestamp) {
        this.uId = uId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public Message(String uId, String message) {
        this.uId = uId;
        this.message = message;
    }

    public Message() {
    }

    public String getuId() {
        return uId;
    }

    public String getMessage() {
        return message;
    }

    public void setTimestamp(long time) {
        this.timestamp = time;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
