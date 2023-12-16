package com.example.blitz.Models;

public class Message {
    String uId, message,type;
    Long timestamp;

    public Message(String uId, String message, Long timestamp, String type) {
        this.uId = uId;
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
    }

    public Message(String uId, String message) {
        this.uId = uId;
        this.message = message;
    }

    public Message() {
    }

//    public String getuId() {
//        return uId;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setTimestamp(long time) {
//        this.timestamp = time;
//    }
//
//    public long getTimestamp() {
//        return timestamp;
//    }


    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
