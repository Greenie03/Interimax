package com.example.interimax.models;

public class Message {
    private String sender;
    private String content;
    private String time;
    private String type;

    public Message() {
        // Constructeur vide requis pour Firebase
    }

    public Message(String sender, String content, String time, String type) {
        this.sender = sender;
        this.content = content;
        this.time = time;
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
