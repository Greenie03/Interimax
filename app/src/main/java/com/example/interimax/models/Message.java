package com.example.interimax.models;

public class Message {
    private String sender;
    private String receiver;
    private String content;
    private long time;
    private String type;

    public Message() {
        // Constructeur vide requis pour Firebase
    }

    public Message(String sender, String content, long time, String type, String receiver) {
        this.sender = sender;
        this.content = content;
        this.time = time;
        this.type = type;
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
